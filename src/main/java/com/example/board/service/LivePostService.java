package com.example.board.service;

import com.example.board.dto.LivePostCreateRequestDTO;
import com.example.board.dto.LivePostDTO;
import com.example.board.dto.LivePostListResponseDTO;

public interface LivePostService {
	LivePostListResponseDTO findPosts(Integer boardGroupIdx, Long beforePostIdx);
	LivePostDTO createPost(LivePostCreateRequestDTO request, String userId);
	void deletePost(Long livePostIdx, String userId);
}
