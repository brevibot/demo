package com.example.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.example.demo.db2.repo",
        entityManagerFactoryRef = "secondaryEntityManagerFactory",
        transactionManagerRef = "secondaryTransactionManager"
)
@EnableConfigurationProperties(DataSecondaryProperties.class)
public class SecondaryDbConfig {

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource,
            DataSecondaryProperties secondaryJpaProperties) {
        
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", secondaryJpaProperties.getDdlAuto());

        return builder
                .dataSource(dataSource)
                .packages("com.example.demo.db2.model")
                .persistenceUnit("db2")
                .properties(properties)
                .build();
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory) {
        return new JpaTransactionManager(secondaryEntityManagerFactory.getObject());
    }
    
    @Bean
    public DataSourceInitializer secondaryDataSourceInitializer(
            @Qualifier("secondaryDataSource") DataSource dataSource,
            DataSecondaryProperties secondaryJpaProperties) throws IOException {

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String[] scriptLocations = secondaryJpaProperties.getScripts().toArray(new String[0]);

        Resource[] resources = Stream.of(scriptLocations)
                .flatMap(location -> {
                    try {
                        return Stream.of(new PathMatchingResourcePatternResolver().getResources(location));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load SQL scripts from " + location, e);
                    }
                })
                .toArray(Resource[]::new);
        populator.addScripts(resources);

        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}