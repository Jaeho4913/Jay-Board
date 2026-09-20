package com.example.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.board.dto.BoardGroupDTO;
import com.example.board.mapper.BoardGroupMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardGroupServiceImpl implements BoardGroupService{
	
	private final BoardGroupMapper boardGroupMapper;
	
	@Override
	public List<BoardGroupDTO>getActiveBoardGroups() {
		return boardGroupMapper.findActiveBoardGroups();
	}
}
