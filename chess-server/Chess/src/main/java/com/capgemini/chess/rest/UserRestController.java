package com.capgemini.chess.rest;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.capgemini.chess.rest.exceptions.ConflictException;
import com.capgemini.chess.rest.exceptions.NotFoundException;
import com.capgemini.chess.service.UserService;
import com.capgemini.chess.service.exceptions.EntityExistsException;
import com.capgemini.chess.service.exceptions.EntityNotFoundException;
import com.capgemini.chess.service.to.UserProfileTO;

/**
 * User controller.
 * 
 * @author Michal Bejm
 *
 */
@Controller
@RequestMapping("/user")
public class UserRestController {
	
	UserService userService;
	
	@Autowired
	public UserRestController(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(value = "/principal", method = RequestMethod.GET)
	@ResponseBody
	public Principal user(Principal user) {
	    return user;
	}
	
	@RequestMapping(value = "/{login}", method = RequestMethod.GET)
	@ResponseBody
	public UserProfileTO readUser(@PathVariable String login) {
		try {
			return userService.findUserProfileByLogin(login);
		} catch (EntityNotFoundException e) {
			throw new NotFoundException(e);
		}
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public UserProfileTO updateUser(@RequestBody UserProfileTO userTo) {
		return userService.updateUser(userTo);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public UserProfileTO createUser(@RequestBody UserProfileTO userTo) {
		try {
			return userService.createUser(userTo);
		} catch (EntityExistsException e) {
			throw new ConflictException(e);
		}
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
	}
	
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	public List<UserProfileTO> findUsers(@RequestParam(value="login", required=false) String login, 
			@RequestParam(value="name", required=false) String name,
			@RequestParam(value="surname", required=false) String surname) {
		return userService.findUsers(login, name, surname);
	}
}
