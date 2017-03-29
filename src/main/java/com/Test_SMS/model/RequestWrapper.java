package com.Test_SMS.model;

import com.Test_SMS.Security.Model.BaseResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;

/**
 * Created by Siddharth Varjivan on 07/02/2017.
 */
public class RequestWrapper extends BaseResponse {
    private List<Recipients> recipientsList;
    private Jws<Claims> jwsClaims;
    private String jwt;
    private Status status;

    public RequestWrapper() {
        this.jwt = jwt;
        setStatus(BaseResponse.Status.SUCCESS);
    }

    public List<Recipients> getRecipientsList() {
        return recipientsList;
    }

    public RequestWrapper(Jws<Claims> jwsClaims) {
        this.jwsClaims = jwsClaims;
        setStatus(Status.SUCCESS);
    }

    public void setRecipientsList(List<Recipients> recipientsList) {
        this.recipientsList = recipientsList;
    }

    public void setStatus(BaseResponse.Status status) {
        this.status = status;
    }
}
