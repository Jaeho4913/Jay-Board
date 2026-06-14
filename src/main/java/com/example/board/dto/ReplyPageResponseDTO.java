package com.example.board.dto;

import java.util.List;
import lombok.Data;

@Data
public class ReplyPageResponseDTO {
	private List<ReplyDTO> replyList;

	private int page;
	private int size;
	private int totalCount;
	private int totalPage;
	private int startPage;
	private int endPage;
	private int pageCount = 10;

	public ReplyPageResponseDTO(int page, int size, int totalCount, List<ReplyDTO> replyList) {
		this.page = page;
		this.size = size;
		this.totalCount = totalCount;
		this.replyList = replyList;

		this.totalPage = (int)Math.ceil((double)totalCount / size);

		if(this.page > totalPage && totalPage > 0) {
			this.page = totalPage;
		}

		this.startPage = ((this.page - 1) / pageCount) * pageCount + 1;
		this.endPage = startPage + pageCount - 1;

		if(this.endPage > totalPage) {
			this.endPage = totalPage;
		}
	}
}
