package com.example.board.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.dto.*;
import com.example.board.mapper.BoardGroupMapper;
import com.example.board.mapper.BoardImageMapper;
import com.example.board.mapper.BoardMapper;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper boardMapper;

	@Autowired
	private BoardGroupMapper boardGroupMapper;

	@Autowired
	private BoardImageStorageService boardImageStorageService;

	@Autowired
	private BoardImageMapper boardImageMapper;

	@Override
	public PageResponseDTO findAll(SearchDTO searchDTO) {
		validateSortType(searchDTO);

		Integer boardGroupIdx = searchDTO.getBoardGroupIdx();

		if (boardGroupIdx != null) {
			int result = boardGroupMapper.countActiveBoardGroup(boardGroupIdx);

			if (result == 0) {
				throw new IllegalArgumentException("존재하지 않거나 비활성화 된 게시판입니다.");
			}
		}

		List<BoardDTO> list = boardMapper.findAll(searchDTO);
		int totalCount = boardMapper.count(searchDTO);

		return new PageResponseDTO(searchDTO, totalCount, list);
	}

	private void validateSortType(SearchDTO searchDTO) {
		String sortType = searchDTO.getSortType();

		if (sortType == null || sortType.trim().isEmpty()) {
			searchDTO.setSortType("latest");
			return;
		}
		switch (sortType) {
		case "latest":
		case "oldest":
		case "viewDesc":
		case "viewAsc":
		case "likeDesc":
		case "likeAsc":
		case "replyDesc":
		case "replyAsc":
			break;
		default:
			searchDTO.setSortType("latest");
		}
	}

	@Override
	public LikeResponseDTO btnLike(Long idx, String userId) {
		LikeResponseDTO response = new LikeResponseDTO();

		BoardDTO board = findById(idx);

		if (board == null) {
			throw new IllegalArgumentException("좋아요를 변경할 수 없는 게시글입니다.");
		}

		int exists = boardMapper.existsLike(idx, userId);

		if (exists > 0) {
			boardMapper.deleteLike(idx, userId);
			response.setLikeCheck(false);
		} else {
			boardMapper.insertLike(idx, userId);
			response.setLikeCheck(true);
		}
		int likeCnt = boardMapper.countLike(idx);

		response.setStatus("success");
		response.setLikeCnt(likeCnt);

		return response;
	}

	@Override
	public int countLike(Long idx) {
		return boardMapper.countLike(idx);
	}

	@Override
	public int existsLike(Long idx, String userId) {
		return boardMapper.existsLike(idx, userId);
	}

	@Override
	public List<LikeUserDTO> findLikeUsers(Long idx) {
		return boardMapper.findLikeUsers(idx);
	}

	@Override
	public int countLikeUsers(Long idx) {
		return boardMapper.countLikeUsers(idx);
	}

	@Override
	public List<MemberDTO> findLikeUsersPaging(Long idx, int size, int offset) {
		return boardMapper.findLikeUsersPaging(idx, size, offset);
	}

	@Override
	@Transactional
	public void save(BoardDTO boardDTO, MultipartFile imageFile) {
		Integer boardGroupIdx = boardDTO.getBoardGroupIdx();
		if (boardGroupIdx == null) {
			throw new IllegalArgumentException("게시판을 선택해주세요");
		}
		int countActiveGroup = boardGroupMapper.countActiveBoardGroup(boardGroupIdx);
		if (countActiveGroup == 0) {
			throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
		}
		BoardImageDTO boardImage = boardImageStorageService.store(imageFile);

		if (boardImage != null) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCompletion(int status) {
					if (status == STATUS_ROLLED_BACK) {
						try {
							boardImageStorageService.delete(boardImage.getStoredName());
						} catch (RuntimeException e) {
							log.error("롤빅 후 이미지 파일 정리 실패: {}", boardImage.getStoredName(), e);
						}
					}
				}
			});
		}

		boardMapper.save(boardDTO);

		if (boardImage != null) {
			boardImage.setBoardIdx(boardDTO.getIdx());
			boardImageMapper.insert(boardImage);
		}
	}

	@Override
	public BoardDTO findById(Long idx) {
		BoardDTO board = boardMapper.findById(idx);

		if (board == null) {
			return null;
		}
		int boardGroupIdx = board.getBoardGroupIdx();

		int countActiveGroup = boardGroupMapper.countActiveBoardGroup(boardGroupIdx);

		if (countActiveGroup == 0) {
			return null;
		}
		return board;
	}

	@Override
	@Transactional
	public void update(BoardDTO boardDTO, MultipartFile imageFile, boolean deleteImage) {

		BoardImageDTO oldImage = boardImageMapper.findByBoardIdx(boardDTO.getIdx());
		boolean hasNewImage = imageFile != null && !imageFile.isEmpty();

		if (deleteImage && hasNewImage) {
			throw new IllegalArgumentException("이미지 교체와 삭제를 동시에 선택할 수 없습니다.");
		}
		
		if (deleteImage && oldImage == null) {
			throw new IllegalArgumentException("삭제할 이미지가 없습니다.");
		}

		BoardImageDTO newImage = null;

		if (hasNewImage) {
			newImage = boardImageStorageService.store(imageFile);
			String newStoredName = newImage.getStoredName();

			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCompletion(int status) {
					if (status == STATUS_ROLLED_BACK) {
						try {
							boardImageStorageService.delete(newStoredName);
						} catch (RuntimeException e) {
							log.error("롤백 후 이미지 파일 정리 실패: {}", newStoredName, e);
						}
					}
				}
			});
		}

		if (hasNewImage || deleteImage) {
			boardImageMapper.deleteByBoardIdx(boardDTO.getIdx());
		}

		if (newImage != null) {
			newImage.setBoardIdx(boardDTO.getIdx());
			boardImageMapper.insert(newImage);
		}

		if (oldImage != null && (hasNewImage || deleteImage)) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try {
						boardImageStorageService.delete(oldImage.getStoredName());
					} catch (RuntimeException e) {
						log.error("커밋 후 이미지 파일 정리 실패: {}", oldImage.getStoredName(), e);
					}
				}
			});
		}
		boardMapper.update(boardDTO);
	}

	@Override
	@Transactional
	public void delete(Long idx) {
		BoardImageDTO boardImage = boardImageMapper.findByBoardIdx(idx);
		boardImageMapper.deleteByBoardIdx(idx);
		boardMapper.delete(idx);

		if (boardImage != null) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					try {
						boardImageStorageService.delete(boardImage.getStoredName());
					} catch (RuntimeException e) {
						log.error("커밋 후 이미지 파일 정리 실패: {}", boardImage.getStoredName(), e);
					}
				}
			});
		}
	}

	@Override
	public void updateViewCnt(Long idx) {
		boardMapper.updateViewCnt(idx);
	}

	@Override
	public BoardImageDTO findImageByBoardIdx(Long boardIdx) {
		BoardDTO board = findById(boardIdx);

		if (board == null) {
			return null;
		}
		return boardImageMapper.findByBoardIdx(boardIdx);
	}
}
