package com.example.board.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.board.dto.BoardImageDTO;

@Mapper
public interface BoardImageMapper {
	int insert(BoardImageDTO boardImage);
	BoardImageDTO findByBoardIdx(Long boardIdx);
	int deleteByBoardIdx(Long boardIdx);
}
