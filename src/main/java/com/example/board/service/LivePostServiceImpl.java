package com.example.board.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.board.dto.BoardGroupDTO;
import com.example.board.dto.LivePostCreateRequestDTO;
import com.example.board.dto.LivePostDTO;
import com.example.board.dto.LivePostListResponseDTO;
import com.example.board.event.LivePostCreatedEvent;
import com.example.board.event.LivePostDeletedEvent;
import com.example.board.mapper.BoardGroupMapper;
import com.example.board.mapper.LivePostMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LivePostServiceImpl implements LivePostService {

	private final LivePostMapper livePostMapper;
	private final BoardGroupMapper boardGroupMapper;
	private final ApplicationEventPublisher eventPublisher;

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
		List<LivePostDTO> posts = livePostMapper.findPosts(boardGroupIdx, beforePostIdx, pageSize + 1);

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

	@Override
	@Transactional
	public LivePostDTO createPost(LivePostCreateRequestDTO request, String userId) {
		if (request == null) {
			throw new IllegalArgumentException("작성 요청이 없습니다.");
		}
		
		Integer boardGroupIdx = request.getBoardGroupIdx();
		
		if (boardGroupIdx == null || boardGroupIdx <= 0) {
			throw new IllegalArgumentException("올바른 게시판을 선택해주세요.");
		}
		
		String content = request.getContent();
		
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("내용을 입력해주세요.");
		}
		if (content.codePointCount(0, content.length()) > 500) {
			throw new IllegalArgumentException("내용은 500자 이내로 입력해주세요.");
		}
		
		BoardGroupDTO boardGroup = boardGroupMapper.findActiveBoardGroupByIdx(boardGroupIdx);
		
		if (boardGroup == null) {
			throw new IllegalArgumentException("존재하지 않거나 비활성화된 게시판입니다.");
		}
		if (!"LIVE".equals(boardGroup.getBoardType())) {
			throw new IllegalArgumentException("라이브형 게시판이 아닙니다.");
		}
		
		LivePostDTO post = new LivePostDTO();
		
		post.setBoardGroupIdx(boardGroupIdx);
		post.setUserId(userId);
		post.setContent(content);
		
		int insertedCount = livePostMapper.insert(post);
		
		if (insertedCount != 1) {
			throw new IllegalStateException("라이브 글 저장에 실패했습니다.");
		}
		
		LivePostDTO savedPost = livePostMapper.findById(post.getLivePostIdx());
		
		if (savedPost == null) {
			throw new IllegalStateException("저장된 라이브 글을 조회할 수 없습니다.");
		}
		eventPublisher.publishEvent(new LivePostCreatedEvent(savedPost));
		return savedPost;
	}

	@Override
	@Transactional
	public void deletePost(Long livePostIdx, String userId) {
		if (livePostIdx == null || livePostIdx <= 0) {
			throw new IllegalArgumentException("올바른 글 번호를 입력해주세요.");
		}
		if (userId == null || userId.isBlank()) {
			throw new IllegalArgumentException("사용자 정보가 없습니다.");
		}
		LivePostDTO post = livePostMapper.findById(livePostIdx);
		
		if (post == null) {
			throw new IllegalArgumentException("존재하지 않는 라이브 글입니다.");
		}
		if (!userId.equals(post.getUserId())) {
			throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
		}
		int deletedCount = livePostMapper.deleteByIdAndUserId(livePostIdx, userId);
		
		if(deletedCount != 1) {
			throw new IllegalStateException("라이브 글 삭제에 실패했습니다");
		}
		eventPublisher.publishEvent(
				new LivePostDeletedEvent(post.getBoardGroupIdx(), livePostIdx)
		);
	}
	

}
