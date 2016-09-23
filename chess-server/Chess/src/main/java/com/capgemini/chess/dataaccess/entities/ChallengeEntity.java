package com.capgemini.chess.dataaccess.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.capgemini.chess.dataaccess.enumerations.Level;

/**
 * Challenge Entity.
 * 
 * @author Michal Bejm
 *
 */
@Entity
public class ChallengeEntity {
	
	@Id @GeneratedValue
	private long id;
	@ManyToOne
	private UserEntity sender;
	@ManyToOne
	private UserEntity receiver;
	@Enumerated(EnumType.ORDINAL)
	private Level senderLevel;
	@Enumerated(EnumType.ORDINAL)
	private Level receiverLevel;
	@OneToOne
	private GameEntity game;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public UserEntity getSender() {
		return sender;
	}
	
	public void setSender(UserEntity sender) {
		this.sender = sender;
	}
	
	public UserEntity getReceiver() {
		return receiver;
	}
	
	public void setReceiver(UserEntity receiver) {
		this.receiver = receiver;
	}
	
	public Level getSenderLevel() {
		return senderLevel;
	}
	
	public void setSenderLevel(Level senderLevel) {
		this.senderLevel = senderLevel;
	}
	
	public Level getReceiverLevel() {
		return receiverLevel;
	}
	
	public void setReceiverLevel(Level receiverLevel) {
		this.receiverLevel = receiverLevel;
	}

	public GameEntity getGame() {
		return game;
	}

	public void setGame(GameEntity game) {
		this.game = game;
	}
}
