package com.ntnu.gidd.security.token;

import com.ntnu.gidd.security.UserDetailsImpl;

public interface TokenFactory {
    JwtAccessToken createAccessToken(UserDetailsImpl userDetails);

    JwtToken createRefreshToken(UserDetailsImpl userDetails);
}
