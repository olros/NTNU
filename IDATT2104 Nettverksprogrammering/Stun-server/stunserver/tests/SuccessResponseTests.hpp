#ifndef SuccessResponseTests_hpp
#define SuccessResponseTests_hpp

#include "../SuccessResponseBuilder.hpp"
#include "../stuntypes.h"
#include "testUtils.hpp"
#include <cassert>


#define SUCCESS_CODE 0x0101

void test_setStunHeaders_sets_the_correct_headers(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    STUNIncomingHeader* request = create_stun_request();
    test_builder.set_stun_success_headers(request);
    STUNResponse* res = test_builder.get_response();
    for (int i = 0; i<identifier_size; i++){
        assert(res->identifier[i] == request->identifier[i]);
    }
    assert(res->type == SUCCESS_CODE);

}
void test_setLength_sets_correct_length(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    test_builder.set_length(true);
    assert(test_builder.get_response()->length == htons(IPV4_LENGTH));
    test_builder.set_length(false);
    assert(test_builder.get_response()->length == htons(IPV6_LENGTH));
}

void test_setAttLength_sets_correct_length(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    test_builder.set_att_length(true);
    assert(test_builder.get_response()->att_length == htons(IPV4_ATT_LENGTH));
    test_builder.set_att_length(false);
    assert(test_builder.get_response()->att_length == htons(IPV6_ATT_LENGTH));
}

void test_setProtocol_sets_correct_protocol(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    test_builder.set_protocol(true);
    assert(test_builder.get_response()->protocol == IPv4_PROTOCOL_VALUE);
    test_builder.set_protocol(false);
    assert(test_builder.get_response()->protocol == IPv6_PROTOCOL_VALUE);
}

void test_setAttType_sets_correct_Type(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    test_builder.set_att_type(XOR_MAPPED_ADDRESS);
    assert(test_builder.get_response()->att_type == htons(XOR_MAPPED_ADDRESS));
}

void test_XORAttributes_XORs_correctly(){
    SuccessResponseBuilder test_builder = SuccessResponseBuilder();
    STUNIncomingHeader* request = create_stun_request();
    test_builder.set_stun_success_headers((request));
    in_addr_t ip = create_IP();
    in_port_t port = create_port();
    uint8_t* ip_arr = (uint8_t*)&(ip);
    uint8_t* port_arr = (uint8_t*)&(port);
    test_builder.XOR_attributes(ip, port, true);
    STUNResponse* res = test_builder.get_response();
    for(int i = 0; i<4;i++){
        assert((res->ip[i]) == ip_arr[i]);
    }
    for(int i = 0; i<2;i++){
        assert((res->port[i]) == port_arr[i]);
    }
    
}


class SuccessResponseTests {
public:
    int test();
};

int SuccessResponseTests::test() {
    test_setStunHeaders_sets_the_correct_headers();
    test_setLength_sets_correct_length();
    test_setAttLength_sets_correct_length();
    test_setProtocol_sets_correct_protocol();
    test_setAttType_sets_correct_Type();
    test_XORAttributes_XORs_correctly();
    std::cout << "All SuccessResponseTests passed " << std::endl;
    return 0;
}

#endif