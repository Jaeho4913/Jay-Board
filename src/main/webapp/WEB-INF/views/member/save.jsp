<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>회원가입 페이지</title>
		<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	</head>
	<body>

		<h2>회원가입</h2>

		<form action="/member/save" method="post" id="saveForm" onsubmit="return joinCheck()">

			<p>
				<label>아이디 :</label>
				<input type="text" name="userId" id="userId" maxlength="50" data-check-result="fail" placeholder="아이디를 입력하세요">
				<button type="button" onclick="checkId()">중복검사</button>
			</p>

			<p>
				<label>비밀번호 :</label>
				<input type="password" name="password" id="password" maxlength="255" placeholder="비밀번호를 입력하세요">
			</p>

			<p>
				<label>이름 :</label>
				<input type="text" name="userName" id="userName" maxlength="50" placeholder ="이름을 입력하세요">
			</p>

			<p>
				<label>이메일 :</label>
				<input type="text" name="email" id="email" maxlength="100" data-check-result="fail"  placeholder="이메일을 입력하세요">
				<button type="button" onclick="checkEmail()">중복검사</button>
			</p>

			<button type="submit">가입하기</button>
		</form>

		<button id="save">테스트</button>
		<hr>
		<a href="/member/login">이미 아이디가 있다면? 로그인하러 가기</a>
		<script>
			$(document).ready(function() {
				$("#userId").on("input", function() {
					$(this).attr("data-check-result", "fail");
				});
				$("#email").on("input", function() {
					$(this).attr("data-check-result", "fail");
				});
			});
			function checkId() {
				 var userId = $("#userId").val().trim();
				if (userId == "" ){
					alert("아이디를 입력해주세요");
					$("#userId").focus();
					return;
				 }
				 var idReg = /^[a-zA-Z0-9]{8,40}$/;

				 if  (!idReg.test(userId)) {
					alert("아이디는 한글 사용이 불가합니다.");
					$("#userId").focus();
					return;
				 }
				 $.ajax({
						type: "get",
						url: "/member/checkId/" + userId,
						dataType: "text",
						cache: false,
						success: function(result){
							if (result != "existID") {
								alert("사용 가능한 아이디입니다.");
								$("#userId").attr("data-check-result", "success");
							} else {
								alert("이미 사용 중인 아이디입니다.");
								$("#userId").attr("data-check-result", "fail");
								$("#userId").val("").focus();
							}
						}
				 });
			}

			function checkEmail() {
				var email = $("#email").val().trim();
				var emailReg = /^[a-zA-Z-0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
				if (email == "") {
					alert("이메일을 입력해주세요.");
					$("#email").focus();
					return;
				}

				if  (email.length > 100) {
					alert("이메일은 100자 이내여야 합니다.")
					$("#email").focus();
					return;
				}

				if (!emailReg.test(email)) {
					alert("올바른 이메일 형식이 아닙니다.(한글 입력은 불가합니다.)");
					$("#email").focus();
					return;
				}

				$.ajax ({
					type: "get",
					url: "/member/checkEmail/" + email,
					dataType: "text",
					cache: false,
					success: function(result){
						if (result != "existEmail") {
							alert("사용 가능한 이메일입니다.");
							$("#email").attr("data-check-result", "success");
						} else {
							alert("이미 사용중인 이메일입니다.");
							$("#email").attr("data-check-result", "fail");
							$("#email").val("").focus();
						}
					}
				});
			}
			function joinCheck() {

				if ($("#userId").attr("data-check-result") == "fail") {
					alert("아이디 중복 검사를 해주세요.");
					$("#userId").focus();
					return false;
				}
				var password = $("#password").val().trim();
				var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,30}$/;

				if (!passwordReg.test(password)) {
					alert("비밀번호는 8~30자의 영문 대/소문자, 숫자, 특수문자를 모두 포함해야 합니다.");
					$("#password").val("").focus();
					return false;
				}
				var userName = $("#userName").val().trim();
				var userNameReg = /^[가-힣a-zA-Z]{1,50}$/;

				if (userName == "") {
					$("#userName").focus();
					return false;
				}
				if (!userNameReg.test(userName)) {
					alert("이름에는 숫자나 특수문자 입력이 불가합니다.");
					$("#userName").val("").focus();
					return false;
				}

				if ($("#email").attr("data-check-result") == "fail") {
					alert("이메일 중복 검사를 해주세요.");
					$("#email").focus();
					return false;
				}
				return true;
			}
		//var name = $("#userName").val();
		//$("input[name=userName]").val();


		</script>
	</body>
</html>

