<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>아이디/비밀번호 찾기</title>
		<script  src="http://code.jquery.com/jquery-latest.min.js"></script>
	</head>
	<body>

		<h2>계정 찾기</h2>

		<div class="tab-container">
			<button class="tab-btn active" onclick="openTab('findId')">아이디 찾기</button>
			<button class="tab-btn" onclick="openTab('findPw')">비밀번호 찾기</button>
		</div>


	<div id="findId" class="find-box active">
		<h3>아이디 찾기</h3>
		<p>가입 시 등록한 이름과 이메일을 입력해주세요</p>
		<form id="findIdForm">
				<p>
					<label>이름 :</label>
					<input type="text" id="userName_id" maxlength="50" placeholder="이름 입력 :">
				</p>
				<p>
					<label>이메일 :</label>
					<input type="text" id=email_id maxlength="100" placeholder="이메일 입력 :">
				</p>
				<button type="button" onclick="findIdFunc()">아이디 찾기</button>
		</form>
	</div>

	<div id="findPw" class="find-box">
		<h3>비밀번호 재설정</h3>
		<p>기입한 정보가 일치시 임시 비밀번호 발급</p>
		<form id="findPwForm">
			<p>
				<label>아이디 :</label>
				<input type="text" id="userId_pw" maxlength="50" placeholder="아이디 입력 :">
			</p>
			<p>
				<label>이름 :</label>
				<input type="text" id="userName_pw" maxlength="50" placeholder="이름 입력">
			</p>
			 <p>
				<label>이메일 :</label>
				<input type="text" id=email_pw maxlength="100" placeholder="이메일 입력 :">
			</p>
			<button type="button" onclick="findPwFunc()">비밀번호 찾기</button>
		</form>
	</div>

	<hr>
	<a href="/member/login">로그인 하러가기</a>  <a href="/member/save">회원가입</a>
	<script>
 		function openTab(tabName) {
		$(".find-box").removeClass("active");
		$(".tab-btn").removeClass("active");
		$("#" + tabName).addClass("active");
		if(tabName === 'findId') $(".tab-btn:first").addClass("active");
		else $(".tab-btn:last").addClass("active");
 		}

 		function findIdFunc() {
			var name = $("#userName_id").val().trim();
			var email = $("#email_id").val().trim();

			if(name == "" || email =="") {
				alert("이름과 이메일 모두 입력해주세요.")
				return;
			}
			$.ajax({
				type: "get",
				url: "/member/findId",
				data: {"userName": name, "email": email},
				dataType: "text",
				success: function(result) {
						if(result != "fail") {
							alert("회원님의 아이디는  [ " + result + " ] 입니다.");
						} else {
							alert("일치하는 회원 정보가 없습니다.");
						}
					}
				});
			}

 		function findPwFunc() {
			var id = $("#userId_pw").val().trim();
			var name = $("#userName_pw").val().trim();
			var email = $("#email_pw").val().trim();

				if(id== "" || name == "" || email =="") {
					alert("모든 정보를 입력해주세요.")
					return;
 				}

				$("#findPwForm button").text("전송 중입니다...").prop("disabled", true);
				$.ajax({
					type: "get",
					url: "/member/findPw",
					data: {"userId": id, "userName": name, "email": email},
					dataType: "text",
					success: function(result) {
							if(result == "success") {
								alert("가입하신 이메일로 임시 비밀번호를 전송했습니다. \n이메일을 확인해주세요.");
								location.href = "/member/login";
							} else {
									alert("일치하는 회원 정보가 없습니다.");
							}
						}
				});
 			}
		</script>
	</body>
</html>