package com.example.board.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.dto.LivePostListResponseDTO;
import com.example.board.service.LivePostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LivePostController {
	
	private final LivePostService livePostService;
	
	@GetMapping("/board/live/posts")
	public LivePostListResponseDTO findPosts(
				@RequestParam ("boardGroupIdx") Integer boardGroupIdx,
				@RequestParam(value = "beforePostIdx", required=false) Long beforePostIdx) {
		return livePostService.findPosts(boardGroupIdx, beforePostIdx);
	}
}
