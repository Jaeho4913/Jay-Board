package com.example.board.validator;

import org.springframework.validation.Errors;

import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.board.dto.BoardDTO;

public class BoardValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return BoardDTO.class.isAssignableFrom(clazz);
	}
	@Override
	public void validate(Object target, Errors errors) {
		BoardDTO boardDTO = (BoardDTO) target;
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "writer", "required", "작성자는 필수입니다.");
		
		if (boardDTO.getContent() == null || boardDTO.getContent().trim().isEmpty()) {
			errors.rejectValue("content", "required", "내용을 입력해주세요.");
		}
		if (boardDTO.getTitle() != null && boardDTO.getTitle().length() < 1) {
			errors.rejectValue("title", "length", "제목은 1글자 이상이어야 합니다.");
		}
	}
	
}
