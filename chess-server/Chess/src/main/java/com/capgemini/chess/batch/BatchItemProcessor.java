package com.capgemini.chess.batch;

import java.util.Calendar;
import java.util.Date;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.capgemini.chess.dataaccess.entities.GameEntity;

/**
 * Item processor for the batch which inactive games.
 * 
 * @author Michal Bejm
 *
 */
@PropertySource("classpath:application.properties")
public class BatchItemProcessor implements ItemProcessor<GameEntity, GameEntity> {
	
    @Value("${batch.offset}")
    private int offset;
	
	@Override
	public GameEntity process(GameEntity item) throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -offset);
		if (cal.getTime().after(item.getLastUpdateDate())) {
			return item;
		}
		else {
			return null;
		}
	}
}
