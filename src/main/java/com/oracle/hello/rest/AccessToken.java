package com.oracle.hello.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public class AccessToken {

    private final String token;
    private final String type;
    private final int expiresIn;

    public AccessToken(String token,
                       String type,
                       int expiresIn) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
    }

    @JsonProperty("access_token")
    public String getToken() {
        return token;
    }

    @JsonProperty("token_type")
    public String getType() {
        return type;
    }

    @JsonProperty("expires_in")
    public int getExpiresIn() {
        return expiresIn;
    }
}
