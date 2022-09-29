package com.ntnu.gidd.security.token;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class JwtAccessToken implements JwtToken {

    private String token;

    @Override
    public String getToken() {
        return token;
    }
}
