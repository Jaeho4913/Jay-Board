package com.example.board.dto;

import java.time.LocalDateTime;

public class BoardDTO {
    private Long idx;
    private String title;
    private String content;
    private String writer;
    private String user_id;
    private int viewCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String boardPw;
    private boolean isAuth;
    private boolean isGuest;

    // --- Getter/Setter ---

    public String getBoardPw() { return boardPw; }
    public void setBoardPw(String boardPw) { this.boardPw = boardPw; }

    public Long getIdx() { return idx; }
    public void setIdx(Long idx) { this.idx = idx; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }

    public int getViewCnt() { return viewCnt; }
    public void setViewCnt(int viewCnt) { this.viewCnt = viewCnt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean getIsAuth() { return isAuth; }
    public void setIsAuth(boolean isAuth) { this.isAuth = isAuth; }

    public boolean getIsGuest() { return isGuest; }
    public void setIsGuest(boolean isGuest) { this.isGuest = isGuest; }

    public String getUserId() {return user_id;}
    public void setUserId(String userId) {this.user_id= userId;}
}
	