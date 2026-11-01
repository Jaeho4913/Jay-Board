package com.example.board.event;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.board.dto.LivePostDTO;
import com.example.board.websocket.LivePostWebSocketHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LivePostEventListener {
	private final LivePostWebSocketHandler livePostWebSocketHandler;
	private final ObjectMapper objectMapper;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePostCreated (LivePostCreatedEvent event) {
		LivePostDTO post = event.getPost();
		
		Map<String, Object> payload = Map.of(
				"type", "POST_CREATED",
				"post", post
				);
		try {
			String message = objectMapper.writeValueAsString(payload);
			livePostWebSocketHandler.broadcastToBoard(post.getBoardGroupIdx(), message);
		} catch (JsonProcessingException e) {
			log.error("라이브 생성 알림 변환 실패: {}", post.getLivePostIdx(), e);
		}
		
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handlePostDeleted(LivePostDeletedEvent event) {
		
		Map<String, Object> payload =  Map.of(
				"type", "POST_DELETED",
				"boardGroupIdx", event.getBoardGroupIdx(),
				"livePostIdx", event.getLivePostIdx()
				);
		try {
			String message = objectMapper.writeValueAsString(payload);
			livePostWebSocketHandler.broadcastToBoard(event.getBoardGroupIdx(), message);
		} catch (JsonProcessingException e) {
			log.error("라이브 삭제 알림 변환 실패: {}", event.getLivePostIdx(), e);
		}
	}
}
