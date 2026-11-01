package com.example.board.event;


import com.example.board.dto.LivePostDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LivePostCreatedEvent {
	
	private final LivePostDTO post;
}
