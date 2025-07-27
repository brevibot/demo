package com.example.demo;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class JndiConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                tomcat.enableNaming();
                return super.getTomcatWebServer(tomcat);
            }

            @Override
            protected void postProcessContext(Context context) {
                // First H2 Database
                ContextResource resource1 = new ContextResource();
                resource1.setName("jdbc/db1");
                resource1.setAuth("Container");
                resource1.setType("javax.sql.DataSource");
                resource1.setScope("Sharable");
                resource1.setProperty("factory", "org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory");
                resource1.setProperty("driverClassName", "org.h2.Driver");
                resource1.setProperty("url", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                resource1.setProperty("username", "sa");
                resource1.setProperty("password", "");
                context.getNamingResources().addResource(resource1);

                // Second H2 Database
                ContextResource resource2 = new ContextResource();
                resource2.setName("jdbc/db2");
                resource2.setAuth("Container");
                resource2.setType("javax.sql.DataSource");
                resource2.setScope("Sharable");
                resource2.setProperty("factory", "org.apache.tomcat.dbcp.dbcp2.BasicDataSourceFactory");
                resource2.setProperty("driverClassName", "org.h2.Driver");
                resource2.setProperty("url", "jdbc:h2:mem:db2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
                resource2.setProperty("username", "sa");
                resource2.setProperty("password", "");
                context.getNamingResources().addResource(resource2);
            }
        };
    }
}