package com.oracle.hello.rest;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class User {

    private static final List<String> SCHEMAS = List.of("urn:ietf:params:scim:schemas:core:2.0:User");

    private final List<Email> emails;
    private final Name name;
    private final String username;

    public User(String email,
                String firstName,
                String lastName,
                String username) {
        this.username = username;
        emails = List.of(new Email(email));
        name = new Name(firstName, lastName);
    }

    public List<Email> getEmails() {
        return emails;
    }

    public Name getName() {
        return name;
    }

    public String getPassword() {
        return "{PBKDF2-HMAC-SHA256}1000$Rm+dEVMv98VInyrNTqEH/15AUxZdvL9Ex1OvFSOvidXECAASU38pnve/dS/fHddQ"; // Oracle123456
    }

    public List<String> getSchemas() {
        return SCHEMAS;
    }

    public String getUserName() {
        return username;
    }

    @Introspected
    public static class Email {

        private final String value;

        public Email(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return "work";
        }

        public boolean isPrimary() {
            return true;
        }
    }

    @Introspected
    public static class Name {

        private final String familyName;
        private final String givenName;

        public Name(String givenName,
                    String familyName) {
            this.givenName = givenName;
            this.familyName = familyName;
        }

        public String getFamilyName() {
            return familyName;
        }

        public String getGivenName() {
            return givenName;
        }
    }
}
