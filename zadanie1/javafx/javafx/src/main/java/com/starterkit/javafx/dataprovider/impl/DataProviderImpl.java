package com.starterkit.javafx.dataprovider.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.starterkit.javafx.dataprovider.DataProvider;
import com.starterkit.javafx.dataprovider.data.PlayerVO;

/**
 * Provides data. Data is stored locally in this object. Additionally a call
 * delay is simulated.
 *
 * @author Leszek
 */
public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	/**
	 * Delay (in ms) for method calls.
	 */

	@Override
	public Collection<PlayerVO> findPlayer(String login, String name, String surname) {
		LOG.debug("Entering findUsers()");
		
		RestTemplate restTemplate = new RestTemplate();
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8090/user/search");
		 
        if (login != null && login.isEmpty() == false) {
            builder.queryParam("login", login);
        }
        if (name != null && name.isEmpty() == false) {
            builder.queryParam("name", name);
        }
        if (surname != null && surname.isEmpty() == false) {
            builder.queryParam("surname", surname);
        }
        
        ResponseEntity<List<PlayerVO>> response =
                restTemplate.exchange(builder.build().encode().toUri(),
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerVO>>(){});
        List<PlayerVO> users = response.getBody();
		
		return users;
	}

	@Override
	public void deletePlayer(Long id) {
		LOG.debug("Entering deleteUser(Long id)");
		
		RestTemplate restTemplate = new RestTemplate();
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:8090/user/" + id);
		
		restTemplate.delete(builder.build().encode().toUri());
		
	}
	
	@Override
	public void savePlayer(PlayerVO player) {
		LOG.debug("Entering update User(Long id)");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put("http://localhost:8090/user/", player); 
		
	}
}
