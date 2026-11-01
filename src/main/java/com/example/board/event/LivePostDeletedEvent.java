package com.example.board.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LivePostDeletedEvent {
	private final Integer boardGroupIdx;
	private final Long livePostIdx;
}