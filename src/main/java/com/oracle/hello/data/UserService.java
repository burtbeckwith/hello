package com.oracle.hello.data;

import com.oracle.hello.os.ObjectService;
import com.oracle.hello.rest.AccessToken;
import com.oracle.hello.rest.User;
import com.oracle.hello.rest.UserRestClient;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
public class UserService {

    private static final String TOKEN_BODY = "grant_type=client_credentials&scope=urn:opc:idm:__myscopes__";

    private final ObjectService objectService;
    private final PersonRepository personRepository;
    private final UserRestClient userRestClient;
    private final String authHeader;

    public UserService(ObjectService objectService,
                       PersonRepository personRepository,
                       UserRestClient userRestClient,
                       @Property(name = "OAUTH_CLIENT_ID") String clientId,
                       @Property(name = "OAUTH_CLIENT_SECRET") String clientSecret) {
        this.objectService = objectService;
        this.personRepository = personRepository;
        this.userRestClient = userRestClient;
        authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ':' + clientSecret).getBytes(UTF_8));
    }

    @Transactional
    public void create(User user, String message, byte[] image) {
        createOAuthUser(user);
        createDatabaseMessage(user, message);
        uploadImage(user, image);
    }

    private void createOAuthUser(User user) {
        AccessToken accessToken = userRestClient.accessToken(TOKEN_BODY, authHeader);
        userRestClient.createUser(user, "Bearer " + accessToken.getToken());
    }

    private void createDatabaseMessage(User user, String message) {
        personRepository.save(new Person(user.getUserName(), message));
    }

    private void uploadImage(User user, byte[] image) {
        objectService.storeImage(image, user.getUserName());
    }
}
