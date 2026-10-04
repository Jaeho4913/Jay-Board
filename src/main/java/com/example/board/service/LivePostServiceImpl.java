package com.example.board.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.board.dto.BoardGroupDTO;
import com.example.board.dto.LivePostDTO;
import com.example.board.dto.LivePostListResponseDTO;
import com.example.board.mapper.BoardGroupMapper;
import com.example.board.mapper.LivePostMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivePostServiceImpl implements LivePostService{

	private final LivePostMapper livePostMapper;
	private final BoardGroupMapper boardGroupMapper; 
	
	@Override
	public LivePostListResponseDTO findPosts(Integer boardGroupIdx, Long beforePostIdx) {
		BoardGroupDTO boardGroup = boardGroupMapper.findActiveBoardGroupByIdx(boardGroupIdx);
		
		if (boardGroup == null) {
			throw new IllegalArgumentException("존재하지 않거나 비활성화된 게시판입니다.");
		}
		if (!"LIVE".equals(boardGroup.getBoardType())) {
			throw new IllegalArgumentException("라이브형 게시판이 아닙니다.");
		}
		if (beforePostIdx != null && beforePostIdx <= 0) {
			throw new IllegalArgumentException("올바르지 않은 조회입니다.");
		}
		
		int pageSize = 20;
		List<LivePostDTO> posts = livePostMapper.findPosts(boardGroupIdx, beforePostIdx , pageSize + 1);
		
		boolean hasNext = posts.size() > pageSize;
		
		if (hasNext) {
			posts = posts.subList(0, pageSize);
		}
		
		Long nextCursor = null;
		
		if (!posts.isEmpty()) {
			nextCursor = posts.get(posts.size() - 1).getLivePostIdx();
		}
		
		LivePostListResponseDTO response = new LivePostListResponseDTO();
		
		response.setItems(posts);
		response.setHasNext(hasNext);
		response.setNextCursor(nextCursor);
		
		return response;
	}
	
}
