#ifndef testUtils_hpp
#define testUtils_hpp

#include "../stuntypes.h"
#include <stdlib.h> 
#include <arpa/inet.h>

STUNIncomingHeader* create_stun_request(){
    STUNIncomingHeader* request = (STUNIncomingHeader *) malloc(sizeof(STUNIncomingHeader));
    for (int i = 0; i<COOKIE_LENGTH; i++){
        request->identifier[i] = cookie[i];
    }
    for (int i = COOKIE_LENGTH; i<identifier_size; i++){
        request->identifier[i] = rand() % 10;
    }
    request->length = htons(0);
    request->type =STUN_REQUEST_TYPE;
    return request;
}

sockaddr_in create_client(){
    sockaddr_in client;
    client.sin_port = htons(80);
    client.sin_addr.s_addr = INADDR_ANY;
    client.sin_family = AF_INET;
    return client;
}

in_addr_t create_IP(){
    return htonl(rand());
} 

in_port_t create_port(){
    return htons(rand());
}

#endif