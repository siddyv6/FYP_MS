package com.Test_SMS.controller;

import com.Test_SMS.exceptions.UnauthorizedException;
import com.Test_SMS.model.JWTResponse;
import com.Test_SMS.service.SecretService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.Map;

public class BaseController {

    @Autowired
    SecretService secretService;

    protected String createJwt(Map<String, Object> claims) {
      /*  Assert.notNull(
            claims.get(AccountService.USERNAME_CLAIM),
            AccountService.USERNAME_CLAIM + " claim is required."
        );
*/
        Date now = new Date();
        Date exp = new Date(now.getTime() + (1000*60)); // 60 seconds

        String jwt =  Jwts.builder()
            .setHeaderParam("kid", secretService.getMyPublicCreds().getKid())
            .setClaims(claims)
            .setIssuedAt(now)
            .setNotBefore(now)
            .setExpiration(exp)
            .signWith(
                SignatureAlgorithm.RS256,
                secretService.getMyPrivateKey()
            )
            .compact();

        return jwt;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
        SignatureException.class, MalformedJwtException.class, JwtException.class, IllegalArgumentException.class
    })
    public JWTResponse badRequest(Exception e) {
        return processException(e);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public JWTResponse unauthorized(Exception e) {
        return processException(e);
    }

    private JWTResponse processException(Exception e) {
        JWTResponse response = new JWTResponse();
        response.setStatus(JWTResponse.Status.ERROR);
        response.setMessage(e.getMessage());
        response.setExceptionType(e.getClass().getName());

        return response;
    }
}
