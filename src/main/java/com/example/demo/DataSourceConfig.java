package com.example.demo;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@Profile("!test")
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "primaryDataSource")
    @Primary
    public DataSource primaryDataSource() {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName(primaryDataSourceProperties().getJndiName());
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        try {
            bean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (DataSource) bean.getObject();
    }

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName(secondaryDataSourceProperties().getJndiName());
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
         try {
            bean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (DataSource) bean.getObject();
    }
}