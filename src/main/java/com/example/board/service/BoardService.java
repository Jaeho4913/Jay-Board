package com.example.board.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;


import com.example.board.dto.BoardDTO;
import com.example.board.dto.BoardImageDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.LikeUserDTO;
import com.example.board.dto.MemberDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;



public interface BoardService {
	PageResponseDTO findAll(SearchDTO searchDTO);

	void save(BoardDTO boardDTO, MultipartFile imageFile);
	BoardDTO findById(Long idx);
	void update(BoardDTO boardDTO, MultipartFile imageFile, boolean deleteImage);
	void delete(Long idx);
	void updateViewCnt(Long idx);
	LikeResponseDTO btnLike(Long idx, String userId);
	int countLike(Long idx);
	int existsLike(Long idx, String userId);
	List<LikeUserDTO> findLikeUsers(Long idx);
	int countLikeUsers(Long idx);
	List<MemberDTO> findLikeUsersPaging(Long idx, int size, int offset);
	BoardImageDTO findImageByBoardIdx(Long boardIdx);
}
