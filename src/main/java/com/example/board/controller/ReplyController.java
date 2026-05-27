package com.example.board.controller;

import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.board.dto.MemberDTO;
import com.example.board.dto.ReplyDTO;
import com.example.board.service.ReplyService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ReplyController {
	
	@Autowired
	private ReplyService replyService;
	
	@ResponseBody
	@GetMapping("/board/replies")
	public ResponseEntity<Map<String, Object>>getReplyList(@ModelAttribute ReplyDTO replyDTO) {
		
		Map<String, Object> resultMap = new HashMap<>();
		
		Long boardIdx = replyDTO.getBoardIdx();
		
		if(boardIdx == null) {
			resultMap.put("status", "fail");
			resultMap.put("message", "해당 게시글이 없습니다.");
			return ResponseEntity.ok(resultMap);
		}
		resultMap.put("status", "success");
		resultMap.put("replyData", replyService.findAllByBoardIdx(boardIdx));
		return ResponseEntity.ok(resultMap);
	}
	@ResponseBody
	@PostMapping("/board/reply/save")
	public ResponseEntity<Map<String, Object>> save(@ModelAttribute ReplyDTO replyDTO, HttpSession session) {
		
		Map<String, Object> resultMap = new HashMap<>()	;
		MemberDTO loginMemberDTO = (MemberDTO)session.getAttribute("loginMember");
		
		if(loginMemberDTO == null) {
			resultMap.put("status", "loginRequired");
			return ResponseEntity.ok(resultMap);
		}
		replyDTO.setUserId(loginMemberDTO.getUserId());
		
		try {
			replyService.save(replyDTO);
			resultMap.put("status", "success");
		} catch (IllegalArgumentException e) {
			resultMap.put("status", "fail");
			resultMap.put("message", e.getMessage());
		}
		return ResponseEntity.ok(resultMap);
	}
}

