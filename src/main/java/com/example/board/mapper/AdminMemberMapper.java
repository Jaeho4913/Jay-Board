package com.example.board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.board.dto.AdminMemberDTO;

@Mapper
public interface AdminMemberMapper {
	void save(AdminMemberDTO adminMemberDTO);
	AdminMemberDTO findByAdminId(@Param("adminId") String adminId);
}
