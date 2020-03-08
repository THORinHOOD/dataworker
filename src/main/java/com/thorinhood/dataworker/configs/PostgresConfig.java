package com.thorinhood.dataworker.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {

    @Value("${postgres.url}")
    private String URL;
    @Value("${postgres.user}")
    private String USER;
    @Value("${postgres.driver}")
    private String DRIVER;
    @Value("${postgres.password}")
    private String PASSWORD;

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setUrl(URL);
        driverManagerDataSource.setUsername(USER);
        driverManagerDataSource.setPassword(PASSWORD);
        driverManagerDataSource.setDriverClassName(DRIVER);
        return driverManagerDataSource;
    }

    @Bean
    public JdbcTemplate postgresJdbc(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}