package com.example.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	public void sendTempPasswordEmail(String toEmail, String tempPw) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(toEmail);
		message.setSubject("[재호] 임시 비밀번호 발급 안내");

		String text = "안녕하세요.\n"
								 + " 요청하신 임시 비밀번호가 발급되었습니다. \n\n"
								 + "임시 비밀번호: [ " + tempPw + " ]\n\n"
								 + "로그인 후 반드시 비밀번호를 변경해주시기 바랍니다.";
		message.setText(text);

		javaMailSender.send(message);
	}
}
