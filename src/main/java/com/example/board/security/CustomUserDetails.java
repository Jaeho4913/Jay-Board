package com.example.board.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.board.dto.MemberDTO;

public class CustomUserDetails implements UserDetails {

	private final MemberDTO memberDTO;

	public CustomUserDetails(MemberDTO memberDTO) {
		this.memberDTO = memberDTO;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}

	@Override
	public String getPassword() {
		return memberDTO.getPassword();
	}

	@Override
	public String getUsername() {
		return memberDTO.getUserId();
	}
	public MemberDTO getMemberDTO() {
		return memberDTO;
	}
}
