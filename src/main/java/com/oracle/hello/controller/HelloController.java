package com.oracle.hello.controller;

import com.oracle.hello.data.PersonRepository;
import com.oracle.hello.os.ObjectService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;

import static io.micronaut.http.MediaType.IMAGE_JPEG;
import static io.micronaut.security.rules.SecurityRule.IS_ANONYMOUS;
import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;

@Controller
@ExecuteOn(TaskExecutors.IO)
class HelloController {

    private final ObjectService objectService;
    private final PersonRepository personRepository;

    HelloController(ObjectService objectService,
                    PersonRepository personRepository) {
        this.objectService = objectService;
        this.personRepository = personRepository;
    }

    @Secured(IS_ANONYMOUS)
    @View("home")
    @Get
    Map<String, Object> index(@Nullable Authentication authentication) {
        Map<String, Object> model = new HashMap<>();

        if (authentication != null) {
            model.put("fullname", authentication.getAttributes().get("user_displayname"));
            model.put("message", personRepository.findByUsername(authentication.getName()).get().getMessage());
        }

        return model;
    }

    @Secured(IS_AUTHENTICATED)
    @Get(uri = "/image", produces = IMAGE_JPEG)
    byte[] image(Authentication authentication) {
        return objectService.getImage(authentication.getName());
    }
}
