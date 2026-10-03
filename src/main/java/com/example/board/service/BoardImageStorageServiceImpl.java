package com.example.board.service;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.board.dto.BoardImageDTO;

@Service
public class BoardImageStorageServiceImpl implements BoardImageStorageService {

	@Value("${app.upload.board-image-dir}")
	private String uploadDir;

	private BufferedImage createThumbnail(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		if (width <= 400 && height <= 400) {
			return image;
		}

		double scale = Math.min(400.0 / width, 400.0 / height);

		int thumbnailWidth = Math.max(1, (int) Math.round(scale * width));
		int thumbnailHeight = Math.max(1, (int) Math.round(scale * height));

		int imageType;

		if (image.getColorModel().hasAlpha()) {
			imageType = BufferedImage.TYPE_INT_ARGB;
		} else {
			imageType = BufferedImage.TYPE_INT_RGB;
		}
		BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, imageType);

		Graphics2D graphics = thumbnail.createGraphics();

		try {
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			graphics.drawImage(image, 0, 0, thumbnailWidth, thumbnailHeight, null);
		} finally {
			graphics.dispose();
		}
		return thumbnail;
	}

	@Override
	public BoardImageDTO store(MultipartFile file) {

		if (file == null || file.isEmpty()) {
			return null;
		}

		if (file.getSize() > 5L * 1024 * 1024) {
			throw new IllegalArgumentException("이미지는 5MB 이하만 업로드할 수 있습니다.");
		}

		String originalName = file.getOriginalFilename();

		if (originalName == null || originalName.isBlank()) {
			throw new IllegalArgumentException("파일명을 확인할 수 없습니다.");
		}

		int dotIndex = originalName.lastIndexOf('.');

		if (dotIndex == -1 || dotIndex == originalName.length() - 1) {
			throw new IllegalArgumentException("파일 확장자를 확인해주세요.");
		}

		String extension = originalName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);

		if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png")) {
			throw new IllegalArgumentException("JPG, JPEG, PNG 이미지만 업로드할 수 있습니다.");
		}

		try (InputStream input = file.getInputStream();
				ImageInputStream imageInput = ImageIO.createImageInputStream(input)) {

			if (imageInput == null) {
				throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다.");
			}

			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInput);

			if (!readers.hasNext()) {
				throw new IllegalArgumentException("정상적인 이미지 파일이 아닙니다.");
			}

			ImageReader reader = readers.next();

			try {
				String format = reader.getFormatName().toLowerCase(Locale.ROOT);

				if (!format.equals("jpeg") && !format.equals("png")) {
					throw new IllegalArgumentException("실제 JPEG 또는 PNG 이미지 파일만 업로드할 수 있습니다.");
				}
				reader.setInput(imageInput);
				int width = reader.getWidth(0);
				int height = reader.getHeight(0);

				if (width <= 0 || height <= 0 || (long) width * height > 10_000_000L) {
					throw new IllegalArgumentException("이미지 크기가 허용 범위를 초과했습니다.");
				}

				BufferedImage image = reader.read(0);

				BufferedImage thumbnail = createThumbnail(image);

				String storedName = UUID.randomUUID().toString() + "." + format;
				String thumbnailName = "thumb_" + storedName;

				Path directory = Path.of(uploadDir).toAbsolutePath().normalize();
				Files.createDirectories(directory);

				Path target = directory.resolve(storedName).normalize();
				Path thumbnailTarget = directory.resolve(thumbnailName).normalize();
				try {
					boolean written = ImageIO.write(image, format, target.toFile());

					if (!written) {
						throw new IllegalStateException("이미지를 저장할 수 없습니다.");
					}

					boolean thumbnailWritten = ImageIO.write(thumbnail, format, thumbnailTarget.toFile());

					if (!thumbnailWritten) {
						throw new IllegalStateException("썸네일을 저장할 수 없습니다.");
					}

					BoardImageDTO boardImage = new BoardImageDTO();

					boardImage.setOriginalName(originalName);
					boardImage.setStoredName(storedName);
					boardImage.setFileSize(Files.size(target));

					return boardImage;
				} catch (IOException | RuntimeException e) {
					try {
						Files.deleteIfExists(target);
					} catch (IOException cleanUpException) {
						e.addSuppressed(cleanUpException);
					}
					try {
						Files.deleteIfExists(thumbnailTarget);
					} catch (IOException cleanUpException) {
						e.addSuppressed(cleanUpException);
					}
					throw e;
				}
			} finally {
				reader.dispose();
			}
		} catch (IOException e) {
			throw new IllegalStateException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
		}
	}

	@Override
	public void delete(String storedName) {
		if (storedName == null || storedName.isBlank()) {
			throw new IllegalArgumentException("삭제할 파일명이 없습니다.");
		}

		Path directory = Path.of(uploadDir).toAbsolutePath().normalize();
		Path target = directory.resolve(storedName).normalize();
		Path thumbnailTarget = directory.resolve("thumb_" + storedName).normalize();

		if (!directory.equals(target.getParent()) || !directory.equals(thumbnailTarget.getParent())) {
			throw new IllegalArgumentException("허용되지 않은 파일 경로입니다.");
		}

		IOException deleteException = null;

		try {
			Files.deleteIfExists(target);
		} catch (IOException e) {
			deleteException = e;
		}
		try {
			Files.deleteIfExists(thumbnailTarget);
		} catch (IOException e) {
			if (deleteException == null) {
				deleteException = e;
			} else {
				deleteException.addSuppressed(e);
			}
		}
		if (deleteException != null) {
			throw new IllegalStateException("이미지 파일 삭제 중 오류가 발생했습니다.", deleteException);
		}
	}

	@Override
	public Resource load(String storedName) {
		if (storedName == null || storedName.isBlank()) {
			throw new IllegalArgumentException("조회할 파일명이 없습니다.");
		}

		Path directory = Path.of(uploadDir).toAbsolutePath().normalize();
		Path target = directory.resolve(storedName).normalize();

		if (!directory.equals(target.getParent())) {
			throw new IllegalArgumentException("허용되지 않은 파일 경로명입니다.");
		}

		if (!Files.isRegularFile(target) || !Files.isReadable(target)) {
			return null;
		}

		return new FileSystemResource(target);
	}
	
	@Override
	public Resource loadThumbnail(String storedName) {
		if (storedName == null || storedName.isBlank()) {
			throw new IllegalArgumentException("조회할 파일명이 없습니다.");
		}
		Resource thumbnail = load("thumb_" + storedName);
		
		if (thumbnail != null) {
			return thumbnail;
		} else {
			return load(storedName);
		}
	}

}
