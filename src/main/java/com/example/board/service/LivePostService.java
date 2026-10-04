package com.example.board.service;

import com.example.board.dto.LivePostListResponseDTO;

public interface LivePostService {
	LivePostListResponseDTO findPosts(Integer boardGroupIdx, Long beforePostIdx);
}
