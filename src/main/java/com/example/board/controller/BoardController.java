package com.example.board.controller; // 패키지명

import org.apache.jasper.tagplugins.jstl.core.If;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.InitBinder;
import jakarta.servlet.http.HttpSession;


import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.LikeUserDTO;
import com.example.board.dto.PageResponseDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.service.BoardService;
import com.example.board.dto.MemberDTO;
import com.example.board.validator.BoardValidator;
import com.example.board.mapper.BoardMapper;


@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String home() {
        return "board/home";
    }
    @GetMapping("/board/list")
    public String boardList() {
    	return "board/home";
    }
    @GetMapping("/write")
    public String writeForm(@ModelAttribute SearchDTO searchDTO, Model model) {
        model.addAttribute("searchDTO", searchDTO);
        return "board/write";
    }
    @GetMapping("/board/view")
    public String viewShell(@ModelAttribute SearchDTO searchDTO, Model model) {
        model.addAttribute("searchDTO", searchDTO);
        return "board/detail";
    }
    @GetMapping("/board/update")
    public String updateForm() {
        return "board/update";
    }
    @ResponseBody
    @GetMapping("/board/getDetail")
    public ResponseEntity<BoardDTO> getBoardDetail(@RequestParam("idx") Long idx, HttpSession session) {
        boardService.updateViewCnt(idx);
        BoardDTO board = boardService.findById(idx);
        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

        int likeCnt = boardService.countLike(idx);
        board.setLikeCnt(likeCnt);

        if(loginMember != null) {
        	int exists = boardService.existsLike(idx, loginMember.getUserId());
        	board.setLikeCheck(exists > 0);
        } else {
        	board.setLikeCheck(false);
        }

        if (loginMember != null) {
        	int exists = boardService.existsLike(idx, loginMember.getUserId());
        	board.setLikeCheck(exists > 0);
        } else {
        	board.setLikeCheck(false);
        }
         if(loginMember != null && loginMember.getUserId().equals(board.getUserId())) {
        	 board.setAuth(true);
         }

        return ResponseEntity.ok(board);
    }

    @ResponseBody
    @GetMapping("/api/boards")
    public ResponseEntity<Map<String, Object>> getBoardList(@ModelAttribute SearchDTO searchDTO, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        result.put("boardData", boardService.findAll(searchDTO));

        MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
        if (loginMember != null) {
            result.put("isLogin", true);
            String name = loginMember.getUserName();
            if (name == null || name.isBlank()) {
                name = loginMember.getUserId();
                }
                result.put("loginName", name);
            } else {
        result.put("isLogin", false);
    }
    return ResponseEntity.ok(result);
}

@ResponseBody
@PostMapping("/board/like")
public ResponseEntity<LikeResponseDTO> btnLike(@RequestParam("idx") Long idx, HttpSession session) {
    MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
    LikeResponseDTO response = new LikeResponseDTO();

    if(loginMember == null) {
        response.setStatus("loginRequired");
        response.setLikeCheck(false);
        response.setLikeCnt(0);
        return ResponseEntity.ok(response);
    }
    LikeResponseDTO result = boardService.btnLike(idx, loginMember.getUserId());
    return ResponseEntity.ok(result);
}

@ResponseBody
@GetMapping("board/likeUsers")
public List<LikeUserDTO> likeUsers(@RequestParam("idx") Long idx) {
	return boardService.findLikeUsers(idx);
}

@ResponseBody
@PostMapping("/board/save")
public ResponseEntity<String> save(BoardDTO boardDTO, HttpSession session) {
    MemberDTO loginMemberDTO = (MemberDTO) session.getAttribute("loginMember");

    if(loginMemberDTO == null) {
       return ResponseEntity.status(401).body("loginRequired");
    }
    boardDTO.setUserId(loginMemberDTO.getUserId());
    boardDTO.setWriter(loginMemberDTO.getUserName());

    boardService.save(boardDTO);
    return ResponseEntity.ok("success");
}
@ResponseBody
@PostMapping("/board/update")
public ResponseEntity<String> update(BoardDTO boardDTO, HttpSession session) {

	MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

	if(loginMember == null) {
		return ResponseEntity.ok("fail");
	}
	BoardDTO originalBoard = boardService.findById(boardDTO.getIdx());

	if(originalBoard == null) {
		return ResponseEntity.ok("fail");
	}

	if(!loginMember.getUserId().equals(originalBoard.getUserId())) {
		return ResponseEntity.ok("fail");
	}
    boardService.update(boardDTO);
    return ResponseEntity.ok("success");
}
@ResponseBody
@GetMapping("/board/delete")
public ResponseEntity<String> delete(@RequestParam("idx") Long idx, HttpSession session) {
	MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

	if(loginMember == null) {
		return ResponseEntity.status(401).body("loginRequired");
	}

	BoardDTO originalBoard = boardService.findById(idx);

	if(originalBoard == null) {
		return ResponseEntity.ok("fail");
	}
	if(!loginMember.getUserId().equals(originalBoard.getUserId())) {
		return ResponseEntity.ok("fail");
	}
    boardService.delete(idx);
    return ResponseEntity.ok("success");
}

}

