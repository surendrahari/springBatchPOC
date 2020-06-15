package edu.core;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration
@EnableRetry
@EnableBatchProcessing
@EnableTransactionManagement //https://www.marcobehler.com/guides/spring-transaction-management-transactional-in-depth
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
