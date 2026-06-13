package com.example.board.config;

import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.board.security.CustomLoginFailureHandler;
import com.example.board.security.CustomLoginSuccessHandler;
import com.example.board.security.CustomAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.DispatcherType;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	private final CustomLoginSuccessHandler customLoginSuccessHandler;
	private final CustomLoginFailureHandler customLoginFailureHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

 	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
		http
			.csrf(csrf -> csrf.disable())
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
