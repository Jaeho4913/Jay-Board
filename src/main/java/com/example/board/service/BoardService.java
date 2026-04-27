package com.example.board.service;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;

import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;


public interface BoardService {
	PageResponseDTO findAll(SearchDTO searchDTO);
	
	void save(BoardDTO boardDTO);
	BoardDTO findById(Long idx);
	void update(BoardDTO boardDTO);
	void delete(Long idx);
	void updateViewCnt(Long idx);
	LikeResponseDTO btnLike(Long idx, String userId);
}
