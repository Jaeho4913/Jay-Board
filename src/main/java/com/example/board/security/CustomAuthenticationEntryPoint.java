package com.example.board.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

	@Override
	public void commence(HttpServletRequest request,
				HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {

				String requestedWith = request.getHeader("X-Requseted-With");
				String accept = request.getHeader("Accept");

				boolean isAjax = "XMLHttpRequest".equals(requestedWith)
						|| (accept != null && accept.contains("application/json"));

				if(isAjax) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"status\":\"loginRequired\"}");
					return;
				}
				response.sendRedirect("/member/login");
		}
}
