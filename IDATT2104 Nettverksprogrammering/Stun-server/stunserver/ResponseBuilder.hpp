#ifndef ResponseBuilder_hpp
#define ResponseBuilder_hpp
#include <stdlib.h> 
#include "SuccessResponseBuilder.hpp"
#include "ErrorResponseBuilder.hpp"
#include "stuntypes.h"
class ResponseBuilder{
    private:
        bool is_IPV4;
        bool is_error_request;
        STUNIncomingHeader* inc;
        sockaddr_in client;
        void check_header();
        void  check_identifier();
        void create_identifier();

    public:
        bool is_error();
        ResponseBuilder();
        ResponseBuilder(bool is_IPV4, STUNIncomingHeader* inc, sockaddr_in client);
        SuccessResponseBuilder build_success_response();
        ErrorResponseBuilder build_error_response(int error_code, std::string error_msg);

};

ResponseBuilder::ResponseBuilder() {}

ResponseBuilder::ResponseBuilder(bool is_IPV4, STUNIncomingHeader* inc, sockaddr_in client){
    this->client = client;
    this->inc = inc;
    this->is_IPV4 = is_IPV4;
    this->is_error_request = false;
    check_identifier();
    check_header();

}


bool ResponseBuilder::is_error(){
    return is_error_request;
}

void ResponseBuilder::check_header(){
    if(!IS_BINDING_REQUEST(inc->type))this->is_error_request = true;
}

void ResponseBuilder::check_identifier(){
    bool is_stun = true;
    for (int i = 0; i<COOKIE_LENGTH; i++){
        if(inc->identifier[i] != cookie[i]){
            is_stun = false;
            break;
        }
    }
    if(!is_stun)create_identifier();
}
void ResponseBuilder::create_identifier(){
    is_error_request = true;
    for (int i = 0; i<COOKIE_LENGTH; i++){
        inc->identifier[i] = cookie[i];
    }
    for (int i = COOKIE_LENGTH; i < identifier_size; i++){
        inc->identifier[i] = rand()%10;
    }
}

SuccessResponseBuilder ResponseBuilder::build_success_response(){
    return SuccessResponseBuilder()
    .set_stun_success_headers(inc)
    .set_length(is_IPV4)
    .set_att_length(is_IPV4)
    .set_att_type(0)
    .set_protocol(is_IPV4)
    .XOR_attributes(client.sin_addr.s_addr, client.sin_port, is_IPV4)
    .set_padding(0);
    
}

ErrorResponseBuilder ResponseBuilder::build_error_response(int error_code, std::string error_msg){
    return ErrorResponseBuilder()
    .set_stun_error_headers(inc)
    .set_length()
    .set_att_length()
    .set_att_type()
    .set_attribute(error_code)
    .set_message(error_msg);
    
}


#endif