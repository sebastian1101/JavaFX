package com.capgemini.chess.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.capgemini.chess.dataaccess.enumerations.GameState;
import com.capgemini.chess.dataaccess.enumerations.MoveType;
import com.capgemini.chess.dataaccess.enumerations.Piece;
import com.capgemini.chess.service.AuthenticationFacade;
import com.capgemini.chess.service.GameService;
import com.capgemini.chess.service.enumerations.BoardState;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.exceptions.InvalidMoveException;
import com.capgemini.chess.service.exceptions.InvalidUserException;
import com.capgemini.chess.service.manager.BoardManager;
import com.capgemini.chess.service.to.BoardTO;
import com.capgemini.chess.service.to.CoordinateTO;
import com.capgemini.chess.service.to.GameTO;
import com.capgemini.chess.service.to.MoveTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameRestControllerTest {

	@InjectMocks
    private GameRestController controller;

    @Mock
    private GameService service;
    
    @Mock
    private AuthenticationFacade facade;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
    	Mockito.when(facade.getUserId()).thenReturn(2L);
    }
    
    @Test
    public void testPerformMoveSuccessful() throws Exception {
    	// given
    	BoardTO board = new BoardTO();
    	MoveTO move = new MoveTO();
    	move.setFrom(new CoordinateTO(0, 0));
    	move.setTo(new CoordinateTO(1, 2));
    	move.setMovedPiece(Piece.BLACK_PAWN);
    	move.setSequenceNumber(0);
    	move.setType(MoveType.ATTACK);
    	board.getMoveHistory().add(move);
    	ObjectMapper mapper = new ObjectMapper();
    	String moveJson = mapper.writeValueAsString(move);
    	
    	Mockito.when(service.performMove(Matchers.eq(1L), Matchers.eq(2L), 
    			Matchers.any(MoveTO.class))).thenReturn(board);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/1/move").content(moveJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).performMove(Matchers.eq(1L), Matchers.eq(2L), Matchers.any(MoveTO.class));
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.moveHistory[0].from.x").value(0))
    		.andExpect(jsonPath("$.moveHistory[0].from.y").value(0))
    		.andExpect(jsonPath("$.moveHistory[0].to.x").value(1))
    		.andExpect(jsonPath("$.moveHistory[0].to.y").value(2))
    		.andExpect(jsonPath("$.moveHistory[0].movedPiece").value(Piece.BLACK_PAWN.toString()))
    		.andExpect(jsonPath("$.moveHistory[0].sequenceNumber").value(0))
    		.andExpect(jsonPath("$.moveHistory[0].type").value(MoveType.ATTACK.toString()));
    }
    
    @Test
    public void testPerformMoveInvalidMove() throws Exception {
    	// given
    	MoveTO move = new MoveTO();
    	move.setFrom(new CoordinateTO(0, 0));
    	move.setTo(new CoordinateTO(1, 2));
    	move.setMovedPiece(Piece.BLACK_PAWN);
    	move.setSequenceNumber(0);
    	move.setType(MoveType.ATTACK);
    	ObjectMapper mapper = new ObjectMapper();
    	String moveJson = mapper.writeValueAsString(move);
    	
    	Mockito.when(service.performMove(Matchers.eq(1L), Matchers.eq(2L), 
    			Matchers.any(MoveTO.class))).thenThrow(new InvalidMoveException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/1/move").content(moveJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).performMove(Matchers.eq(1L), Matchers.eq(2L), Matchers.any(MoveTO.class));
    	
    	resultActions.andExpect(status().isBadRequest());
    }
    
    @Test
    public void testPerformMoveEntityNotFound() throws Exception {
    	// given
    	MoveTO move = new MoveTO();
    	move.setFrom(new CoordinateTO(0, 0));
    	move.setTo(new CoordinateTO(1, 2));
    	move.setMovedPiece(Piece.BLACK_PAWN);
    	move.setSequenceNumber(0);
    	move.setType(MoveType.ATTACK);
    	ObjectMapper mapper = new ObjectMapper();
    	String moveJson = mapper.writeValueAsString(move);
    	
    	Mockito.when(service.performMove(Matchers.eq(1L), Matchers.eq(2L), 
    			Matchers.any(MoveTO.class))).thenThrow(new EntityNotFoundException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/1/move").content(moveJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).performMove(Matchers.eq(1L), Matchers.eq(2L), Matchers.any(MoveTO.class));
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testPerformMoveInvalidUser() throws Exception {
    	// given
    	MoveTO move = new MoveTO();
    	move.setFrom(new CoordinateTO(0, 0));
    	move.setTo(new CoordinateTO(1, 2));
    	move.setMovedPiece(Piece.BLACK_PAWN);
    	move.setSequenceNumber(0);
    	move.setType(MoveType.ATTACK);
    	ObjectMapper mapper = new ObjectMapper();
    	String moveJson = mapper.writeValueAsString(move);
    	
    	Mockito.when(service.performMove(Matchers.eq(1L), Matchers.eq(2L), 
    			Matchers.any(MoveTO.class))).thenThrow(new InvalidUserException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/1/move").content(moveJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).performMove(Matchers.eq(1L), Matchers.eq(2L), Matchers.any(MoveTO.class));
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testGetBoardSuccessful() throws Exception {
    	// given
    	BoardTO board = (new BoardManager()).getBoard();
    	board.setState(BoardState.REGULAR);
    	
    	Mockito.when(service.getBoard(1L)).thenReturn(board);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/game/1")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getBoard(1L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.state").value(BoardState.REGULAR.toString()))
    		.andExpect(jsonPath("$.pieces[0][0]").value(Piece.WHITE_ROOK.toString()));
    }
    
    @Test
    public void testGetBoardEntityNotFound() throws Exception {
    	// given
    	BoardTO board = (new BoardManager()).getBoard();
    	board.setState(BoardState.REGULAR);
    	
    	Mockito.when(service.getBoard(1L)).thenThrow(new EntityNotFoundException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/game/1")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getBoard(1L);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testGetBoardHistorySuccessful() throws Exception {
    	// given
    	BoardTO board = (new BoardManager()).getBoard();
    	board.setState(BoardState.REGULAR);
    	
    	Mockito.when(service.getBoardHistory(1L, 2L)).thenReturn(board);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/game/history?game=1&move=2")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getBoardHistory(1L, 2L);
    	
    	resultActions.andExpect(status().isOk())
			.andExpect(jsonPath("$.state").value(BoardState.REGULAR.toString()))
			.andExpect(jsonPath("$.pieces[0][0]").value(Piece.WHITE_ROOK.toString()));
    }
    
    @Test
    public void testGetBoardHistoryEntityNotFound() throws Exception {
    	// given    	
    	Mockito.when(service.getBoardHistory(1L, 2L)).thenThrow(new EntityNotFoundException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/game/history?game=1&move=2")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getBoardHistory(1L, 2L);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuggestTieSuccessful() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/suggest")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).suggestTie(gameId, userId);
    	
    	resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void testSuggestTieEntityNotFound() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).suggestTie(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/suggest")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).suggestTie(gameId, userId);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testSuggestTieInvalidUser() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new InvalidUserException()).when(service).suggestTie(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/suggest")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).suggestTie(gameId, userId);
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testAcceptTieSuccessful() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/accept")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).acceptTie(gameId, userId);
    	
    	resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void testAcceptTieEntityNotFound() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).acceptTie(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/accept")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).acceptTie(gameId, userId);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testAcceptTieInvalidUser() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new InvalidUserException()).when(service).acceptTie(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/tie/accept")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).acceptTie(gameId, userId);
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testGiveUpGameSuccessful() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/giveup")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).giveupGame(gameId, userId);
    	
    	resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void testGiveUpGameEntityNotFound() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new EntityNotFoundException()).when(service).giveupGame(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/giveup")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).giveupGame(gameId, userId);
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testGiveUpGameInvalidUser() throws Exception {
    	// given
    	long gameId = 1L;
    	long userId = 2L;
    	
    	Mockito.doThrow(new InvalidUserException()).when(service).giveupGame(gameId, userId);;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/game/"+gameId+"/giveup")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).giveupGame(gameId, userId);
    	
    	resultActions.andExpect(status().isForbidden());
    }
    
    @Test
    public void testGetGames() throws Exception {
    	// given
    	GameTO game1 = new GameTO();
    	game1.setId(1L);
    	game1.setState(GameState.IN_PROGRESS);
    	GameTO game2 = new GameTO();
    	game2.setId(2L);
    	game2.setState(GameState.TIE_SUGGESTED_BLACK);
    	
    	Mockito.when(service.getGames(2L)).thenReturn(Arrays.asList(game1, game2));
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/game/user")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).getGames(2L);
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("[0].id").value(1))
    		.andExpect(jsonPath("[0].state").value(GameState.IN_PROGRESS.toString()))
    		.andExpect(jsonPath("[1].id").value(2))
    		.andExpect(jsonPath("[1].state").value(GameState.TIE_SUGGESTED_BLACK.toString()));
    }
}
