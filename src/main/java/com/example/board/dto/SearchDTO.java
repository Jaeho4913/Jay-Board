package com.example.board.dto;

import lombok.Data;

@Data
public class SearchDTO {
	private Integer page;
	private Integer size;
	private String keyword;
	private String searchType;
	private String sortOrder;
	private String sortType;

	public SearchDTO() {
		this.page = 1;
		this.size = 10;
		this.keyword = "";
		this.searchType = "";
		this.sortOrder = "DESC";
		this.sortType = "latest";
	}
	public int getPage() {
		if (page == null) return 1;
		return page;
	}
	public int getSize() {
		if (size == null) return 10;
		return size;
	}
	public int getOffset() {
		return (getPage() - 1) * getSize();
	}
	public String  getSortOrder() {
		if (sortOrder == null || sortOrder.trim().isEmpty())
			return "DESC";
		return sortOrder.toUpperCase();
	}
	public String getSortType() {
		if(sortType == null || sortType.trim().isEmpty())
			return "latest";
		return sortType;
	}
}
