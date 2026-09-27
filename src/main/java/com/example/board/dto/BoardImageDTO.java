package com.example.board.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardImageDTO {
	private Long imageIdx;
	private Long boardIdx;
	private String originalName;
	private String storedName;
	private Long fileSize;
	private LocalDateTime createdAt;
}
