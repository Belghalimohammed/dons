package com.isima.dons.configuration;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.isima.dons.entities.User;



public class UserPrincipale implements UserDetails{

	private User user;
	
	public UserPrincipale(User user) {
		this.user = user;
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Long getId() {
		// TODO Auto-generated method stub
		return this.user.getId();
	}


	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.user.getUsername();
	}

}
