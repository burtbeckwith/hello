package com.oracle.hello.data;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

import static io.micronaut.data.model.query.builder.sql.Dialect.ORACLE;

@JdbcRepository(dialect = ORACLE)
public interface PersonRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByUsername(String username);
}
