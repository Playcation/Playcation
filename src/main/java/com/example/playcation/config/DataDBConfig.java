package com.example.playcation.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
@EnableJpaRepositories(
    basePackages = "com.example.playcation.*.repository", // 어떤 패키지에 대해 동작할지
    entityManagerFactoryRef = "dataEntityManager", // 아래 작성할 메서드 명
    transactionManagerRef = "dataTransactionManager" // "
)
@EntityScan(basePackages = {"com.example.playcation.*"})
public class DataDBConfig {

  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.data")
  public DataSource dataDBSource() {

    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean dataEntityManager() {

    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

    em.setDataSource(dataDBSource());
    // em.setPackagesToScan("com.example.playcation.*.entity");
    em.setPackagesToScan("com.example.playcation.*");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    HashMap<String, Object> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", "create");
    properties.put("hibernate.show_sql", "true");
    properties.put("hibernate.format_sql", "true");
    properties.put("hibernate.use_sql_comments", "true");
    em.setJpaPropertyMap(properties);

    return em;
  }

  @Bean
  public PlatformTransactionManager dataTransactionManager() {

    JpaTransactionManager transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(dataEntityManager().getObject());

    return transactionManager;
  }
}
