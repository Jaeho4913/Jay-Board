package com.example.board.service;

import java.util.List;

import com.example.board.dto.ReplyDTO;
import com.example.board.dto.ReplyPageResponseDTO;

public interface ReplyService {

	List<ReplyDTO> findAllByBoardIdx(Long boardIdx);
	void save(ReplyDTO replyDTO);
	void delete(Long replyIdx, String loginUserId);
	void update(ReplyDTO replyDTO, String loginUserId);
	ReplyPageResponseDTO findRepliesPaging(ReplyDTO replyDTO);
}
