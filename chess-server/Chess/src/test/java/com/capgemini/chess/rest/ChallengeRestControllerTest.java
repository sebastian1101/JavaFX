package com.capgemini.chess.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.capgemini.chess.service.ChallengeService;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.to.ChallengeLineTO;
import com.capgemini.chess.service.to.ChallengeTO;
import com.capgemini.chess.service.to.PendingChallengesTO;
import com.capgemini.chess.service.to.UserStatsTO;

public class ChallengeRestControllerTest {

	@InjectMocks
    private ChallengeRestController controller;

    @Mock
    private ChallengeService service;
    
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
    public void testGetChallengeSuggestions() throws Exception {
    	// given
    	ChallengeLineTO challenge1 = new ChallengeLineTO();
    	challenge1.setId(1L);
    	challenge1.setProfit(100);
    	challenge1.setLoss(80);
    	ChallengeLineTO challenge2 = new ChallengeLineTO();
    	challenge2.setId(2L);
    	challenge2.setProfit(45);
    	challenge2.setLoss(60);
    	
    	Mockito.when(service.getChallengeSuggestions(1L))
    		.thenReturn(Arrays.asList(challenge1, challenge2));
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/challenge/suggestions")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getChallengeSuggestions(1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("[0].id").value(1))
    		.andExpect(jsonPath("[0].profit").value(100))
    		.andExpect(jsonPath("[0].loss").value(80))
    		.andExpect(jsonPath("[1].id").value(2))
    		.andExpect(jsonPath("[1].profit").value(45))
    		.andExpect(jsonPath("[1].loss").value(60));
    }
    
    @Test
    public void testGetPendingChallenges() throws Exception {
    	// given
    	PendingChallengesTO pendingChallenges = new PendingChallengesTO();
    	ChallengeLineTO challenge1 = new ChallengeLineTO();
    	challenge1.setId(1L);
    	challenge1.setProfit(100);
    	challenge1.setLoss(80);
    	pendingChallenges.getSentChallenges().add(challenge1);
    	ChallengeLineTO challenge2 = new ChallengeLineTO();
    	challenge2.setId(2L);
    	challenge2.setProfit(45);
    	challenge2.setLoss(60);
    	pendingChallenges.getReceivedChallenges().add(challenge2);
    	ChallengeLineTO challenge3 = new ChallengeLineTO();
    	challenge3.setId(3L);
    	challenge3.setProfit(100);
    	challenge3.setLoss(80);
    	pendingChallenges.getReceivedChallenges().add(challenge3);
    	
    	Mockito.when(service.getPendingChallenges(1L))
    		.thenReturn(pendingChallenges);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/challenge")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getPendingChallenges(1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("sentChallenges[0].id").value(1))
    		.andExpect(jsonPath("sentChallenges[0].profit").value(100))
    		.andExpect(jsonPath("sentChallenges[0].loss").value(80))
    		.andExpect(jsonPath("receivedChallenges[0].id").value(2))
    		.andExpect(jsonPath("receivedChallenges[0].profit").value(45))
    		.andExpect(jsonPath("receivedChallenges[0].loss").value(60))
    		.andExpect(jsonPath("receivedChallenges[1].id").value(3))
    		.andExpect(jsonPath("receivedChallenges[1].profit").value(100))
    		.andExpect(jsonPath("receivedChallenges[1].loss").value(80));
    }
    
    @Test
    public void testCreateChallengeSuccessful() throws Exception {
    	// given
    	ChallengeLineTO challenge = new ChallengeLineTO();
    	challenge.setId(1L);
    	challenge.setProfit(100);
    	challenge.setLoss(80);
    	
    	Mockito.when(service.createChallenge(2L, 1L))
    		.thenReturn(challenge);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(post("/challenge/2")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).createChallenge(2L, 1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.id").value(1))
    		.andExpect(jsonPath("$.profit").value(100))
    		.andExpect(jsonPath("$.loss").value(80));
    }
    
    @Test
    public void testCreateChallengeEntityNotFound() throws Exception {
    	// given
    	ChallengeTO challenge = new ChallengeTO();
    	challenge.setId(1L);
    	challenge.setAccepted(true);
    	
    	Mockito.when(service.createChallenge(2L, 1L))
    		.thenThrow(new EntityNotFoundException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(post("/challenge/2")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).createChallenge(2L, 1L);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testAcceptChallengeEntityNotFound() throws Exception {
    	// given
    	long challengeId = 1L;
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).acceptChallenge(challengeId, 1L);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/challenge/"+challengeId+"/accept")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).acceptChallenge(challengeId, 1L);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testAcceptChallengeInvalidUser() throws Exception {
    	// given
    	long challengeId = 1L;
    	
    	Mockito.doThrow(new InvalidUserException()).when(service).acceptChallenge(challengeId, 1L);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/challenge/"+challengeId+"/accept")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).acceptChallenge(challengeId, 1L);
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testDeclineChallengeSuccessful() throws Exception {
    	// given
    	long challengeId = 1L;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/challenge/"+challengeId+"/decline")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).declineChallenge(challengeId, 1L);
    	
    	resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void testDeclineChallengeEntityNotFound() throws Exception {
    	// given
    	long challengeId = 1L;
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).declineChallenge(challengeId, 1L);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/challenge/"+challengeId+"/decline")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).declineChallenge(challengeId, 1L);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testDeclineChallengeInvalidUser() throws Exception {
    	// given
    	long challengeId = 1L;
    	
    	Mockito.doThrow(new InvalidUserException()).when(service).declineChallenge(challengeId, 1L);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/challenge/"+challengeId+"/decline")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).declineChallenge(challengeId, 1L);
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testFindChallengedPlayerSuccessful() throws Exception {
    	// given
    	String login = "user";
    	UserStatsTO user = new UserStatsTO();
    	user.setLogin(login);
    	user.setPoints(1000);
    	user.setLevel(Level.BEGINNER);
    	
    	Mockito.when(service.findChallengedPlayer(login))
    		.thenReturn(user);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/challenge/user/"+login)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).findChallengedPlayer(login);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.login").value(login))
    		.andExpect(jsonPath("$.points").value(1000))
    		.andExpect(jsonPath("$.level").value(Level.BEGINNER.toString()));
    }
    
    @Test
    public void testFindChallengedPlayerEntityNotFound() throws Exception {
    	// given
    	String login = "user";
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).findChallengedPlayer(login);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/challenge/user/"+login)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).findChallengedPlayer(login);
    	
    	resultActions.andExpect(status().isNotFound());
    }
}
