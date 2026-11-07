package com.example.board.dto;

import java.util.List;

import lombok.Data;

@Data
public class LivePostListResponseDTO {
	private List<LivePostDTO> items;
	private boolean hasNext;
	private Long nextCursor;
}
