package org.example;

import org.example.security.AdminBootstrapProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@EnableConfigurationProperties(AdminBootstrapProperties.class)
public class RetroGameMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetroGameMarketApplication.class, args);
    }

}