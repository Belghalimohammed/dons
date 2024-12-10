package com.isima.dons.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsService myUserDetailsService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpsecurity) throws Exception {
		httpsecurity.csrf(csrf -> csrf.disable());
		httpsecurity.authorizeHttpRequests(request -> request
				.requestMatchers("login", "signup", "/h2-console/**").permitAll()
				.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login") // URL of your custom login form
						.defaultSuccessUrl("/", true) // Redirect after successful login
						.permitAll())
				.logout(logout -> logout.permitAll());
		//// to allow http authentication (Postam or server )
		return httpsecurity.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(myUserDetailsService);
		return provider;
	}

}
