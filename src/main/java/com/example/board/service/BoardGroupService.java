package com.example.board.service;

import java.util.List;

import com.example.board.dto.BoardGroupDTO;

public interface BoardGroupService {
	
	List<BoardGroupDTO> getActiveBoardGroups();
	
}
