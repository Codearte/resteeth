package eu.codearte.resteeth.example;

import eu.codearte.resteeth.config.EnableResteeth;
import eu.codearte.resteeth.endpoint.EndpointProvider;
import eu.codearte.resteeth.endpoint.Endpoints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableResteeth(basePackages = "eu.codearte.resteeth.example")
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public EndpointProvider endpointProvider() {
        return Endpoints.fixedEndpoint("http://localhost:8080");
    }

}
