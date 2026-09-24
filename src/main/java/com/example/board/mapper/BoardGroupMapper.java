package com.example.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.board.dto.BoardGroupDTO;

@Mapper
public interface BoardGroupMapper {
	int countActiveBoardGroup(@Param("boardGroupIdx") Integer boardGroupIdx);
	List<BoardGroupDTO> findActiveBoardGroups();
}
