package com.example.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.board.dto.ReplyDTO;
import com.example.board.mapper.ReplyMapper;

@Service
public class ReplyServiceImpl implements ReplyService{

	@Autowired
	private ReplyMapper replyMapper;

	@Override
	public List<ReplyDTO> findAllByBoardIdx(Long boardIdx){
		return replyMapper.findAllByBoardIdx(boardIdx);
	}
	@Override
	public void save(ReplyDTO replyDTO) {

		if (replyDTO.getBoardIdx() == null) {
			throw new IllegalArgumentException("해당 게시글이 없습니다.");
		}

		String content = replyDTO.getContent()	;

		if(content == null || content.trim().isEmpty()	) {
			throw new IllegalArgumentException("댓글 내용 입력 부탁드립니다.");
		}

		content = content.trim();

		if(content.length() > 1000) {
			throw new IllegalArgumentException("1,000자 이하로 입력 부탁드립니다.");
		}
		replyDTO.setContent(content.trim());

		replyMapper.save(replyDTO);
	}
}
