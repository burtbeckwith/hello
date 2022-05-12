package com.oracle.hello.os;

import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetBucketRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Singleton;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class ObjectService {

    private static final String IMAGES = "images";

    private final ObjectStorage objectStorage;
    private final ResourceResolver resourceResolver;
    private final String compartmentId;
    private final String namespaceName;

    public ObjectService(ObjectStorage objectStorage,
                         ResourceResolver resourceResolver,
                         @Property(name = "hello.bucket-compartment-ocid") String compartmentId) {
        this.objectStorage = objectStorage;
        this.resourceResolver = resourceResolver;
        this.compartmentId = compartmentId;

        namespaceName = objectStorage.getNamespace(GetNamespaceRequest.builder()
                .build())
                .getValue();
    }

    public void createBucket() {

        try {
            // see if it exists
            objectStorage.getBucket(GetBucketRequest.builder()
                    .namespaceName(namespaceName)
                    .bucketName(IMAGES)
                    .build());
            return;
        } catch (BmcException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            }
        }

        // not found, create
        objectStorage.createBucket(CreateBucketRequest.builder()
                .namespaceName(namespaceName)
                .createBucketDetails(CreateBucketDetails.builder()
                        .compartmentId(compartmentId)
                        .name(IMAGES)
                        .build())
                .build());
    }

    public void storeImage(String resourcePath, String username) {

        try {
            // see if it exists
            objectStorage.getObject(GetObjectRequest.builder()
                    .namespaceName(namespaceName)
                    .bucketName(IMAGES)
                    .objectName(username)
                    .build());
        } catch (BmcException e) {
            if (e.getStatusCode() != 404) {
                throw e;
            }
        }

        // not found, create
        try (InputStream inputStream = resourceResolver
                .getResourceAsStream("classpath:" + resourcePath)
                .orElseThrow()) {

            byte[] bytes = inputStream.readAllBytes();

            storeImage(bytes, username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeImage(byte[] image, String username) {
        objectStorage.putObject(PutObjectRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(IMAGES)
                .objectName(username)
                .contentLength((long) image.length)
                .putObjectBody(new ByteArrayInputStream(image))
                .build()
        );
    }

    public byte[] getImage(String username) {
        GetObjectResponse response = objectStorage.getObject(GetObjectRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(IMAGES)
                .objectName(username)
                .build());
        try {
            return response.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
