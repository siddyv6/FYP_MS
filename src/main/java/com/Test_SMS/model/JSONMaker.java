package com.Test_SMS.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.messagebird.objects.Message;

public class JSONMaker {
    private final String Response;
    private final String error;

    @JsonCreator
    public JSONMaker(String Response, @JsonProperty("Error") String error) {
        this.Response = Response;
        this.error = error;
    }

    public String getResponse() {
        return Response;
    }

    public String getError() {
        return error;
    }
}
