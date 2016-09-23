package com.capgemini.chess.dataaccess.dao.impl;

import org.springframework.stereotype.Repository;

import com.capgemini.chess.dataaccess.dao.MoveDao;
import com.capgemini.chess.dataaccess.entities.MoveEntity;

/**
 * Implementation of {@link MoveDao}.
 * 
 * @author Michal Bejm
 *
 */
@Repository
public class MoveDaoImpl extends AbstractDao<MoveEntity, Long> implements MoveDao {

}
