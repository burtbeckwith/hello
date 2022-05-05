package com.oracle.hello.data;

import com.oracle.hello.os.ObjectService;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;

import static io.micronaut.context.env.Environment.TEST;

@Singleton
@Requires(notEnv = TEST)
class DataPopulator {

    private final ObjectService objectService;
    private final PersonRepository personRepository;

    public DataPopulator(ObjectService objectService,
                         PersonRepository personRepository) {
        this.objectService = objectService;
        this.personRepository = personRepository;
    }

    @EventListener
    void init(StartupEvent event) {
        createDatabasePersons();
        uploadImages();
    }

    @Transactional
    void createDatabasePersons() {
        if (personRepository.count() == 0) {
            personRepository.save(new Person("aloksinha", "Alok Sinha custom message"));
            personRepository.save(new Person("ericsedlar", "Eric Sedlar custom message"));
        }
    }

    void uploadImages() {
        objectService.createBucket();
        objectService.storeImage("images/alok.jpg", "aloksinha");
        objectService.storeImage("images/eric.jpg", "ericsedlar");
    }
}
