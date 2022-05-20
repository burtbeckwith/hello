package com.oracle.hello.controller;

import com.oracle.hello.HelloException;
import com.oracle.hello.data.UserService;
import com.oracle.hello.rest.User;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.View;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;

@Secured(IS_AUTHENTICATED)
@Controller("/admin")
@ExecuteOn(TaskExecutors.IO)
class AdminController {

    private static final URI ADMIN = URI.create("/admin");

    private final UserService userService;

    AdminController(UserService userService) {
        this.userService = userService;
    }

    @View("admin")
    @Get
    void index() {
    }

    @Post(uri = "/createUser", consumes = MULTIPART_FORM_DATA)
    HttpResponse<?> createUser(String email,
                               String username,
                               String firstName,
                               String lastName,
                               String message,
                               CompletedFileUpload image) {
        byte[] bytes;
        try {
            bytes = image.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (bytes.length == 0) {
            return validationError("Image is required");
        }

        if (isEmpty(email)) {
            return validationError("Email is required");
        }

        if (isEmpty(firstName)) {
            return validationError("First name is required");
        }

        if (isEmpty(lastName)) {
            return validationError("Last name is required");
        }

        if (isEmpty(username)) {
            return validationError("Username is required");
        }

        if (isEmpty(message)) {
            return validationError("Message is required");
        }

        User user = new User(email, firstName, lastName, username);

        try {
            userService.create(user, message, bytes);
        } catch (HelloException e) {
            return validationError(e.getMessage());
        }

        return HttpResponse.seeOther(ADMIN);
    }

    private HttpResponse<?> validationError(String message) {
        return HttpResponse.serverError(Collections.singletonMap("error", message));
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
