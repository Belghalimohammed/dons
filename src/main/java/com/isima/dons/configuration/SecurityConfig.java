package com.isima.dons.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;

import com.isima.dons.filters.JwtFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsService myUserDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher("/api/**")
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/api/login", "/api/signup").permitAll()
						.anyRequest().authenticated())
				.httpBasic(Customizer.withDefaults())
				.formLogin(form -> form.disable())
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint((request, response, authException) -> {
							response.setContentType("application/json");
							response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
							response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \""
									+ authException.getMessage() + "\"}");
						}))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
		RequestMatcher excludeApiMatcher = new NegatedRequestMatcher(new AntPathRequestMatcher("/api/**"));

		http
				.securityMatcher(excludeApiMatcher) // Apply security rules to all except "/api/**"
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(request -> request
						.requestMatchers("/login", "/signup", "/h2-console/**").permitAll()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/", true)
						.failureUrl("/login?error=true")
						.permitAll())
				.logout(logout -> logout.permitAll());

		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(myUserDetailsService);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}
