package com.example.board.service;

import java.util.List;

import com.example.board.dto.ReplyDTO;

public interface ReplyService {

	List<ReplyDTO> findAllByBoardIdx(Long boardIdx);
	void save(ReplyDTO replyDTO);
	void delete(Long replyIdx, String loginUserId);
}
