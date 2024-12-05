package com.example.scheduledbach.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private final PrimaryDataSourceProperties primaryProperties;
    private final SecondaryDataSourceProperties secondaryProperties;
    private final TertiaryDataSourceProperties tertiaryProperties;

    public DataSourceConfig(PrimaryDataSourceProperties primaryProperties,
                            SecondaryDataSourceProperties secondaryProperties,
                            TertiaryDataSourceProperties tertiaryProperties) {
        this.primaryProperties = primaryProperties;
        this.secondaryProperties = secondaryProperties;
        this.tertiaryProperties = tertiaryProperties;
    }

    @Primary
    @Bean(name = "primaryDataSource")
    public DataSource primaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(primaryProperties.getUrl());
        dataSource.setUsername(primaryProperties.getUsername());
        dataSource.setPassword(primaryProperties.getPassword());
        dataSource.setDriverClassName(primaryProperties.getDriverClassName());
        return dataSource;
    }

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(secondaryProperties.getUrl());
        dataSource.setUsername(secondaryProperties.getUsername());
        dataSource.setPassword(secondaryProperties.getPassword());
        dataSource.setDriverClassName(secondaryProperties.getDriverClassName());
        return dataSource;
    }

    @Bean(name = "tertiaryDataSource")
    public DataSource tertiaryDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(tertiaryProperties.getUrl());
        dataSource.setUsername(tertiaryProperties.getUsername());
        dataSource.setPassword(tertiaryProperties.getPassword());
        dataSource.setDriverClassName(tertiaryProperties.getDriverClassName());
        return dataSource;
    }
}

