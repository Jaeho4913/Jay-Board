package com.example.board.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReplyDTO {
	    private Long replyIdx;
	    private Long boardIdx;
	    private Long parentReplyIdx;
	    private String userId;
	    private String userName;
	    private String content;
	    private LocalDateTime createdAt;
	    private LocalDateTime updatedAt;
	    private boolean myReply;
}
