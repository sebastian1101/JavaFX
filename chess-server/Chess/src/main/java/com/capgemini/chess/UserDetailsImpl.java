package com.capgemini.chess;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.capgemini.chess.service.to.UserProfileTO;

/**
 * Implementation of {@link UserDetails}.
 * 
 * @author Michal Bejm
 *
 */
public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = -5733784627299632673L;
	
	private final UserProfileTO user;
	
	public UserDetailsImpl(UserProfileTO user) {
		this.user = user;
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		GrantedAuthority authority = new GrantedAuthority() {

			private static final long serialVersionUID = -7208072816392910158L;

			@Override
            public String getAuthority() {
                return "USER";
            }
        };

        List<GrantedAuthority> authorities = Arrays.asList(authority);
        return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getLogin();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public long getId() {
		return user.getId();
	}

}
