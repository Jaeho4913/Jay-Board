package com.example.board.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.board.security.CustomAuthenticationEntryPoint;
import com.example.board.security.CustomLoginFailureHandler;
import com.example.board.security.CustomLoginSuccessHandler;
import com.example.board.security.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	private final CustomLoginSuccessHandler customLoginSuccessHandler;
	private final CustomLoginFailureHandler customLoginFailureHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomUserDetailsService customUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private DaoAuthenticationProvider userAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(customUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authenticationProvider(userAuthenticationProvider())
			.authorizeHttpRequests(auth -> auth
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

				.requestMatchers(
					"/css/**",
					"/js/**",
					"/images/**",
					"/favicon.ico"
				).permitAll()

				.requestMatchers(
					"/",
					"/board/list",
					"/api/boards",
					"/board/view",
					"/board/getDetail",
					"/board/replies",
					"/board/likeUsers",
					"/member/save",
					"/member/login",
					"/member/find",
					"/member/checkId/**",
					"/member/checkEmail/**",
					"/member/findId",
					"/member/findPw"
				).permitAll()

				.requestMatchers(
					"/write",
					"/board/save",
					"/board/update",
					"/board/delete",
					"/board/like",
					"/board/reply/save",
					"/board/reply/update",
					"/board/reply/delete",
					"/member/updatePw"
				).authenticated()

				.anyRequest().denyAll()
			)
			.formLogin(form -> form
				.loginPage("/member/login")
				.loginProcessingUrl("/member/loginPost")
				.usernameParameter("userId")
				.passwordParameter("password")
				.successHandler(customLoginSuccessHandler)
				.failureHandler(customLoginFailureHandler)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/member/logout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll()
			)
			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(customAuthenticationEntryPoint)
			);

		return http.build();
	}
}