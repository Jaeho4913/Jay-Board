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

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

	private final CustomLoginSuccessHandler customLoginSuccessHandler;
	private final CustomLoginFailureHandler customLoginFailureHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

 	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth

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
							"/member/save",
							"/member/login",
							"/member/find"
					).permitAll()

					.requestMatchers(
							"/write",
							"/board/save",
							"/board/update",
							"/board/update/**",
							"/board/delete",
							"/board/delete/**",
							"/board/like",
							"/board/reply/save",
							"/board/reply/update",
							"/board/reply/delete",
							"/member/updatePw"
					).authenticated()

					.anyRequest().permitAll()
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
				);

			return http.build();
	}
}
