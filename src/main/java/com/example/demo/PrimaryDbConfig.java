package com.example.demo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.example.demo.db1",
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
@EnableConfigurationProperties(DataPrimaryProperties.class)
public class PrimaryDbConfig {

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource,
            DataPrimaryProperties primaryJpaProperties) {
        
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", primaryJpaProperties.getDdlAuto());

        return builder
                .dataSource(dataSource)
                .packages("com.example.demo.db1")
                .persistenceUnit("db1")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory.getObject());
    }

    @Bean
    @ConditionalOnProperty(name = "data.primary.mode", havingValue = "always")
    public DataSourceInitializer primaryDataSourceInitializer(
            @Qualifier("primaryDataSource") DataSource dataSource,
            DataPrimaryProperties primaryJpaProperties) throws IOException {
        
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        String[] scriptLocations = primaryJpaProperties.getScripts().toArray(new String[0]);
        
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