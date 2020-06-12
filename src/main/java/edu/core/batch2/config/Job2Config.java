package edu.core.batch2.config;

import edu.core.batch2.step2.Processor2;
import edu.core.batch2.step2.Reader2;
import edu.core.batch2.step2.Writer2;
import edu.core.model.Item;
import edu.core.model.ItemList;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Job2Config {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private PolicyConfig2 policyConfig2;

    @Bean
    public Reader2 reader() {
        System.out.println("=====> Reader bean initialization");
        ItemList itemList = new ItemList(5);
        return new Reader2(itemList);
    }

    @Bean
    public Processor2 processor() {
        System.out.println("=====> Processor bean initialization");
        return new Processor2();
    }

    @Bean
    public Writer2 writer() {
        System.out.println("=====> Writer bean initialization");
        return new Writer2();
    }


    @Bean
    public Step chunkStep1() {
        return stepBuilderFactory.get("chunkStep")
                .<Item, Item>chunk(1)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .retryPolicy(policyConfig2.retryPolicy())
                .build();
    }

    @Bean
    public Job jobFlow() {
        FlowJobBuilder flowJobBuilder = jobBuilderFactory.get("jobFlow")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep1())
                .on("FAILED").fail()
                .on("COMPLETED").end()
                .on("*").stop()
                .build();

        return flowJobBuilder.build();
    }
}
