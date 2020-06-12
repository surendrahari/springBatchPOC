package edu.core.batch1.config;

import edu.core.batch1.step1.Processor1;
import edu.core.batch1.step1.Reader1;
import edu.core.batch1.step1.Writer1;
import edu.core.exception.ProcessNonRetriableException;
import edu.core.exception.WriteNonRetriableException;
import edu.core.model.Item;
import edu.core.model.ItemList;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryException;

@Configuration
public class Job1Config {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Reader1 reader() {
        System.out.println("=====> Reader bean initialization");
        ItemList itemList = new ItemList(5);
        return new Reader1(itemList);
    }

    @Bean
    public Processor1 processor() {
        System.out.println("=====> Processor bean initialization");
        return new Processor1();
    }

    @Bean
    public Writer1 writer() {
        System.out.println("=====> Writer bean initialization");
        return new Writer1();
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
                .retry(WriteNonRetriableException.class)
                .retryLimit(3)
                .listener(new StepExecutionListenerSupport() {

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {

                        System.out.println(" ----> " + stepExecution.getFailureExceptions());
                        System.out.println("<<<<<<<<< ---> " + stepExecution);
                        System.out.println("stepExecution.getExitStatus() : " + stepExecution.getExitStatus());

                        boolean condition1 = "chunkStep".equalsIgnoreCase(stepExecution.getStepName());
                        boolean condition2 = stepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode());
                        boolean condition3 = stepExecution.getFailureExceptions().get(0) instanceof RetryException;

                        System.out.println(condition1 + " " + condition2 + " " + condition3);

                        if (condition1 && condition2 && condition3) {
                            return new ExitStatus("RETRY_FAILED");
                        }
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    public Job jobFlow() {
        FlowJobBuilder flowJobBuilder = jobBuilderFactory.get("jobFlow")
                .incrementer(new RunIdIncrementer())
                .start(chunkStep())
                .on("RETRY_FAILED").stop()
                .on("FAILED").fail()
                .on("COMPLETED").end()
                .on("*").stop()
                .build();

        return flowJobBuilder.build();
    }
}
