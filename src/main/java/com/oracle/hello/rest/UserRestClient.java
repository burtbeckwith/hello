package com.oracle.hello.rest;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

import static io.micronaut.http.MediaType.APPLICATION_FORM_URLENCODED;
import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Client("${OAUTH_ISSUER}")
public interface UserRestClient {

    @Post(uri = "/oauth2/v1/token", produces = APPLICATION_FORM_URLENCODED, consumes = APPLICATION_JSON)
    AccessToken accessToken(@Body String body,             // "grant_type=client_credentials&scope=urn:opc:idm:__myscopes__"
                            @Header String authorization); // Basic <base64encoded clientid:secret>

    @Post(uri = "/admin/v1/Users", produces = "application/scim+json")
    void createUser(@Body User user,
                    @Header String authorization); // Bearer <Access Token>

    @Get(uri = "/admin/v1/Users", produces = "application/scim+json")
    User getUser(@Header String authorization); // Bearer <Access Token>
}
