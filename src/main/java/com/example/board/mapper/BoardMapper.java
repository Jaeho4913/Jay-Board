package com.example.board.mapper; // (패키지명은 작성자님꺼 그대로 두세요)

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeUserDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.dto.MemberDTO;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 1. 목록 보기 (파라미터 2개 추가된 버전으로 교체!)
    List<BoardDTO> findAll(SearchDTO searchDTO);
    int count(SearchDTO searchDTO);
    int countLike(@Param("idx") Long idx);
    int existsLike(@Param("idx") Long idx, @Param("userId") String userId);
    int insertLike(@Param("idx") Long idx, @Param("userId") String userId);
    int deleteLike(@Param("idx") Long idx, @Param("userId") String userId);
    int countLikeUsers(@Param("idx") Long idx);

    List<LikeUserDTO> findLikeUsers(Long idx);
    List<MemberDTO> findLikeUsersPaging(
    		@Param("idx") Long idx,
    		@Param("size") int size,
    		@Param("offset") int offset
    );

    // 2. 글 저장
    void save(BoardDTO boardDTO);

    // 3. 상세 보기
    BoardDTO findById(Long idx);

    // 4. 수정
    void update(BoardDTO boardDTO);

    // 5. 삭제
    void delete(Long idx);

    void updateViewCnt(Long idx);
}