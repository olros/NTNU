#ifndef ErrorResponseTests_hpp
#define ErrorResponseTests_hpp

#include "../ErrorResponseBuilder.hpp"
#include "../stuntypes.h"
#include "testUtils.hpp"
#include <string.h>
#include <cassert>

void test_setErrorStunHeaders_sets_the_correct_headers(){
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    STUNIncomingHeader* request = create_stun_request();
    test_builder.set_stun_error_headers(request);
    StunErrorResponse* res = test_builder.get_response();
    for (int i = 0; i<identifier_size; i++){
        assert(res->identifier[i] == request->identifier[i]);
    }
    assert(res->type ==  htons(ErrorCode));
}

void test_setLength_Error_sets_correct_length(){
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    test_builder.set_length();
    assert(test_builder.get_response()->length == htons(ERROR_LENGTH));
    test_builder.set_length();
    assert(test_builder.get_response()->length == htons(ERROR_LENGTH));
}

void test_setAttLength_Error_sets_correct_length(){
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    test_builder.set_att_length();
    assert(test_builder.get_response()->att_length == htons(ERROR_ATT_LENGTH));
    test_builder.set_att_length();
    assert(test_builder.get_response()->att_length == htons(ERROR_ATT_LENGTH));
}

void test_setAttType_Error_sets_correct_Type(){
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    test_builder.set_att_type();
    assert(test_builder.get_response()->att_type == htons(ErrorCodeType));
}

void test_setAttribute_Error_sets_correct_class_and_nr(){
    int error_code = 400;
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    test_builder.set_attribute(error_code);
    assert(test_builder.get_response()->error_class == error_code/100);
    assert(test_builder.get_response()->nr == error_code%100);
}

void test_setMessage_sets_correct_message(){
    std::string error_code = "Test message";
    ErrorResponseBuilder test_builder = ErrorResponseBuilder();
    test_builder.set_message(error_code);
    for(int i = 0; i<error_code.length();i++){
        assert(test_builder.get_response()->reason[i]==error_code.c_str()[i]);
    }
}


class ErrorResponseTests {
public:
    int test();
};

int ErrorResponseTests::test() {
    test_setErrorStunHeaders_sets_the_correct_headers();
    test_setLength_Error_sets_correct_length();
    test_setAttLength_Error_sets_correct_length();
    test_setAttType_Error_sets_correct_Type();
    test_setAttribute_Error_sets_correct_class_and_nr();
    test_setMessage_sets_correct_message();
    std::cout << "All ErrorResponseTests passed " << std::endl;
    return 0;
}
#endif