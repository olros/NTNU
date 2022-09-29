#include <iostream>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include "Workers.hpp"
#include "stuntypes.h"
#include "ResponseBuilder.hpp"
#include <errno.h>
#include <openssl/ssl.h>
#include <openssl/err.h>


#define BUFFER_SIZE 256
#define BACKLOG 5
#define ERROR_CODE 400
//Added enum for future possibilities of implementing TLS
enum SocketType {
    UDP, TCP, TLS
};


class Server {
private:
    int port;
    Workers *event_loop;
    bool keep_going;
    int socket_fd;
    SocketType socket_type;
    struct addrinfo *result;
    SSL_CTX *context;


    bool init_listening_socket();

    bool handle_udp(ResponseBuilder &builder, sockaddr_in &client, socklen_t &length);

    bool handle_tcp(ResponseBuilder &builder,sockaddr_in &client , socklen_t &length);

    bool handle_tls(ResponseBuilder &builder,sockaddr_in &client, socklen_t &length);

    bool init_tls();

    void cleanup_tls();
public:
    Server();

    Server(int socket_port, SocketType sock_type);

    bool start_server();

    void close_server();

    ~Server();
};

Server::Server() {}

Server::Server(int socket_port, SocketType sock_type) {
    this->port = socket_port;
    this->event_loop = new Workers(1);
    this->socket_type = sock_type;
}

bool Server::init_listening_socket() {
    this->socket_fd = socket(AF_INET, this->socket_type == UDP ? SOCK_DGRAM : SOCK_STREAM, 0);
    if (this->socket_fd == -1){
        std::cerr << "socket() failed: " << strerror(this->socket_fd) << std::endl;
        return false;
    }
    struct sockaddr_in sock_addr;
    sock_addr.sin_addr.s_addr = INADDR_ANY;
    sock_addr.sin_port = htons(port);
    sock_addr.sin_family = AF_INET;

    int exit_code;
    if((exit_code = bind(socket_fd, (struct sockaddr*)(&sock_addr), sizeof(sock_addr))) < 0){
        std::cerr << "bind() failed with exit code: " << strerror(exit_code) << std::endl;
        close(socket_fd);
        return false;
    }
    return true;
}

bool Server::handle_udp(ResponseBuilder &builder, sockaddr_in &client, socklen_t &length) {
    unsigned char buffer[BUFFER_SIZE];
    bool is_error = false;
    int n = recvfrom(socket_fd, buffer, sizeof(buffer),
                     MSG_WAITALL, (struct sockaddr *) (&client), &length);
    if (n == -1) {
        std::cerr << "recvfrom() failed: " << strerror(n) << std::endl;
        return false;
    }
    event_loop->post_after([&client, this, &buffer, &builder, &is_error] {
        if ( is_error || builder.is_error())
            sendto(this->socket_fd, builder.build_error_response(ERROR_CODE, "Something went wrong!?").get_response(),
                   sizeof(struct StunErrorResponse), MSG_CONFIRM,
                   (const struct sockaddr *) &client, sizeof(client));

        else
            sendto(this->socket_fd, builder.build_success_response().get_response(), sizeof(struct STUNResponseIPV4),
                   MSG_CONFIRM, (const struct sockaddr *) &client, sizeof(client));
    }, [&builder, &buffer, &client, &is_error, &n] {
        builder = ResponseBuilder(true, (STUNIncomingHeader *) buffer, client);
        is_error = ((buffer[0] >> 6) & 3) != 0 || n < 20;
    });
    return true;
}

bool Server::handle_tcp(ResponseBuilder &builder, struct sockaddr_in &client, socklen_t &length) {
    unsigned char buffer[BUFFER_SIZE];
    bool is_error = false;
    int client_socket_fd = accept(socket_fd, (struct sockaddr *) &client, &length);
    if (client_socket_fd == -1) return false;
    event_loop->post_after([&builder, &client_socket_fd, &is_error]{
        if (is_error || builder.is_error())
            send(client_socket_fd, builder.build_error_response(ERROR_CODE, "Something went wrong!?").get_response(),
                   sizeof(struct StunErrorResponse), MSG_CONFIRM);
        else
            send(client_socket_fd, builder.build_success_response().get_response(), sizeof(struct STUNResponseIPV4),
                   MSG_CONFIRM);
        close(client_socket_fd);
    }, [&builder, &client_socket_fd, &buffer, &is_error, client]{
        int n = recv(client_socket_fd, buffer, BUFFER_SIZE,0);
        if(n == -1) std::cerr << "recv() failed: " << n << std::endl;
        builder = ResponseBuilder(true, (STUNIncomingHeader *) buffer, client);
        is_error = ((buffer[0] >> 6) & 3) != 0 || n < 20;
    });

    return true;
}

bool Server::handle_tls(ResponseBuilder &builder, sockaddr_in &client, socklen_t &length) {
    unsigned char buffer[BUFFER_SIZE];
    bool is_error = false;
    bool is_SSL_error = false;
    int client_socket_fd = accept(socket_fd, (struct sockaddr *) &client, &length);
    if (client_socket_fd == -1) return false;
    SSL *ssl;
    event_loop->post_after([&ssl, &builder, &client_socket_fd, &is_error, &is_SSL_error]{
        if(is_SSL_error)
            send(client_socket_fd, builder.build_error_response(ERROR_CODE, "TLS connection could not be established.").get_response(),
                 sizeof(struct StunErrorResponse), MSG_CONFIRM);
        else if (is_error || builder.is_error())
            SSL_write(ssl, builder.build_error_response(ERROR_CODE, "Something went wrong!?").get_response(), sizeof(struct StunErrorResponse));
        else
            SSL_write(ssl, builder.build_success_response().get_response(), sizeof(struct STUNResponseIPV4));
        SSL_shutdown(ssl);
        SSL_free(ssl);
        close(client_socket_fd);
    }, [this, &ssl, &builder, &client_socket_fd, &buffer, &is_error, &is_SSL_error, client]{
        ssl = SSL_new(this->context);
        SSL_set_fd(ssl, client_socket_fd);
        if(SSL_accept(ssl) <= 0) is_SSL_error = true;
        int n = SSL_read(ssl, buffer, BUFFER_SIZE);
        if(n == -1) std::cerr << "SSL read failed: " << n << std::endl;
        builder = ResponseBuilder(true, (STUNIncomingHeader *) buffer, client);
        is_error = ((buffer[0] >> 6) & 3) != 0 || n < 20;
    });
    return true;
}

bool Server::init_tls() {
    int error_code;

    SSL_library_init();
    SSL_load_error_strings();
    OpenSSL_add_ssl_algorithms();

    const SSL_METHOD *method;

    method = TLS_server_method();
    context= SSL_CTX_new(method);
    if(!context){
        std::cerr << "SSL_CTX_new() did not work" << std::endl;
        return false;
    }

    SSL_CTX_set_ecdh_auto(context, 1);

    if((error_code = SSL_CTX_use_certificate_file(context, "cert.pem", SSL_FILETYPE_PEM)) <= 0){
        std::cerr << "SSL_CTX_use_certificate_file had an error: " << error_code << std::endl;
        return false;
    }

    if((error_code = SSL_CTX_use_PrivateKey_file(context, "key.pem", SSL_FILETYPE_PEM)) <= 0){
        std::cerr << "SSL_CTX_use_PrivateKey_file had an error: " << error_code << std::endl;
        return false;
    }

    if(!SSL_CTX_check_private_key(context)){
        std::cerr << "Certificate and private key do not match." << std::endl;
        return false;
    }

    return true;
}

void Server::cleanup_tls() {
    SSL_CTX_free(context);
    EVP_cleanup();
}

bool Server::start_server() {
    event_loop->start();
    keep_going = true;

    if (!init_listening_socket()) return false;
    if (socket_type != UDP) listen(socket_fd, BACKLOG);
    //Will only run if socket_type is TLS
    if(socket_type == TLS && !init_tls()) return false;
    while (keep_going) {
        struct sockaddr_in client;
        socklen_t length = sizeof(client);
        ResponseBuilder builder;

        switch (socket_type) {
            case UDP:
                handle_udp(builder, client,length);
                break;
            case TCP:
                handle_tcp(builder, client, length);
                break;
            case TLS:
                handle_tls(builder, client, length);
                break;
            default:
                return false;
        }
    }
    return true;
}

void Server::close_server() {
    event_loop->stop();
    event_loop->join();
    keep_going = false;
    freeaddrinfo(result);
    close(socket_fd);
}

Server::~Server() {
    delete event_loop;
}