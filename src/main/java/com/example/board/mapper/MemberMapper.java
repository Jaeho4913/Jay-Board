package com.example.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.example.board.dto.MemberDTO;
import com.example.board.dto.LoginDTO;


@Mapper
public interface MemberMapper {
	void save(MemberDTO memberDTO);
	MemberDTO findById(Long userId);
	MemberDTO findByUserId(String userId);
	MemberDTO checkExistID(String id);
}
