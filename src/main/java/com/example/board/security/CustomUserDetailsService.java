package com.example.board.security;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.board.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

import com.example.board.dto.MemberDTO;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

	private final MemberMapper memberMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		MemberDTO memberDTO = memberMapper.findByUserId(username);

		if(memberDTO == null) {
			throw new UsernameNotFoundException("존재하지 않는 회원입니다.");
		}

		return new CustomUserDetails(memberDTO);
	}

}
