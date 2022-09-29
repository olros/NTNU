#ifndef ResponseBuilderTests_hpp
#define ResponseBuilderTests_hpp

#include "../ResponseBuilder.hpp"
#include "../stuntypes.h"
#include "testUtils.hpp"
#include <stdlib.h>
#include <cassert>


void test_non_stun_request_sets_error_true(){
    char * request = (char *)malloc(sizeof(char));
    ResponseBuilder test_builder = ResponseBuilder(true, (STUNIncomingHeader *)request, create_client());
    assert(test_builder.is_error()== true);
}

void test_stun_request_sets_error_false(){
    STUNIncomingHeader * request = create_stun_request();
    ResponseBuilder test_builder = ResponseBuilder(true, request, create_client());
    assert(test_builder.is_error() == false);
}

class ResponseBuilderTests {
public:
    int test();
};

int ResponseBuilderTests::test() {
    test_non_stun_request_sets_error_true();
    test_stun_request_sets_error_false();
    std::cout << "All ResponseBuilderTests passed " << std::endl;
    return 0;
}

#endif