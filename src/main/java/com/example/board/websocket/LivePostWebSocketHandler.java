package com.example.board.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.board.dto.BoardGroupDTO;
import com.example.board.service.BoardGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class LivePostWebSocketHandler extends TextWebSocketHandler {
	
	private final Map<String, WebSocketSession> sessions= new ConcurrentHashMap<>();
	private final BoardGroupService boardGroupService;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		
		URI uri = session.getUri(); 
		if (uri == null) {
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		String boardGroupIdxText = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("boardGroupIdx");
		if (boardGroupIdxText == null || boardGroupIdxText.isBlank()) {
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		Integer boardGroupIdx;
		try {
			boardGroupIdx = Integer.valueOf(boardGroupIdxText);
		} catch (NumberFormatException e){
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		if (boardGroupIdx <= 0) {
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		
		BoardGroupDTO boardGroup = boardGroupService.getActiveBoardGroup(boardGroupIdx);
		if(boardGroup == null || !"LIVE".equals(boardGroup.getBoardType())) {
			session.close(CloseStatus.POLICY_VIOLATION);
			return;
		}
		session.getAttributes().put("boardGroupIdx", boardGroupIdx);
		
		WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 10000, 64*1024);
				
		sessions.put(session.getId(), safeSession);
		log.info("라이브 연결 성공: {},게시판: {}", session.getId(), boardGroupIdx);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session.getId());
		log.info("라이브 연결 종료: {}, 상태: {}", session.getId(), status);
	}
	
	public void broadcastToBoard(Integer boardGroupIdx, String message) {
		for (WebSocketSession session: sessions.values()) {
			if(!session.isOpen()) {
				continue;
			}
			Integer sessionBoardGroupIdx = (Integer)session.getAttributes().get("boardGroupIdx");
			
			if(!boardGroupIdx.equals(sessionBoardGroupIdx)) {
				continue;
			}
			try {
				session.sendMessage(new TextMessage(message));
			} catch(IOException | IllegalStateException e) {
				log.warn("라이브 메시지 전송 실패: {}", session.getId(), e);
			}
		}
	}
	
}
