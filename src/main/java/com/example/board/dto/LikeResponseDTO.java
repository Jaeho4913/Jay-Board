package com.example.board.dto;

import lombok.Data;

@Data
public class LikeResponseDTO {
	private String status;
	private boolean likeCheck;
	private int  likeCnt;
}
