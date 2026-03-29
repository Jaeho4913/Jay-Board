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

import com.example.board.dto.BoardDTO;
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
	public String updateForm(@RequestParam("idx") Long idx,
										   @RequestParam(value = "boardPw", required = false) String boardPw,
										   Model model, HttpSession session) {
		BoardDTO board = boardService.findById(idx);
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");
		boolean isAuth = false;

		if (loginMember != null && loginMember.getUserName().equals(board.getWriter())) {
			isAuth = true;
		}
		else if (boardPw != null && boardPw.equals(board.getBoardPw())) {
			isAuth = true;
		}

		if (!isAuth) {
			return "redirect:/board/view?idx=" + idx;
		}

		model.addAttribute("board", board);
		return "board/update";
	}
	@PostMapping("/board/update")
		public String update(BoardDTO boardDTO, HttpSession session) {
			BoardDTO originalBoard = boardService.findById(boardDTO.getIdx());
			MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

			boolean isAuth = false;
			if (loginMember != null && loginMember.getUserName().equals(originalBoard.getWriter())) {
				isAuth = true;
			} else if (boardDTO.getBoardPw() != null && boardDTO.getBoardPw().equals(originalBoard.getBoardPw())) {
				isAuth = true;
			}

			if (!isAuth ) {
				return "redirect:/board/view?idx=" + boardDTO.getIdx();
			}
			boardService.update(boardDTO);
			return "redirect:/board/view?idx=" + boardDTO.getIdx();
	}
	@GetMapping("/board/delete")
	public String delete(@RequestParam("idx") Long idx,
								 @RequestParam(value = "boardPw", required = false) String boardPw,
								 HttpSession session) {
		BoardDTO board = boardService.findById(idx);
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		boolean isAuth = false;
		if( loginMember != null && loginMember.getUserName().equals(board.getWriter())) {
			isAuth = true;
		}else if ( boardPw != null && boardPw.equals(board.getBoardPw())) {
			isAuth = true;
		}

		if (!isAuth) {
			return "redirect:/board/view?idx=" + idx;
		}
		boardService.delete(idx);
		return "redirect:/";
	}
	@ResponseBody
	@GetMapping("/board/getDetail")
	public ResponseEntity<BoardDTO> getBoardDetail(@RequestParam("idx") Long idx, HttpSession session) {
		BoardDTO board = boardService.findById(idx);
		MemberDTO loginMember = (MemberDTO) session.getAttribute("loginMember");

		if (board.getBoardPw() != null && !board.getBoardPw().isEmpty()) {
			board.setIsGuest(true);
		}
		else {
			if(loginMember != null && loginMember.getUserId().equals(board.getUserId())) {
				board.setIsAuth(true);
			}
		}
		return ResponseEntity.ok(board);
	}
	@ResponseBody
	@PostMapping("/board/checkGuestPw")
	public ResponseEntity<String> checkGuestPw(@RequestParam("idx") Long idx, @RequestParam("boardPw") String boardPw) {
		BoardDTO board = boardService.findById(idx);
		if (board != null && boardPw.equals(board.getBoardPw())) {
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.ok("fail");
	}
	@ResponseBody
	@PostMapping("/board/deleteGuestPost")
	public ResponseEntity<String> deleteGuestPost(@RequestParam("idx") Long idx, @RequestParam("boardPw") String boardPw) {
		BoardDTO board = boardService.findById(idx);
		if (board != null && boardPw.equals(board.getBoardPw())) {
			boardService.delete(idx);
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.ok("fail");
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
			if (name == null || name.trim().isEmpty()) {
				name = loginMember.getUserId();
				}
				result.put("loginName", name);
			} else {
		result.put("isLogin", false);
	}
	return ResponseEntity.ok(result);
}

@ResponseBody
@PostMapping("/board/save")
public ResponseEntity<String> save(BoardDTO boardDTO, HttpSession session) {
	MemberDTO loginMemberDTO = (MemberDTO) session.getAttribute("loginMember");

	if(loginMemberDTO != null) {
		boardDTO.setUserId(loginMemberDTO.getUserId());
	}
	boardService.save(boardDTO);
	return ResponseEntity.ok("success");
}
}

