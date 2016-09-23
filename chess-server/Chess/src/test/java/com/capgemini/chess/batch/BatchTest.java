package com.capgemini.chess.batch;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.capgemini.chess.dataaccess.entities.GameEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BatchConfiguration.class, BatchTest.BatchTestConfiguration.class},
loader=AnnotationConfigContextLoader.class)
public class BatchTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Configuration
	@EnableAutoConfiguration
	@EntityScan(basePackages = {"com.capgemini.chess.dataaccess"})
	static class BatchTestConfiguration {
		
		@Bean
	    public DataSource dataSource() throws SQLException {
	        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
	        return builder.setType(EmbeddedDatabaseType.H2).build();
	    }
		
		@Bean
		public JobLauncherTestUtils jobLauncherTestUtils() {
			return new JobLauncherTestUtils();
		}
	}
	
	@Test
	public void testLanchJob() throws Exception {
		
		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		
		// then
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}
	
	@Test
	public void testProcessorShouldNotDelete() throws Exception {
		// given
		BatchItemProcessor processor = new BatchItemProcessor();
		GameEntity game = createGameEntity(new Date());
		
		// when
		game = processor.process(game);
		
		// then
		assertNull(game);
	}
	
	@Test
	public void testProcessorShouldDelete() throws Exception {
		// given
		BatchItemProcessor processor = new BatchItemProcessor();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -11);
		GameEntity game = createGameEntity(cal.getTime());
		
		// when
		game = processor.process(game);
		
		// then
		assertNotNull(game);
	}
	
	private GameEntity createGameEntity(Date lastUpdateDate) {
		GameEntity gameEntity = new GameEntity();
		gameEntity.setLastUpdateDate(lastUpdateDate);
		return gameEntity;
	}
}
