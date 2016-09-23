package com.capgemini.chess.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capgemini.chess.rest.exceptions.ForbiddenException;
import com.capgemini.chess.rest.exceptions.NotFoundException;
import com.capgemini.chess.service.AuthenticationFacade;
import com.capgemini.chess.service.ChallengeService;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.to.ChallengeLineTO;
import com.capgemini.chess.service.to.PendingChallengesTO;
import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Challenge controller.
 * 
 * @author Michal Bejm
 *
 */
@Controller
@RequestMapping("/challenge")
public class ChallengeRestController {
	
	private ChallengeService challengeService;
	private AuthenticationFacade authenticationFacade;
	
	@Autowired
	public ChallengeRestController(ChallengeService challengeService,
			AuthenticationFacade authenticationFacade) {
		this.challengeService = challengeService;
		this.authenticationFacade = authenticationFacade;
	}

	@RequestMapping(value = "/suggestions", method = RequestMethod.GET)
	@ResponseBody
	public List<ChallengeLineTO> getChallengeSuggestions() {
		long userId = authenticationFacade.getUserId();
		return challengeService.getChallengeSuggestions(userId);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PendingChallengesTO getPendingChallenges() {
		long userId = authenticationFacade.getUserId();
		return challengeService.getPendingChallenges(userId);
	}
	
	@RequestMapping(value = "/{receiverId}", method = RequestMethod.POST)
	@ResponseBody
	public ChallengeLineTO createChallenge(@PathVariable Long receiverId) {
		long userId = authenticationFacade.getUserId();
		try {
			return challengeService.createChallenge(receiverId, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}
	
	@RequestMapping(value = "/{id}/accept", method = RequestMethod.PUT)
	@ResponseBody
	public void acceptChallenge(@PathVariable Long id) {
		long userId = authenticationFacade.getUserId();
		try {
			challengeService.acceptChallenge(id, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/{id}/decline", method = RequestMethod.PUT)
	@ResponseBody
	public void declineChallenge(@PathVariable Long id) {
		long userId = authenticationFacade.getUserId();
		try {
			challengeService.declineChallenge(id, userId);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		} catch (InvalidUserException e) {
			throw new ForbiddenException(e);
		}
	}
	
	@RequestMapping(value = "/user/{login}", method = RequestMethod.GET)
	@ResponseBody
	public UserStatsTO findChallengedPlayer(@PathVariable String login) {
		try {
			return challengeService.findChallengedPlayer(login);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}
}
