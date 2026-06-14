package com.example.board.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.board.dto.ReplyDTO;
import com.example.board.dto.ReplyPageResponseDTO;
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

	@Override
	public void delete(Long replyIdx, String loginUserId) {

		if (replyIdx == null) {
			throw new IllegalArgumentException("삭제할 댓글 번호가 없습니다.");
		}

		if(loginUserId == null || loginUserId.trim().isEmpty()) {
			throw new IllegalArgumentException("로그인 정보가 없습니다.");
		}

		ReplyDTO replyDTO = replyMapper.findByReplyIdx(replyIdx);

		if(replyDTO == null) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}

		if(!replyDTO.getUserId().equals(loginUserId)) {
			throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
		}
		replyMapper.delete(replyIdx);
	}
	@Override
	public void update(ReplyDTO replyDTO, String loginUserId) {
		if (replyDTO.getReplyIdx() == null) {
			throw new IllegalArgumentException("수정할 댓글 번호가 없습니다.");
		}

		if(loginUserId == null || loginUserId.trim().isEmpty()) {
			throw new IllegalArgumentException("로그인 정보가 없습니다.");
		}

		String content = replyDTO.getContent();

		if(content == null || content.trim().isEmpty()) {
			throw new IllegalArgumentException("수정할 내용이 없습니다.");
		}

		content = content.trim();

		if(content.length() > 1000) {
			throw new IllegalArgumentException("댓글은 1,000자를 초과할 수 없습니다.");
		}

		ReplyDTO savedReply = replyMapper.findByReplyIdx(replyDTO.getReplyIdx());

		if(savedReply == null) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}

		if(!savedReply.getUserId().equals(loginUserId)) {
			throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
		}
		replyDTO.setContent(content);

		replyMapper.update(replyDTO);
	}
	@Override
	public ReplyPageResponseDTO findRepliesPaging(ReplyDTO replyDTO) {

		Long boardIdx = replyDTO.getBoardIdx();

		if (boardIdx == null) {
			throw new IllegalArgumentException("게시글 번호가 없습니다.");
		}

		Integer requestPage = replyDTO.getPage();
		Integer requestSize = replyDTO.getSize();

		int page;
		if(requestPage == null) {
			page = 1;
		} else {
			page = requestPage;
		}

		int size;
		if(requestSize == null) {
			size = 10;
		} else {
			size = requestSize;
		}

		if(page < 1) {
			page = 1;
		}

		if(size < 1) {
			size = 10;
		}

		int offset = (page - 1) * size;

		replyDTO.setPage(page);
		replyDTO.setSize(size);
		replyDTO.setOffset(offset);

		int totalCount = replyMapper.countByBoardIdx(boardIdx);

		List<ReplyDTO> replyList = replyMapper.findRepliesPaging(replyDTO);

		return new ReplyPageResponseDTO(page, size, totalCount, replyList);
	}
}
