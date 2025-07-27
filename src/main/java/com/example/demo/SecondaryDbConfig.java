package com.example.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
public class SecondaryDbConfig {

    // Bind to the custom 'data.secondary' properties from application.yml
    @Bean
    @ConfigurationProperties("data.secondary")
    public JpaProperties secondaryJpaProperties() {
        return new JpaProperties();
    }

    @Bean(name = "secondaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("secondaryDataSource") DataSource dataSource,
            @Qualifier("secondaryJpaProperties") JpaProperties jpaProperties) {
        
        // Pass the ddl-auto property to the entity manager
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", jpaProperties.getDatabasePlatform());

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
    
    // This bean will initialize the secondary database with the scripts
    @Bean
    public DataSourceInitializer secondaryDataSourceInitializer(
            @Qualifier("secondaryDataSource") DataSource dataSource,
            @Qualifier("secondaryJpaProperties") JpaProperties jpaProperties) throws IOException {

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String[] scriptLocations = jpaProperties.getSql().getInit().getDataLocations().toArray(new String[0]);

        // Load all specified scripts
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
