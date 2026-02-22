package com.example.board.service;

import java.util.Date;
import java.util.UUID;

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

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Override
	public void save(MemberDTO memberDTO) {
		String encodedPw = passwordEncoder.encode(memberDTO.getPassword());
		memberDTO.setPassword(encodedPw);
		memberMapper.save(memberDTO);
	}
	@Override
	public MemberDTO login(LoginDTO loginDTO) {
		MemberDTO member = memberMapper.findByUserId(loginDTO.getUserId());

		if(member != null) {

			if (passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
				return member;
			}
		}
		return null;
	}
	@Override
	public MemberDTO checkExistID(String id) {
		return memberMapper.checkExistID(id);
	}
	@Override
	public MemberDTO checkExistEmail(String email) {
		return memberMapper.checkExistEmail(email);
	}
	@Override
	public MemberDTO findId(MemberDTO memberDTO) {
		return memberMapper.findId(memberDTO);
	}
	@Override
	public MemberDTO findPw(MemberDTO memberDTO) {
		MemberDTO member = memberMapper.checkMember(memberDTO);

		if (member != null) {
			String tempPw = UUID.randomUUID().toString().replace("-","").substring(0, 8);
			member.setPassword(passwordEncoder.encode(tempPw));
			memberMapper.updatePassword(member);
			emailService.sendTempPasswordEmail(member.getEmail(), tempPw);
			return member;
			}
		return null;
	}
	@Override
	public boolean changePassword(String userId, String currentPw, String newPw) {
		MemberDTO member = memberMapper.findByUserId(userId);
		if (member != null && passwordEncoder.matches(currentPw, member.getPassword())) {
			member.setPassword(passwordEncoder.encode(newPw));
			memberMapper.updatePassword(member);

			return true;
		}
		return false;
	}
}