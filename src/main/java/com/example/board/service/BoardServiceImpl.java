package com.example.board.service;

import java.util.List;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.mapper.BoardMapper;

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
