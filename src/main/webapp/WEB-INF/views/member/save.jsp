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
				<input type="text" name="userId" id="userId" data-check-result="fail" placeholder="아이디를 입력하세요">
				<button type="button" onclick="checkId()">중복검사</button>
			</p>

			<p>
				<label>비밀번호 :</label>
				<input type="text" name="password" placeholder="비밀번호를 입력하세요">
			</p>

			<p>
				<label>이름 :</label>
				<input type="text" name="userName" id="userName" placeholder="이름을 입력하세요">
			</p>

			<p>
				<label>이메일 :</label>
				<input type="text" name="email" placeholder="이메일을 입력하세요">
			</p>

			<button>가입하기</button>
		</form>

		<button id="save">테스트</button>
		<hr>
		<a href="/member/login">이미 아이디가 있다면? 로그인하러 가기</a>
		<script>
			$(document).ready(function() {
				$("#userId").on("input", function() {
					$(this).attr("data-check-result", "fail");
				});
			});
			function checkId() {
				 var userId = $("#userId").val().trim();
				var $userIdInput = $("#userId");

				 if (userId == "" ){
					alert("아이디를 입력해주세요");
					$userIdInput.focus();
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
								$userIdInput.attr("data-check-result", "success");
							} else {
								alert("이미 사용 중인 아이디입니다.");
								$userIdInput.val("").focus();
								$userIdInput.attr("data-check-result", "fail");
							}
						}
				 });
			}
			function joinCheck() {
				if ($("#userId").attr("data-check-result") != "success") {
					alert("아이디 중복 검사를 먼저 해주세요!");
					$("#userId").focus();
					return false;
				}
				return true;
			}

			var name = $("#userName").val();
			$("input[name=userName]").val();


		</script>
	</body>
</html>

