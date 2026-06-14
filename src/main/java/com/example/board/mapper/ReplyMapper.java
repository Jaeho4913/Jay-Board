package com.example.board.mapper;

import java.util.List;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.board.dto.ReplyDTO;

@Mapper
public interface ReplyMapper {

	List<ReplyDTO> findAllByBoardIdx(@Param("boardIdx") Long boardIdx);

    void save(ReplyDTO replyDTO);

    ReplyDTO findByReplyIdx(@Param("replyIdx") Long replyIdx);

    void  delete(@Param("replyIdx") Long replyIdx);

    void update(ReplyDTO replyDTO);

    List<ReplyDTO> findRepliesPaging(ReplyDTO replyDTO);

    int countByBoardIdx(Long boardIdx);
}
