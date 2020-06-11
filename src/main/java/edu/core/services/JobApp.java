package edu.core.services;

import edu.core.services.exception.ProcessNonRetriableException;
import edu.core.services.model.Item;
import edu.core.services.model.ItemList;
import edu.core.services.step.Processor;
import edu.core.services.step.Reader;
import edu.core.services.step.Writer;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableBatchProcessing
public class JobApp {

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    public static void main(String[] args) {
        SpringApplication.run(JobApp.class, args);
    }

    @Bean
    public Reader reader() {
        System.out.println("=====> Reader bean initialization");
        ItemList itemList = new ItemList(30);
        itemList.getItemList().add(new Item(40, "name"));
        itemList.getItemList().add(new Item(50, "name"));
        itemList.getItemList().add(new Item(60, "name"));
        itemList.getItemList().add(new Item(70, "name"));
        return new Reader(itemList);
    }

    @Bean
    public Processor processor() {
        System.out.println("=====> Processor bean initialization");
        return new Processor();
    }

    @Bean
    public Writer writer() {
        System.out.println("=====> Writer bean initialization");
        return new Writer();
    }

    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get("chunkStep")
                .<Item, Item>chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .retry(ProcessNonRetriableException.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public Job jobFlow() {
        FlowJobBuilder flowJobBuilder = jobBuilderFactory.get("jobFlow")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .on("new status").stop()
                .on("FAILED").fail()
                .on("COMPLETED").end()
                .build();

        return flowJobBuilder.build();
    }
}
