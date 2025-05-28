package com.cts.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cts.entity.UserCredentials;

public class CustomUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;
	private String password;
	
	
	public CustomUserDetails(UserCredentials userCredentials) {
		super();
		this.email = userCredentials.getEmail();
		this.password = userCredentials.getPassword();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}
	
	@Override
	public boolean isAccountNonExpired() {
	    return true; // Change this if you want to track account expiration
	}

	@Override
	public boolean isAccountNonLocked() {
	    return true; // Change this if you want to implement locking logic
	}

	@Override
	public boolean isCredentialsNonExpired() {
	    return true; // Change this if you want credential expiration logic
	}

	@Override
	public boolean isEnabled() {
	    return true; // Change this based on whether the user should be active
	}


}
