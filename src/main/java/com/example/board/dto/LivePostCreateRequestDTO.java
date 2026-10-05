package com.example.board.dto;

import lombok.Data;

@Data
public class LivePostCreateRequestDTO {
	private Integer boardGroupIdx;
	private String content;
}
