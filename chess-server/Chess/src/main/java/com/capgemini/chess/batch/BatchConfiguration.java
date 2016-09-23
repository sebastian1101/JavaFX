package com.capgemini.chess.batch;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.annotation.Scheduled;

import com.capgemini.chess.dataaccess.entities.GameEntity;

/**
 * Configuration of the batch job which marks incctive games.
 * 
 * @author Michal Bejm
 *
 */
@Configuration
@EnableBatchProcessing
@Import({BatchScheduler.class})
@PropertySource("classpath:application.properties")
public class BatchConfiguration {
	
	@Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public DataSource dataSource;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Autowired
    private SimpleJobLauncher jobLauncher;

    @Scheduled(cron = "${batch.cron}")
    public void perform() throws Exception {

        System.out.println("Job Started at :" + new Date());

        JobParameters param = new JobParametersBuilder().addString("JobID",
                String.valueOf(System.currentTimeMillis())).toJobParameters();

        JobExecution execution = jobLauncher.run(job1(), param);

        System.out.println("Job finished with status: " + execution.getStatus());
    }
	
	@Bean
    public JdbcCursorItemReader<GameEntity> reader() {
		JdbcCursorItemReader<GameEntity> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setSql("SELECT * FROM GAME WHERE state IN ('IN_PROGRESS', 'TIE_SUGGESTED_WHITE', 'TIE_SUGGESTED_BLACK')");
		reader.setRowMapper(new BeanPropertyRowMapper<>(GameEntity.class));
        return reader;
    }
	
	@Bean
    public ItemProcessor<GameEntity, GameEntity> processor() {
        return new BatchItemProcessor();
    }

	@Bean
    public JdbcBatchItemWriter<GameEntity> writer() {
        JdbcBatchItemWriter<GameEntity> writer = new JdbcBatchItemWriter<GameEntity>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GameEntity>());
        writer.setSql("UPDATE GAME SET state='INACTIVE' WHERE id = :id");
        writer.setDataSource(dataSource);
        return writer;
    }
	
	@Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<GameEntity, GameEntity> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}
