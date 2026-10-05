package com.example.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.board.dto.LivePostDTO;

@Mapper
public interface LivePostMapper {
	List<LivePostDTO> findPosts(@Param("boardGroupIdx") Integer boardGroupIdx,
			@Param("beforePostIdx") Long beforePostIdx, @Param("limit") int limit);

	int insert(LivePostDTO post);

	LivePostDTO findById(@Param("livePostIdx") Long livePostIdx);
}
