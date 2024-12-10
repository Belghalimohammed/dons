package com.isima.dons.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.isima.dons.configuration.UserPrincipale;
import com.isima.dons.entities.User;
import com.isima.dons.repositories.UserRepository;


@Service
public class Usersdetailservice implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepo.findByUsername(username);
		if(user==null) {
			System.out.println("user not found");
		}
		
		return new UserPrincipale(user);
	}

}