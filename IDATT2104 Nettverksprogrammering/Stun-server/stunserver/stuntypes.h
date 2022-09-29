
#ifndef stuntypes_h
#define stuntypes_h

#include <iostream>

#define identifier_size 16
#define IPv4_PROTOCOL_VALUE 0x01
#define IPv6_PROTOCOL_VALUE 0x02
#define XOR_MAPPED_ADDRESS 0x0020
#define IS_BINDING_REQUEST(msg_type)(((msg_type) & 0x0110) == 0x00100)
const int COOKIE_LENGTH = 4;
const uint8_t STUN_COOKIE_B1 = 0x21;
const uint8_t STUN_COOKIE_B2 = 0x12;
const uint8_t STUN_COOKIE_B3 = 0xA4;
const uint8_t STUN_COOKIE_B4 = 0x42;
const uint16_t STUN_REQUEST_TYPE = 0x00100;
const uint8_t cookie[COOKIE_LENGTH] = {STUN_COOKIE_B1, STUN_COOKIE_B2, STUN_COOKIE_B3, STUN_COOKIE_B4};
struct STUNIncomingHeader{

    uint16_t type;
    
    uint16_t length;
    
    uint8_t identifier[identifier_size];
};

struct STUNResponse{

    uint16_t type;

    uint16_t length;
    
    uint8_t identifier[identifier_size];

    uint16_t att_type;
    
    uint16_t att_length;

    uint8_t padding;

    uint8_t protocol;
    
    uint8_t port[2];

    uint8_t  ip[16];
};
struct STUNResponseIPV4{

    uint16_t type;

    uint16_t length;
    
    uint8_t identifier[identifier_size];

    uint16_t att_type;

    uint16_t att_length;

    uint8_t padding;

    uint8_t protocol;

    uint8_t port[2];

    uint8_t  ip[4];
};

struct StunErrorResponse{
    uint16_t type;

    uint16_t length;
    
    uint8_t identifier[identifier_size];

    uint16_t att_type;

    uint16_t att_length;

    uint16_t zeros;

    uint8_t error_class;

    uint8_t nr;

    uint8_t reason[128];
};

#endif
