package com.example.board.service;

import java.util.List;


import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.LikeUserDTO;
import com.example.board.dto.MemberDTO;
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
	int countLike(Long idx);
	int existsLike(Long idx, String userId);
	List<LikeUserDTO> findLikeUsers(Long idx);
	int countLikeUsers(Long idx);
	List<MemberDTO> findLikeUsersPaging(Long idx, int size, int offset);
}
