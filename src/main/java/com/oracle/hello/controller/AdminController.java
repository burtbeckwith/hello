package com.oracle.hello.controller;

import com.oracle.hello.data.UserService;
import com.oracle.hello.rest.User;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.View;

import java.io.IOException;

import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;

@Secured(IS_AUTHENTICATED)
@Controller("/admin")
@ExecuteOn(TaskExecutors.IO)
class AdminController {

    private final UserService userService;

    AdminController(UserService userService) {
        this.userService = userService;
    }

    @View("admin")
    @Get
    void index() {
    }

    @Post(uri = "/createUser", consumes = MULTIPART_FORM_DATA)
    void createUser(String email,
                    String username,
                    String firstName,
                    String lastName,
                    String message,
                    CompletedFileUpload image) {
        User user = new User(email, firstName, lastName, username);
        try {
            userService.create(user, message, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
