package com.capgemini.chess.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capgemini.chess.rest.exceptions.BadRequestException;
import com.capgemini.chess.rest.exceptions.ForbiddenException;
import com.capgemini.chess.rest.exceptions.NotFoundException;
import com.capgemini.chess.service.AuthenticationFacade;
import com.capgemini.chess.service.GameService;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.GameTO;
import com.capgemini.chess.service.to.MoveTO;

/**
 * Game controller.
 * 
 * @author Michal Bejm
 *
 */
@Controller
@RequestMapping("/game")
public class GameRestController {
	
	private GameService gameService;
	private AuthenticationFacade authenticationFacade;
	
	@Autowired
	public GameRestController(GameService gameService, AuthenticationFacade authenticationFacade) {
		this.gameService = gameService;
		this.authenticationFacade = authenticationFacade;
	}
	
	@RequestMapping(value = "/{id}/move", method = RequestMethod.PUT)
	@ResponseBody
	public BoardTO performMove(@PathVariable Long id, @RequestBody MoveTO moveTO) {
		long userId = authenticationFacade.getUserId();
		try {
			return gameService.performMove(id, userId, moveTO);
		} catch (InvalidMoveException e) {
			throw new BadRequestException(e);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BoardTO getBoard(@PathVariable Long id) {
		try {
			return gameService.getBoard(id);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}
	
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ResponseBody
	public BoardTO getBoardHistory(@RequestParam(value="game") Long gameId, 
			@RequestParam(value="move") Long moveNumber) {
		try {
			return gameService.getBoardHistory(gameId, moveNumber);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}
	
	@RequestMapping(value = "/{id}/tie/suggest", method = RequestMethod.PUT)
	@ResponseBody
	public void suggestTie(@PathVariable Long id) {
		long userId = authenticationFacade.getUserId();
		try {
			gameService.suggestTie(id, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/{id}/tie/accept", method = RequestMethod.PUT)
	@ResponseBody
	public void acceptTie(@PathVariable Long id) {
		long userId = authenticationFacade.getUserId();
		try {
			gameService.acceptTie(id, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/{id}/giveup", method = RequestMethod.PUT)
	@ResponseBody
	public void giveupGame(@PathVariable Long id) {
		long userId = authenticationFacade.getUserId();
		try {
			gameService.giveupGame(id, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody
	public List<GameTO> getGames() {
		long userId = authenticationFacade.getUserId();
		return gameService.getGames(userId);
	}
}
