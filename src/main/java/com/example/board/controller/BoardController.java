package com.example.board.controller; // 패키지명


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.example.board.dto.BoardDTO;
import com.example.board.dto.LikeResponseDTO;
import com.example.board.dto.LikeUserDTO;
import com.example.board.dto.SearchDTO;
import com.example.board.security.CustomUserDetails;
import com.example.board.service.BoardService;
import com.example.board.dto.MemberDTO;


@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;
    private boolean isLogin(Authentication authentication) {
    	return authentication != null
    		&& authentication.isAuthenticated()
    		&& authentication.getPrincipal() instanceof CustomUserDetails;
    }

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
    public ResponseEntity<BoardDTO> getBoardDetail(@RequestParam("idx") Long idx,
    																												Authentication authentication) {
        boardService.updateViewCnt(idx);
        BoardDTO board = boardService.findById(idx);

        if(board == null) {
        	return ResponseEntity.notFound().build();
        }
        int likeCnt = boardService.countLike(idx);
        board.setLikeCnt(likeCnt);

        if(isLogin(authentication)) {
        	String loginUserId = authentication.getName();
        	int exists = boardService.existsLike(idx, loginUserId);
        	board.setLikeCheck(exists > 0);

        	if(loginUserId.equals(board.getUserId())) {
        		board.setAuth(true);
        	} else {
        		board.setAuth(false);
        	}
        } else {
        	board.setLikeCheck(false);
        	board.setAuth(false);
        }
        return ResponseEntity.ok(board);
    }

    @ResponseBody
    @GetMapping("/api/boards")
    public ResponseEntity<Map<String, Object>> getBoardList(@ModelAttribute SearchDTO searchDTO,
    																													Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        result.put("boardData", boardService.findAll(searchDTO));

        if(isLogin(authentication)) {
        	CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        	MemberDTO member = userDetails.getMemberDTO();

        	result.put("isLogin", true);

        	String name = member.getUserName();

        	if(name == null || name.isBlank()) {
        		name = member.getUserId();
        	}
        	result.put("loginName", name);
        }else {
        	result.put("isLogin", false);
        }
    return ResponseEntity.ok(result);
}

@ResponseBody
@PostMapping("/board/like")
public ResponseEntity<LikeResponseDTO> btnLike(@RequestParam("idx") Long idx,
																												Authentication authentication) {
    LikeResponseDTO response = new LikeResponseDTO();

    if(!isLogin(authentication)) {
        response.setStatus("loginRequired");
        response.setLikeCheck(false);
        response.setLikeCnt(0);
        return ResponseEntity.ok(response);
    }
    String loginUserId = authentication.getName();
    LikeResponseDTO result = boardService.btnLike(idx, loginUserId);
    return ResponseEntity.ok(result);
}

@ResponseBody
@GetMapping("/board/likeUsers")
public List<LikeUserDTO> likeUsers(@RequestParam("idx") Long idx) {
	return boardService.findLikeUsers(idx);
}

@ResponseBody
@PostMapping("/board/save")
public ResponseEntity<String> save(BoardDTO boardDTO,
													  Authentication authentication) {

    if(!isLogin(authentication)) {
       return ResponseEntity.status(401).body("loginRequired");
    }
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    MemberDTO member = userDetails.getMemberDTO();
    boardDTO.setUserId(member.getUserId());
    boardDTO.setWriter(member.getUserName());

    boardService.save(boardDTO);
    return ResponseEntity.ok("success");
}
@ResponseBody
@PostMapping("/board/update")
public ResponseEntity<String> update(BoardDTO boardDTO,
														  Authentication authentication) {

	if(!isLogin(authentication)) {
		return ResponseEntity.ok("fail");
	}
	if(boardDTO.getIdx() == null) {
		return ResponseEntity.ok("fail");
	}
	BoardDTO originalBoard = boardService.findById(boardDTO.getIdx());

	if(originalBoard == null) {
		return ResponseEntity.ok("fail");
	}
	String loginUserId = authentication.getName();
	if(!loginUserId.equals(originalBoard.getUserId())) {
		return ResponseEntity.ok("fail");
	}
    boardService.update(boardDTO);
    return ResponseEntity.ok("success");
}
@ResponseBody
@GetMapping("/board/delete")
public ResponseEntity<String> delete(@RequestParam("idx") Long idx,
																							Authentication authentication) {

	if(!isLogin(authentication)) {
		return ResponseEntity.status(401).body("loginRequired");
	}

	BoardDTO originalBoard = boardService.findById(idx);

	if(originalBoard == null) {
		return ResponseEntity.ok("fail");
	}
	String loginUserId = authentication.getName();
	if(!loginUserId.equals(originalBoard.getUserId())) {
		return ResponseEntity.ok("fail");
	}
    boardService.delete(idx);
    return ResponseEntity.ok("success");
}

}

