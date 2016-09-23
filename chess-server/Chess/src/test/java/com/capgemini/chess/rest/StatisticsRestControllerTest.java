package com.capgemini.chess.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.capgemini.chess.dataaccess.enumerations.Level;
import com.capgemini.chess.service.AuthenticationFacade;
import com.capgemini.chess.service.StatisticsService;
import com.capgemini.chess.service.to.UserStatsTO;

public class StatisticsRestControllerTest {

	@InjectMocks
    private StatisticsRestController controller;

    @Mock
    private StatisticsService service;
    
    @Mock
    private AuthenticationFacade facade;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
        Mockito.when(facade.getUserId()).thenReturn(1L);
    }
	
	@Test
	public void testGetUserStats() throws Exception {
		// given
		UserStatsTO userStats = new UserStatsTO();
		userStats.setId(1L);
		userStats.setLevel(Level.MASTER);
		userStats.setLogin("user");
    	userStats.setPoints(1000);
    	userStats.setGamesDrawn(2);
    	userStats.setGamesLost(2);
    	userStats.setGamesWon(6);
    	userStats.setGamesPlayed(10);

    	Mockito.when(service.getUserStats(1L)).thenReturn(userStats);

    	// when
    	ResultActions resultActions = mockMvc.perform(get("/stats")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getUserStats(1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.id").value((int) userStats.getId()))
    		.andExpect(jsonPath("$.level").value(Level.MASTER.toString()))
    		.andExpect(jsonPath("$.login").value("user"))
    		.andExpect(jsonPath("$.points").value(1000))
    		.andExpect(jsonPath("$.gamesLost").value(2))
    		.andExpect(jsonPath("$.gamesDrawn").value(2))
    		.andExpect(jsonPath("$.gamesWon").value(6))
    		.andExpect(jsonPath("$.gamesPlayed").value(10));
	}
	
	@Test
	public void testReadLeaderboard() throws Exception {
		// given
    	UserStatsTO user1 = new UserStatsTO();
    	user1.setId(1L);
    	user1.setLevel(Level.MASTER);
    	user1.setLogin("user1");
    	user1.setName("name");
    	user1.setPoints(1000);
    	user1.setSurname("surname");
    	UserStatsTO user2= new UserStatsTO();
    	user2.setId(2L);
    	user2.setLevel(Level.ADVANCED);
    	user2.setLogin("user2");
    	user2.setName("name");
    	user2.setSurname("surname");
    	user2.setPoints(2000);
    	
    	Mockito.when(service.readLeaderboard(1L)).thenReturn(Arrays.asList(user1, user2));
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/stats/leaderboard")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).readLeaderboard(1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("[0].id").value((int) user1.getId()))
    		.andExpect(jsonPath("[0].level").value(Level.MASTER.toString()))
    		.andExpect(jsonPath("[0].login").value("user1"))
    		.andExpect(jsonPath("[0].name").value("name"))
    		.andExpect(jsonPath("[0].points").value(1000))
    		.andExpect(jsonPath("[0].surname").value("surname"))
			.andExpect(jsonPath("[1].id").value((int) user2.getId()))
			.andExpect(jsonPath("[1].level").value(Level.ADVANCED.toString()))
			.andExpect(jsonPath("[1].login").value("user2"))
			.andExpect(jsonPath("[1].name").value("name"))
			.andExpect(jsonPath("[1].points").value(2000))
			.andExpect(jsonPath("[1].surname").value("surname"));
	}

}
