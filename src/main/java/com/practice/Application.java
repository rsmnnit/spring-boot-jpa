package com.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = {"com.practice.spring"})
@EnableAutoConfiguration
public class Application {
    public static void main(String args[]) {
        SpringApplication app = new SpringApplication(Application.class);
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", "8083");
        properties.put("server.servlet.context-path", "/practice");
        app.setDefaultProperties(properties);
        app.run(args);
    }
}
