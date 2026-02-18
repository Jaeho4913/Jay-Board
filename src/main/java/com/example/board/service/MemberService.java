package com.example.board.service;

import com.example.board.dto.MemberDTO;
import com.example.board.dto.LoginDTO;

import java.util.Date;
public interface MemberService {
	void save(MemberDTO memberDTO);

	MemberDTO login(LoginDTO loginDTO);
	MemberDTO checkExistID(String id);
	MemberDTO checkExistEmail(String email);
	MemberDTO findId(MemberDTO memberDTO);
	MemberDTO findPw(MemberDTO memberDTO);
}
