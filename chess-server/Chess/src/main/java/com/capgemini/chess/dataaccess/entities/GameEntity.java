package com.capgemini.chess.dataaccess.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.capgemini.chess.dataaccess.enumerations.GameState;

/**
 * Chess game representation.
 * 
 * @author Michal Bejm
 *
 */
@Entity
@Table(name = "GAME")
public class GameEntity {
	
	@Id @GeneratedValue
	private long id;
	@OneToOne(mappedBy = "game")
	private ChallengeEntity challenge;
	@OneToMany
	@OrderColumn(name = "sequenceNumber")
	private List<MoveEntity> moves = new ArrayList<>();
	@Enumerated(EnumType.STRING)
	private GameState state;
	private Date startDate;
	private Date lastUpdateDate;
	
	@PrePersist
    public void prePersist() {
        setStartDate(new Date());
        preUpdate();
    }
 
    @PreUpdate
    public void preUpdate() {
        setLastUpdateDate(new Date());
    }
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public ChallengeEntity getChallenge() {
		return challenge;
	}
	
	public void setChallenge(ChallengeEntity challenge) {
		this.challenge = challenge;
	}
	
	public List<MoveEntity> getMoves() {
		return moves;
	}
	
	public void setMoves(List<MoveEntity> moves) {
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
