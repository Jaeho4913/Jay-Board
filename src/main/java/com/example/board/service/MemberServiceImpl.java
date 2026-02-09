package com.example.board.service;

import java.util.Date;

import lombok.Data;

import com.example.board.dto.MemberDTO;
import com.example.board.dto.LoginDTO;
import com.example.board.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class MemberServiceImpl implements MemberService{

	@Autowired
	private MemberMapper memberMapper;
	
	@Override
	public void save(MemberDTO memberDTO) {
		memberMapper.save(memberDTO);
	}
	@Override
	public MemberDTO login(LoginDTO loginDTO) {
		MemberDTO member = memberMapper.findByUserId(loginDTO.getUserId());
		
		if(member != null) {
			
			if (member.getPassword().equals(loginDTO.getPassword())) {
				return member;
			}
		}
		return null;
	}
	@Override
	public MemberDTO checkExistID(String id) {
		return memberMapper.checkExistID(id);
	}
}