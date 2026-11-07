package com.example.board.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LivePostDTO {
	private Long livePostIdx;
	private Integer boardGroupIdx;
	private String userId;
	private String content;
	private LocalDateTime createdAt;
	private String writer;
}
