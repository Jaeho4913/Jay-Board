package com.example.board.service;

import java.util.List;

import com.example.board.dto.BoardGroupDTO;
import com.example.board.dto.BoardImageDTO;

public interface BoardGroupService {
	
	List<BoardGroupDTO> getActiveBoardGroups();
}
