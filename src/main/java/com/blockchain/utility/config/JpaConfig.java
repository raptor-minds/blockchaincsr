package com.blockchain.utility.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.blockchain.utility.model.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        
        // 自动提交配置
        properties.setProperty("hibernate.connection.autocommit", "true");
        properties.setProperty("hibernate.jdbc.batch_size", "1"); // 禁用批处理，立即执行
        properties.setProperty("hibernate.order_inserts", "false");
        properties.setProperty("hibernate.order_updates", "false");
        
        // 连接隔离级别 - 读已提交，适合自动提交模式
        properties.setProperty("hibernate.connection.isolation", "2");
        
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        properties.setProperty("hibernate.jdbc.time_zone", "UTC+8");
        
        em.setJpaProperties(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
} 