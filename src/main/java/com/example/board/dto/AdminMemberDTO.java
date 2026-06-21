package com.example.board.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminMemberDTO {
		private Long adminIdx;
		private String adminId;
		private String password;
		private String adminName;
		private String email;
		private String adminRole;
		private String adminStatus;
		private LocalDateTime lastLoginAt;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
}
