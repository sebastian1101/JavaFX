package com.capgemini.chess.dataaccess.dao.impl;

import org.springframework.stereotype.Repository;

import com.capgemini.chess.dataaccess.dao.GameDao;
import com.capgemini.chess.dataaccess.entities.GameEntity;

/**
 * Implementation of {@link GameDao}.
 * 
 * @author Michal Bejm
 *
 */
@Repository
public class GameDaoImpl extends AbstractDao<GameEntity, Long> implements GameDao {

}
