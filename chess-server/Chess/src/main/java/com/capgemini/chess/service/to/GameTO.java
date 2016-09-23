package com.capgemini.chess.service.to;

import java.util.Date;
import java.util.List;

import com.capgemini.chess.dataaccess.entities.GameEntity;
import com.capgemini.chess.dataaccess.enumerations.GameState;

/**
 * Transport object which represents {@link GameEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class GameTO {
	
	private long id;
	private ChallengeTO challenge;
	private List<MoveTO> moves;
	private GameState state;
	private Date startDate;
	private Date lastUpdateDate;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public ChallengeTO getChallenge() {
		return challenge;
	}
	
	public void setChallenge(ChallengeTO challenge) {
		this.challenge = challenge;
	}
	
	public List<MoveTO> getMoves() {
		return moves;
	}
	
	public void setMoves(List<MoveTO> moves) {
		this.moves = moves;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
}
