#ifndef SuccessResponseBuilder_hpp
#define SuccessResponseBuilder_hpp

#include "stuntypes.h"
#include <arpa/inet.h>

#define SuccessCode 0x0101
#define IPV4_LENGTH 12
#define IPV6_LENGTH 24
#define IPV4_ATT_LENGTH 8
#define IPV6_ATT_LENGTH 20
class SuccessResponseBuilder{
    private:
        struct STUNResponse* res;
        void set_port(uint8_t *port);
        void set_IP(uint8_t *ip, bool is_IPv4);
    public:
        long get_length();
        SuccessResponseBuilder();
        struct STUNResponse* get_response();
        SuccessResponseBuilder&  set_stun_success_headers(struct STUNIncomingHeader* inc);
        SuccessResponseBuilder&  set_length(bool is_IPv4);
        SuccessResponseBuilder&  set_att_type(int type);
        SuccessResponseBuilder&  set_att_length(bool is_IPv4);
        SuccessResponseBuilder&  set_protocol(bool is_IPv4);
        SuccessResponseBuilder&  set_padding(int size);
        SuccessResponseBuilder& XOR_attributes(in_addr_t &ip,in_port_t &port, bool is_IPv4);
};

SuccessResponseBuilder::SuccessResponseBuilder(){
    res = (STUNResponse*) malloc(sizeof(struct STUNResponse));
}

struct STUNResponse* SuccessResponseBuilder::get_response(){
    return res;
}

SuccessResponseBuilder&  SuccessResponseBuilder::set_stun_success_headers(struct STUNIncomingHeader* inc){
    res->type = htons(SuccessCode);
    for(int i = 0; i < identifier_size; i++){
        res->identifier[i] = inc->identifier[i];
    }
    return *this;
}

SuccessResponseBuilder&  SuccessResponseBuilder::set_length(bool is_IPv4){
    res->length = htons(is_IPv4? IPV4_LENGTH: IPV6_LENGTH);
    return *this;

}

SuccessResponseBuilder&  SuccessResponseBuilder::set_att_type(int type){
    res->att_type= htons(XOR_MAPPED_ADDRESS);
    return *this;
}
SuccessResponseBuilder&  SuccessResponseBuilder::set_att_length(bool is_IPv4){
    res->att_length = htons(is_IPv4 ? IPV4_ATT_LENGTH : IPV6_ATT_LENGTH);
    return *this;

}


SuccessResponseBuilder&  SuccessResponseBuilder::set_protocol(bool is_IPv4){
    res->protocol = is_IPv4 ? IPv4_PROTOCOL_VALUE : IPv6_PROTOCOL_VALUE;
    return *this;

}

SuccessResponseBuilder&  SuccessResponseBuilder::set_padding(int size){
    res->padding = ((size_t )res & 1) ? 0x01 : 0x00;
    return *this;
}

SuccessResponseBuilder& SuccessResponseBuilder::XOR_attributes(in_addr_t &ip ,in_port_t &port, bool is_IPv4){
    uint8_t* client_port = (uint8_t*)&(port);
    uint8_t* client_IP = (uint8_t*)&(ip);

    for (int i = 0; i < sizeof(res->port); ++i) {
        client_port[i] = client_port[i] ^ res->identifier[i];
    }

    for (size_t i = 0; i < (is_IPv4? 4: 16); i++){
        client_IP[i] = client_IP[i] ^ res->identifier[i];
    }
    set_port(client_port);
    set_IP(client_IP, is_IPv4);
    return *this;
}

void SuccessResponseBuilder::set_port(uint8_t *port){
    for (size_t i = 0; i < 2; i++){
        res->port[i] = port[i];
    }
    
}

void SuccessResponseBuilder::set_IP(uint8_t *ip, bool is_IPv4){
    for(int i = 0; i < (is_IPv4? 4: 16); i++){
        res->ip[i] = ip[i];
    }
}

long SuccessResponseBuilder::get_length(){
    auto length = sizeof(res);
    return length;
}
#endif


