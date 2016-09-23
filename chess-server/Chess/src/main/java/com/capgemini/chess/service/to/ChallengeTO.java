package com.capgemini.chess.service.to;

import com.capgemini.chess.dataaccess.entities.ChallengeEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;

/**
 * Transport object which represents {@link ChallengeEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class ChallengeTO {
	
	private long id;
	private UserProfileTO sender;
	private UserProfileTO receiver;
	private Level senderLevel;
	private Level receiverLevel;
	private boolean accepted;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public UserProfileTO getSender() {
		return sender;
	}
	
	public void setSender(UserProfileTO sender) {
		this.sender = sender;
	}
	
	public UserProfileTO getReceiver() {
		return receiver;
	}
	
	public void setReceiver(UserProfileTO receiver) {
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
	
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
