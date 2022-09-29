package com.ntnu.gidd.security.validator;


import com.ntnu.gidd.exception.InvalidJwtToken;
import com.ntnu.gidd.model.RefreshToken;
import com.ntnu.gidd.service.token.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtTokenValidator implements TokenValidator {

    private RefreshTokenService refreshTokenService;

    @Override
    public void validate(String jti) {
        RefreshToken refreshToken = refreshTokenService.getByJti(jti);
        if (!refreshToken.isValid())
            throw new InvalidJwtToken();
    }
}
