package com.capgemini.chess.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capgemini.chess.service.AuthenticationFacade;
import com.capgemini.chess.service.StatisticsService;
import com.capgemini.chess.service.to.UserStatsTO;

/**
 * Statisitcs controller.
 * 
 * @author Michal Bejm
 *
 */
@Controller
@RequestMapping("/stats")
public class StatisticsRestController {
	
	private StatisticsService statisticsService;
	private AuthenticationFacade authenticationFacade;
	
	@Autowired
	public StatisticsRestController(StatisticsService statisticsService, 
			AuthenticationFacade authenticationFacade) {
		this.statisticsService = statisticsService;
		this.authenticationFacade = authenticationFacade;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public UserStatsTO getUserStats() {
		long id = authenticationFacade.getUserId();
		return statisticsService.getUserStats(id);
	}
	
	@RequestMapping(value = "/leaderboard", method = RequestMethod.GET)
	@ResponseBody
	public List<UserStatsTO> readLeaderboard() {
		long id = authenticationFacade.getUserId();
		return statisticsService.readLeaderboard(id);
	}
}
