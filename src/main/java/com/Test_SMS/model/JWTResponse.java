package com.Test_SMS.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JWTResponse {
    private String exceptionType;
    private String jwt;
    private Jws<Claims> jwsClaims;

    public JWTResponse() {}

    public JWTResponse(String jwt) {
        this.jwt = jwt;
    }

    public JWTResponse(Jws<Claims> jwsClaims) {
        this.jwsClaims = jwsClaims;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Jws<Claims> getJwsClaims() {
        return jwsClaims;
    }

    public void setJwsClaims(Jws<Claims> jwsClaims) {
        this.jwsClaims = jwsClaims;
    }
}
