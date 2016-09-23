package com.capgemini.chess.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.capgemini.chess.service.UserService;
import com.capgemini.chess.service.exceptions.EntityExistsException;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.to.UserProfileTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRestControllerTest {

	@InjectMocks
    private UserRestController controller;

    @Mock
    private UserService service;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void testReadUserSuccessful() throws Exception {
    	// given
    	UserProfileTO user = new UserProfileTO();
    	user.setAboutMe("about me");
    	user.setEmail("user@user.com");
    	user.setId(1L);
    	user.setLifeMotto("motto");
    	user.setLogin("user");
    	user.setName("name");
    	user.setPassword("password");
    	user.setSurname("surname");
    	
    	Mockito.when(service.findUserProfileByLogin("user")).thenReturn(user);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/user/user")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).findUserProfileByLogin("user");
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.aboutMe").value("about me"))
    		.andExpect(jsonPath("$.email").value("user@user.com"))
    		.andExpect(jsonPath("$.id").value((int) user.getId()))
    		.andExpect(jsonPath("$.lifeMotto").value("motto"))
    		.andExpect(jsonPath("$.login").value("user"))
    		.andExpect(jsonPath("$.name").value("name"))
    		.andExpect(jsonPath("$.password").doesNotExist())
    		.andExpect(jsonPath("$.surname").value("surname"));
    }
    
    @Test
    public void testReadUserNotFound() throws Exception {
    	// given
    	Mockito.when(service.findUserProfileByLogin("user")).thenThrow(new EntityNotFoundException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/user/user")
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).findUserProfileByLogin("user");
    	
    	resultActions.andExpect(status().isNotFound());
    }
    
    @Test
    public void testCreateUserSuccessful() throws Exception {
    	// given
    	UserProfileTO user = new UserProfileTO();
    	user.setAboutMe("about me");
    	user.setEmail("user@user.com");
    	user.setId(1L);
    	user.setLifeMotto("motto");
    	user.setLogin("user");
    	user.setName("name");
    	user.setPassword("password");
    	user.setSurname("surname");
    	ObjectMapper mapper = new ObjectMapper();
    	String userJson = mapper.writeValueAsString(user);
    	
    	Mockito.when(service.createUser(Matchers.any(UserProfileTO.class))).thenReturn(user);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(post("/user").content(userJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.aboutMe").value("about me"))
    		.andExpect(jsonPath("$.email").value("user@user.com"))
    		.andExpect(jsonPath("$.id").value((int) user.getId()))
    		.andExpect(jsonPath("$.lifeMotto").value("motto"))
    		.andExpect(jsonPath("$.login").value("user"))
    		.andExpect(jsonPath("$.name").value("name"))
    		.andExpect(jsonPath("$.password").doesNotExist())
    		.andExpect(jsonPath("$.surname").value("surname"));
    }

    @Test
    public void testCreateUserEntityExists() throws Exception {
    	// given
    	UserProfileTO user = new UserProfileTO();
    	user.setAboutMe("about me");
    	user.setEmail("user@user.com");
    	user.setId(1L);
    	user.setLifeMotto("motto");
    	user.setLogin("user");
    	user.setName("name");
    	user.setPassword("password");
    	user.setSurname("surname");
    	ObjectMapper mapper = new ObjectMapper();
    	String userJson = mapper.writeValueAsString(user);
    	
    	Mockito.when(service.createUser(Matchers.any(UserProfileTO.class))).thenThrow(new EntityExistsException());
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(post("/user").content(userJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	resultActions.andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUser() throws Exception {
    	// given
    	UserProfileTO user = new UserProfileTO();
    	user.setAboutMe("about me");
    	user.setEmail("user@user.com");
    	user.setId(1L);
    	user.setLifeMotto("motto");
    	user.setLogin("user");
    	user.setName("name");
    	user.setPassword("password");
    	user.setSurname("surname");
    	ObjectMapper mapper = new ObjectMapper();
    	String userJson = mapper.writeValueAsString(user);
    	
    	Mockito.when(service.updateUser(Matchers.any(UserProfileTO.class))).thenReturn(user);
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(put("/user").content(userJson)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("$.aboutMe").value("about me"))
    		.andExpect(jsonPath("$.email").value("user@user.com"))
    		.andExpect(jsonPath("$.id").value((int) user.getId()))
    		.andExpect(jsonPath("$.lifeMotto").value("motto"))
    		.andExpect(jsonPath("$.login").value("user"))
    		.andExpect(jsonPath("$.name").value("name"))
    		.andExpect(jsonPath("$.password").doesNotExist())
    		.andExpect(jsonPath("$.surname").value("surname"));
    }

    @Test
    public void testDeleteUser() throws Exception {
    	// given
    	long id = 1L;
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(delete("/user/"+id)
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).deleteUser(id);
    	
    	resultActions.andExpect(status().isOk());
    }
    
    @Test
    public void testFindUsers() throws Exception {
    	// given
    	UserProfileTO user = new UserProfileTO();
    	user.setAboutMe("about me");
    	user.setEmail("user@user.com");
    	user.setId(1L);
    	user.setLifeMotto("motto");
    	user.setLogin("user");
    	user.setName("name");
    	user.setPassword("password");
    	user.setSurname("surname");
    	
    	Mockito.when(service.findUsers(user.getLogin(), null, user.getSurname()))
    		.thenReturn(Arrays.asList(user));
    	
    	// when
    	ResultActions resultActions = mockMvc.perform(get("/user/search?login=" + user.getLogin()
    				+ "&surname=" + user.getSurname())
    			.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print());
    	
    	// then 
    	Mockito.verify(service).findUsers(user.getLogin(), null, user.getSurname());
    	
    	resultActions.andExpect(status().isOk())
    		.andExpect(jsonPath("[0].aboutMe").value("about me"))
    		.andExpect(jsonPath("[0].email").value("user@user.com"))
    		.andExpect(jsonPath("[0].id").value((int) user.getId()))
    		.andExpect(jsonPath("[0].lifeMotto").value("motto"))
    		.andExpect(jsonPath("[0].login").value("user"))
    		.andExpect(jsonPath("[0].name").value("name"))
    		.andExpect(jsonPath("[0].password").doesNotExist())
    		.andExpect(jsonPath("[0].surname").value("surname"));
    }
}
