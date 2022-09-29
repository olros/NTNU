package com.ntnu.gidd.security.service;

import com.ntnu.gidd.dto.JwtTokenResponse;

public interface JwtService {

    JwtTokenResponse refreshToken(String header);

}
