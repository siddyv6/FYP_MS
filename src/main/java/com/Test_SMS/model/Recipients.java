package com.Test_SMS.model;

import java.math.BigInteger;

public class Recipients {

    private BigInteger id;
    private String body;

    public BigInteger getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

}
