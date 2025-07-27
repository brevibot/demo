package com.example.demo;

import javax.sql.DataSource;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@TestConfiguration
public class TestDataSourceConfig {

    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("db1")
                .addScript("classpath:sql/db1-schema.sql")
                .addScript("classpath:sql/db1-data.sql")
                .build();
    }

    @Bean
    public DataSource secondaryDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("db2")
                .addScript("classpath:sql/db2-schema.sql")
                .addScript("classpath:sql/db2-data.sql")
                .build();
    }
}