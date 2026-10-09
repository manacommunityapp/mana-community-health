package com.manacommunity.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.manacommunity.health",
        "com.manacommunity.common.security"
})
@EntityScan(basePackages = {
        "com.manacommunity.health.model",
        "com.manacommunity.common.model",
        "com.manacommunity.common.user.model"
})
@EnableJpaRepositories(basePackages = {
        "com.manacommunity.health.repository",
        "com.manacommunity.common.repository",
        "com.manacommunity.common.user.repository"
})
public class HealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthApplication.class, args);
    }
}
