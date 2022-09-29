#ifndef ErrorResponseBuilder_hpp
#define ErrorResponseBuilder_hpp

#include "stuntypes.h"
#include <arpa/inet.h>
#include <string>

#define ErrorCode 0x0111
#define ErrorCodeType 0x0009
#define ERROR_LENGTH 136
#define ERROR_ATT_LENGTH 132

class ErrorResponseBuilder{
    private:
        struct StunErrorResponse* res;

    public:
        StunErrorResponse* get_response();
        ErrorResponseBuilder();
        ErrorResponseBuilder&  set_stun_error_headers(struct STUNIncomingHeader* inc);
        ErrorResponseBuilder&  set_length();
        ErrorResponseBuilder&  set_att_length();
        ErrorResponseBuilder&  set_att_type();
        ErrorResponseBuilder&  set_attribute(int code);
        ErrorResponseBuilder&  set_message(std::string msg);

};
ErrorResponseBuilder::ErrorResponseBuilder(){
    res = (StunErrorResponse*) malloc(sizeof(struct StunErrorResponse));
}

struct StunErrorResponse* ErrorResponseBuilder::get_response(){
    return res;
}

ErrorResponseBuilder& ErrorResponseBuilder::set_stun_error_headers(struct STUNIncomingHeader* inc){
    res->type = htons(ErrorCode);
    for(int i = 0; i < identifier_size; i++){
        res->identifier[i] = inc->identifier[i];
    }
    return *this;
}

ErrorResponseBuilder& ErrorResponseBuilder::set_length(){
    res->length = htons(ERROR_LENGTH);
    return *this;
}

ErrorResponseBuilder&  ErrorResponseBuilder::set_att_length(){
    res->att_length = htons(ERROR_ATT_LENGTH);
    return *this;
}

ErrorResponseBuilder&  ErrorResponseBuilder::set_att_type(){
    res->att_type = htons(ErrorCodeType);
    return *this;
}

ErrorResponseBuilder&  ErrorResponseBuilder::set_attribute(int code){
    res->error_class = code/100;
    res->nr = code%100;

    return *this;
}

ErrorResponseBuilder&  ErrorResponseBuilder::set_message(std::string msg){
    for(int i = 0; i<msg.length(); i++){
        res->reason[i]  = msg[i];
    }
    return *this;
}   

#endif