package com.capgemini.chess.service;

import java.util.List;

import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.GameTO;
import com.capgemini.chess.service.to.MoveTO;

/**
 * Service for game management.
 * 
 * @author Michal Bejm
 *
 */
public interface GameService {
	
	/**
	 * Performs a move on the chess board.
	 * 
	 * @param id id of the game.
	 * @param userId id of the user who performs a move.
	 * @param moveTO move to perform.
	 * @return board after the move is performed.
	 * @throws EntityNotFoundException in case game cannot be found.
	 * @throws InvalidUserException in case user is not allowed to perform a move.
	 * @throws InvalidMoveException in case move is not valid.
	 */
	public BoardTO performMove(Long id, Long userId, MoveTO moveTO) 
			throws EntityNotFoundException, InvalidUserException, InvalidMoveException;
	
	/**
	 * Calculates current board for a game.
	 * 
	 * @param id id of a game.
	 * @return calculated board.
	 * @throws EntityNotFoundException in case game cannot be found.
	 */
	public BoardTO getBoard(Long id) throws EntityNotFoundException;
	
	/**
	 * Calculates historical board for a game.
	 * 
	 * @param id id of a game.
	 * @param moveNumber index of move for which board shall be returned.
	 * @return calculated board.
	 * @throws EntityNotFoundException in case game cannot be found.
	 */
	public BoardTO getBoardHistory(Long gameId, Long moveNumber) throws EntityNotFoundException;
	
	/**
	 * Suggests tie for a game with a given id.
	 * 
	 * @param id id of the game.
	 * @param userId id of the user suggesting a tie.
	 * @throws EntityNotFoundException in case game cannot be found.
	 * @throws InvalidUserException in case user is not allowed to suggest the tie.
	 */
	public void suggestTie(Long id, Long userId) throws EntityNotFoundException, InvalidUserException;
	
	/**
	 * Accepts tie for a game with a given id.
	 * 
	 * @param id id of the game.
	 * @param userId id of the user accepting a tie.
	 * @throws EntityNotFoundException in case game cannot be found.
	 * @throws InvalidUserException in case user is not allowed to accept the tie.
	 */
	public void acceptTie(Long id, Long userId) throws EntityNotFoundException, InvalidUserException;
	
	/**
	 * Gives up a game with a given id.
	 * 
	 * @param id id of the game.
	 * @param userId id of the user giving up.
	 * @throws EntityNotFoundException in case game cannot be found.
	 * @throws InvalidUserException in case user is not allowed to give up a game.
	 */
	public void giveupGame(Long id, Long userId) throws EntityNotFoundException, InvalidUserException;
	
	/**
	 * Gets a list of games that an user currently plays.
	 * 
	 * @param userId id of the user.
	 * @return list of games that the user currently plays.
	 */
	public List<GameTO> getGames(Long userId);
}
