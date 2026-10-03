package com.example.board.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.dto.BoardImageDTO;

public interface BoardImageStorageService {
	
	BoardImageDTO store(MultipartFile file);

	void delete(String storedName);
	
	Resource load(String storedName);
	
	Resource loadThumbnail(String storedName);
}
