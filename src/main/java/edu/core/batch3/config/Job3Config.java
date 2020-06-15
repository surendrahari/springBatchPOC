package edu.core.batch3.config;

import edu.core.batch3.BusinessLogic3;
import edu.core.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Job3Config implements ApplicationContextAware {

    private static final String FILE_PATH = "/Users/surendra/sshmbp/GitHub/springBatchExamples/src/main/resources/";
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private BusinessLogic3 businessLogic3;
    @Autowired
    private PolicyConfig3 policyConfig3;

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JobRegistryBeanPostProcessor jobRegistrar() throws Exception {
        JobRegistryBeanPostProcessor jobRegistrar = new JobRegistryBeanPostProcessor();

        jobRegistrar.setJobRegistry(jobRegistry);
        jobRegistrar.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        jobRegistrar.afterPropertiesSet();

        return jobRegistrar;
    }

    @Bean
    public JobOperator jobOperator() throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();

        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobRegistry(jobRegistry);

        jobOperator.afterPropertiesSet();

        return jobOperator;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader(@Value("#{jobParameters[filename]}") String filename) {
        System.out.println("filename : " + filename);
        return new FlatFileItemReaderBuilder<Employee>()
                .name("employeeReader")
                .resource(new ClassPathResource(filename))
//                .linesToSkip(1)
                .delimited()
                .names("id", "name", "sal")
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
                    setTargetType(Employee.class);
                }})
                .build();
    }

    @Bean
    public LineMapper<Employee> lineMapper() {
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "sal");

        final EmployeeFieldSetMapper fieldSetMapper = new EmployeeFieldSetMapper();

        final DefaultLineMapper<Employee> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader1(@Value("#{jobParameters[filename]}") String filename) {
        System.out.println("=====> Begin Reader .....");

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "name", "sal");

        DefaultLineMapper<Employee> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(tokenizer);
        defaultLineMapper.setFieldSetMapper(new EmployeeFieldSetMapper());
        defaultLineMapper.afterPropertiesSet();

        FlatFileItemReader<Employee> reader = new FlatFileItemReader<Employee>();
//        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource(filename));
        reader.setLineMapper(defaultLineMapper);

        System.out.println("=====> End Reader .....");
        return reader;
    }

    @Bean
    public ItemProcessor<Employee, Employee> processor() throws Exception {
        System.out.println("=====> Begin Processor .....");

        ValidatingItemProcessor<Employee> validateProcessor =
                new ValidatingItemProcessor<>((employee) -> {
                    if (employee.getSal() < 0) {
                        throw new ValidationException("Salary shouldn't be negative");
                    }
                });

        List<ItemProcessor<Employee, Employee>> delegateProcessor = new ArrayList<>();
        delegateProcessor.add(validateProcessor);
        delegateProcessor.add((employee) -> {
            //Upper Case Item Processor
            System.out.println("=================================");
            System.out.println("Begin Process : " + employee.toLog());
            Employee emp = businessLogic3.getRemoteAccess(employee);
            System.out.println("End Process : " + emp.toLog());
            return emp;
        });

        CompositeItemProcessor<Employee, Employee> processor = new CompositeItemProcessor<>();
        processor.setDelegates(delegateProcessor);
        processor.afterPropertiesSet();

        System.out.println("=====> End Processor .....");
        return processor;
    }

    @Bean
    @Transactional
    public ItemWriter<Employee> writerDB() throws Exception {
        System.out.println("=====> Begin Writer .....");

        JdbcBatchItemWriter<Employee> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into employee values(:id, :name, :sal)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        System.out.println("=====> End Writer .....");
        return itemWriter;
    }

    @Bean
    public ItemWriter<Employee> writerFile() throws Exception {
        System.out.println("=====> Begin Writer .....");

        FlatFileItemWriter<Employee> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new PassThroughLineAggregator<>());

        File outputFile = new File(FILE_PATH + "output.csv");
        String outputFilePath = outputFile.getAbsolutePath();
        System.out.println("output file path: -> " + outputFilePath);

        itemWriter.setAppendAllowed(true);
        itemWriter.setResource(new FileSystemResource(outputFile));
        itemWriter.afterPropertiesSet();

        System.out.println("=====> End Writer .....");
        return itemWriter;
    }

    @Bean
    public Step step() throws Exception {
        System.out.println("=========> Begin Step..........");

        Step step = stepBuilderFactory.get("step")
                .<Employee, Employee>chunk(5)
                .reader(reader(null))
                .processor(processor())
                .writer(writerDB())
//                .writer(writerFile())
                .build();

        System.out.println("=========> End Step..........");
        return step;
    }

    @Bean
    public Job job() throws Exception {
        System.out.println("=========> Begin Job..........");

        Job job = jobBuilderFactory.get("job")
                .start(step())
                .build();

        System.out.println("=========> End Job..........");
        return job;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class EmployeeFieldSetMapper implements FieldSetMapper<Employee> {

        @Override
        public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
            return new Employee(fieldSet.readInt("id"), fieldSet.readString("name"), fieldSet.readInt("sal"));
        }
    }
}
