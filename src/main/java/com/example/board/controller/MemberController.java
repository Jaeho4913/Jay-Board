package com.example.board.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.board.SecurityConfig;
import com.example.board.dto.LoginDTO;
import com.example.board.dto.MemberDTO;
import com.example.board.service.MemberService;
import com.example.board.service.MemberServiceImpl;


@Controller
@RequestMapping("/member")
public class MemberController {

    private final SecurityConfig securityConfig;
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService memberService;

    MemberController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

	@GetMapping("/save")
	public String saveForm() {
		logger.info("회원가입 페이지 진입");
		return "member/save";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute MemberDTO memberDTO, HttpServletRequest request, Model model) {
		logger.info("회원가입 요청 : " + memberDTO.getUserId());
		if (memberDTO.getUserId() == null || memberDTO.getUserId().trim().isEmpty()) {
			return "member/save";
		}
		try {
		memberService.save(memberDTO);
		HttpSession session = request.getSession();
		session.setAttribute("loginMember", memberDTO);
		return "redirect:/";
	} catch (DuplicateKeyException e) {
		logger.error("중복 가입 시도 : " + e.getMessage());
		model.addAttribute("errorMessage", "이미 사용 중인 아이디거나 이메일입니다.");
		return "member/save";
	}
}

	@GetMapping("/login")
	public String loginGet() {
		return "member/login";
	}

	@PostMapping("/loginPost")
	public String loginPost(@ModelAttribute LoginDTO loginDTO, HttpSession session) throws Exception {
		logger.info("로그인 요청: " + loginDTO.getUserId());
		MemberDTO loginResult = memberService.login(loginDTO);
		if (loginResult != null) {
			session.setAttribute("loginMember", loginResult);
			return "redirect:/";
		} else {
			return "member/login";
		}
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	@GetMapping("/find")
	public String findMember() {
		return "member/findMember";
	}

	@ResponseBody
	@GetMapping("/checkId/{userId}")
	public ResponseEntity<String> checkExistID(@PathVariable("userId") String userId){

		logger.info("중복 체크 요청 ID:" + userId);
		MemberDTO member = memberService.checkExistID(userId);

		if (member != null && member.getUserId() != null) {
			logger.info("exist id : " +  userId);

			return ResponseEntity.ok("existID");
		}

		logger.info("not exist id : " + userId);
		return ResponseEntity.ok("notExistID");
	}
	@ResponseBody
	@GetMapping("/checkEmail/{email}")
	public ResponseEntity<String> checkExistEmail(@PathVariable("email") String email){

		logger.info("중복 체크 요청 이메일:" + email);
		MemberDTO member = memberService.checkExistEmail(email);

		if (member != null && member.getEmail() != null) {
			logger.info("exist email : " +  email);

			return ResponseEntity.ok("email");
		}

		logger.info("not exist email : " + email);
		return ResponseEntity.ok("notExistEmail");
	}
	@ResponseBody
	@GetMapping("/findId")
	public ResponseEntity<String> findId(@ModelAttribute MemberDTO memberDTO){
		logger.info("findId : name={}, email={}", memberDTO.getUserName(), memberDTO.getEmail());
		MemberDTO resultMember = memberService.findId(memberDTO);

		if (resultMember != null && resultMember.getUserId() != null ) {
			return ResponseEntity.ok(resultMember.getUserId());
		} else {
			return ResponseEntity.ok("fail");
		}
	}
	@ResponseBody
	@GetMapping("/findPw")
	public ResponseEntity<String> findPw(@ModelAttribute MemberDTO memberDTO){
		logger.info("findPw : id={}, name={}, email={}",memberDTO.getUserId(), memberDTO.getUserName(), memberDTO.getEmail());
		MemberDTO resultMember = memberService.findPw(memberDTO);

		if (resultMember != null ) {
			return ResponseEntity.ok("success");
		} else {
			return ResponseEntity.ok("fail");
		}
	}
}
