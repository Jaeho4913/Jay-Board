<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>비밀번호 변경</title>
	<script src="http://code.jquery.com/jquery-latest.min.js"></script>
	<style>
		body { width: 500px; margin: 0 auto; padding: 20px; }
		label { display: inline-block; width: 130px; font-weight: bold;}
		p { margin-bottom: 15px; }
		input { padding: 5px; width: 200px; }
	</style>
</head>
<body>
	<h2>🔒 비밀번호 변경</h2>
	<div style="border: 2px solid #eee; padding: 20px; border-radius: 10px;">
		<form id="updatePwForm">
					<p>
						<label>현재 비밀번호 :</label>
						<input type="password" id="currentPw" required placeholder="기존 비밀번호">
					</p>
					<p>
						<label>신규 비밀번호 :</label>
						<input type="password" id="newPw" required placeholder="영문, 숫자, 특수기호 8~30자">
					</p>
					<p>
						<label>신규 비밀번호 확인 :</label>
						<input type="password" id="newPwConfirm" required placeholder="신규 비밀번호 한번 더 입력">
					</p>
					<button type="button" onclick="changePw()" style="padding: 10px 20px; background-color: #007bff; color: white; border: none; cursor: pointer;">변경하기</button>
					<button type="button" onclick="history.back()" style="padding: 10px 20px;">취소</button>
		</form>
	</div>

	<script>
				function changePw() {
							var currentPw = $("#currentPw").val().trim();
							var newPw = $("#newPw").val().trim();
							var newPwConfirm = $("#newPwConfirm").val().trim();

							if (currentPw == "" || newPw == "" || newPwConfirm == "") {
								alert("모든 항목을 입력해주세요.");
								return;
							}

							if (newPw !== newPwConfirm) {
								alert("비밀번호가 일치하지 않습니다.")
								$("#newPwConfirm").focus();
								return;
							}

							if (currentPw === newPw) {
								alert("기존 비밀번호와 동일합니다. 비밀번호를 다시 입력해주세요.")
								$("#newPw").focus();
								return;
							}

							var passwordReg = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,30}$/;
							if (!passwordReg.test(newPw)) {
								alert("비밀번호는 8~30자의 영문 대/소문자, 숫자, 특수문자를 모두 포함해야 합니다.");
								return;
							}

							$.ajax ({
										  type: "post",
										  url: "/member/updatePw",
										  data: { "currentPw": currentPw, "newPw": newPw},
										  success: function(result) {
														if (result === "success") {
															alert("비밀번호가 변경되었습니다.")
															location.href = "/member/login";
															} else {
																		alert("현재 비밀번호가 일치하지 않습니다.")
																		$("#currentPw").val("").focus()
																		}
														}
							})	;
						}
	</script>
</body>
</html>