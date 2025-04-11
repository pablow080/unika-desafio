package com.example.backend.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/database-desafio")
                .username("root")
                .password("Pablo#2005") // Aqui pode usar a senha normal
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}