package com.example.board.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageResponseDTO {
	private List<BoardDTO> boardList;
	private SearchDTO searchDTO;

	private int totalCount;
	private int totalPage;
	private int startPage;
	private int endPage;
	private int pageCount = 10;

	public PageResponseDTO(SearchDTO searchDTO, int totalCount, List<BoardDTO> boardList) {
		this.searchDTO = searchDTO;
		this.totalCount = totalCount;
		this.boardList = boardList;

		this.totalPage = (int)Math.ceil((double) totalCount / searchDTO.getSize());

		if (searchDTO.getPage() > totalPage && totalPage > 0) {
			searchDTO.setPage(totalPage);
		}

		this.startPage = ((searchDTO.getPage() - 1) / pageCount) * pageCount + 1;

		this.endPage = startPage + pageCount - 1;

		if (this.endPage > totalPage) {
			this.endPage = totalPage;
		}
	}
}
