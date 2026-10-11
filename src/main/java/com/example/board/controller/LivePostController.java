package com.example.board.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.board.dto.LivePostCreateRequestDTO;
import com.example.board.dto.LivePostDTO;
import com.example.board.dto.LivePostListResponseDTO;
import com.example.board.service.LivePostService;

import lombok.RequiredArgsConstructor;
import com.example.board.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
public class LivePostController {

	private final LivePostService livePostService;

	private boolean isLogin(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated()
				&& authentication.getPrincipal() instanceof CustomUserDetails;
	}

	@GetMapping("/board/live/posts")
	public LivePostListResponseDTO findPosts(@RequestParam("boardGroupIdx") Integer boardGroupIdx,
			@RequestParam(value = "beforePostIdx", required = false) Long beforePostIdx) {
		return livePostService.findPosts(boardGroupIdx, beforePostIdx);
	}

	@PostMapping("/board/live/posts")
	public ResponseEntity<LivePostDTO> createPost(@RequestBody LivePostCreateRequestDTO request,
			Authentication authentication) {
		if (!isLogin(authentication)) {
			return ResponseEntity.status(401).build();
		}
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String userId = userDetails.getMemberDTO().getUserId();

		LivePostDTO savedPost = livePostService.createPost(request, userId);
		return ResponseEntity.ok(savedPost);
	}

	@DeleteMapping("/board/live/posts/{livePostIdx}")
	public ResponseEntity<Void> deletePost(@PathVariable("livePostIdx") Long livePostIdx,
			Authentication authentication) {
		if (!isLogin(authentication)) {
			return ResponseEntity.status(401).build();
		}
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String userId = userDetails.getMemberDTO().getUserId();
		
		livePostService.deletePost(livePostIdx, userId);
		return ResponseEntity.noContent().build();
	}
}
