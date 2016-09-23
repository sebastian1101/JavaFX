package com.capgemini.chess.service.to;

import com.capgemini.chess.dataaccess.entities.UserEntity;
import com.capgemini.chess.dataaccess.enumerations.Level;

/**
 * Transport object which represents stats of {@link UserEntity}.
 * 
 * @author Michal Bejm
 *
 */
public class UserStatsTO {
	
	private long id;
	private String login;
	private String name;
	private String surname;
	private Level level;
	private int points;
	private int position;
	private int gamesPlayed;
	private int gamesWon;
	private int gamesDrawn;
	private int gamesLost;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Level getLevel() {
		return level;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	
	public int getGamesWon() {
		return gamesWon;
	}
	
	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
	
	public int getGamesDrawn() {
		return gamesDrawn;
	}
	
	public void setGamesDrawn(int gamesDrawn) {
		this.gamesDrawn = gamesDrawn;
	}
	
	public int getGamesLost() {
		return gamesLost;
	}
	
	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;
	}
}
