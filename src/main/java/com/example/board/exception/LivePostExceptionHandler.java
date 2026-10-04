package com.example.board.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.board.controller.LivePostController;

@RestControllerAdvice(assignableTypes = LivePostController.class)
public class LivePostExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}
