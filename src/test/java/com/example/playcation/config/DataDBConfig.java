package com.example.playcation.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories
public class DataDBConfig {

  @Bean(name = "dataEntityManager")
  public LocalContainerEntityManagerFactoryBean dataEntityManager(
      EntityManagerFactoryBuilder builder,
      @Qualifier("dataSource") DataSource dataSource) {
    return builder
        .dataSource(dataSource)
        .packages("com.example.playcation.entity")
        .persistenceUnit("dataPU")
        .build();
  }

  @Bean(name = "dataTransactionManager")
  public PlatformTransactionManager dataTransactionManager(
      @Qualifier("dataEntityManager") EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }

  @Bean(name = "dataSource")
  @ConfigurationProperties(prefix = "spring.datasource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().build();
  }
}

