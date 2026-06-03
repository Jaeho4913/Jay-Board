package com.example.board.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.board.dto.MemberDTO;
import com.example.board.mapper.MemberMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler{

	private final MemberMapper memberMapper;

	@Override
	public void onAuthenticationSuccess(
				HttpServletRequest request,
				HttpServletResponse response,
				Authentication authentication
	) throws IOException, ServletException {

		String userId = authentication.getName();

		MemberDTO memberDTO = memberMapper.findByUserId(userId);

		request.getSession().setAttribute("loginMember", memberDTO);

		response.sendRedirect("/");
	}

}
