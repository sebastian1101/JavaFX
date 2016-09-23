package com.capgemini.chess.dataaccess.dao.impl;

import org.springframework.stereotype.Repository;

import com.capgemini.chess.dataaccess.dao.ChallengeDao;
import com.capgemini.chess.dataaccess.entities.ChallengeEntity;

/**
 * Implementation of {@link ChallengeDao}.
 * 
 * @author Michal Bejm
 *
 */
@Repository
public class ChallengeDaoImpl extends AbstractDao<ChallengeEntity, Long> implements ChallengeDao {

}
