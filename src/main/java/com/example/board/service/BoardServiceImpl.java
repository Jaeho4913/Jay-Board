package com.example.board.service;

import java.net.ResponseCache;
import java.util.List;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.mapper.BoardMapper;
import com.sun.net.httpserver.Authenticator.Success;


@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;

	@Override
	public PageResponseDTO findAll(SearchDTO searchDTO) {
		validateSortType(searchDTO);

		List<BoardDTO> list = boardMapper.findAll(searchDTO);
		int totalCount = boardMapper.count(searchDTO);

		return new PageResponseDTO(searchDTO, totalCount, list);
	}
	private void validateSortType(SearchDTO searchDTO) {
		String sortType = searchDTO.getSortType();

		if (sortType == null || sortType.trim().isEmpty()) {
			searchDTO.setSortType("latest");
			return;
		}
		switch(sortType) {
		case "latest":
		case "oldest":
		case "viewDesc":
		case "viewAsc":
			break;
		default:
			searchDTO.setSortType("latest");
		}
	}

	@Override
	public LikeResponseDTO btnLike(Long idx, String userId) {
		LikeResponseDTO response = new LikeResponseDTO();

		int exists = boardMapper.existsLike(idx, userId);

		if (exists > 0) {
			boardMapper.deleteLike(idx, userId);
			response.setLikeCheck(false);
		} else {
			boardMapper.insertLike(idx, userId);
			response.setLikeCheck(true);
		}
		int likeCnt = boardMapper.countLike(idx);

		response.setStatus("success");
		response.setLikeCnt(likeCnt);

		return response;
	}

	@Override
	public void save(BoardDTO boardDTO) {
		boardMapper.save(boardDTO);
	}
	@Override
	public BoardDTO findById(Long idx) {
		return boardMapper.findById(idx);
	}
	@Override
	public void update(BoardDTO boardDTO) {
		boardMapper.update(boardDTO);
	}
	@Override
	public void delete(Long idx) {
		boardMapper.delete(idx);
	}
	@Override
	public void updateViewCnt(Long idx) {
		boardMapper.updateViewCnt(idx);
	}
}
