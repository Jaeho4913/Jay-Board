package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.board.security.AdminUserDetailsService;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AdminSecurityConfig {
	private final AdminUserDetailsService adminUserDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Bean
	public DaoAuthenticationProvider adminAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	@Order(1)
	public SecurityFilterChain admSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/admin/**")
			.csrf(csrf -> csrf.disable())
			.authenticationProvider(adminAuthenticationProvider())
			.authorizeHttpRequests(auth -> auth
					.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

					.requestMatchers(
							"/admin/login",
							"/admin/loginPost",
							"/css/**",
							"/js/**",
							"/images/**",
							"/favicon.ico"
					).permitAll()

					.requestMatchers("/admin/admins/**").hasRole("MASTER")

					.requestMatchers("/admin/**").hasAnyRole("MASTER", "MANAGER", "VIEWER")

					.anyRequest().denyAll()
			)
			.formLogin(form -> form
					.loginPage("/admin/login")
					.loginProcessingUrl("/admin/loginPost")
					.usernameParameter("adminId")
					.passwordParameter("password")
					.defaultSuccessUrl("/admin", true)
					.failureUrl("/admin/login?error")
					.permitAll()
			 )
			.logout(logout -> logout
					.logoutUrl("/admin/logout")
					.logoutSuccessUrl("/admin/login?logout")
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.permitAll()
			);

		return http.build();

	}
}
