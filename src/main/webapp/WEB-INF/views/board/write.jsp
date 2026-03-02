<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri ="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri ="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글쓰기</title>
    <style>
        /* 간단하게 예쁘게 꾸미기 */
        body { width: 800px; margin: 0 auto; padding: 20px; }
        input, textarea { width: 100%; margin-bottom: 10px; padding: 10px; box-sizing: border-box; }
        button { padding: 10px 20px; }
    </style>


</head>
<body>
	<spring:hasBindErrors name="boardDTO">
		<script>
			var msg="";
			<c:forEach var="error" items="${errors.allErrors}">
				msg += "${error.defaultMessage}\n";
			</c:forEach>
			alert(msg);
		</script>
	</spring:hasBindErrors>
    <h2>✏️ 글 작성하기</h2>

    <form:form action="/board/save" method="post" modelAttribute="boardDTO" onsubmit="return checkForm()">

        <label>제목</label>
        <form:input path="title" id="title" placeholder="제목을 입력하세요" />
	<c:choose>
		<c:when test="${not empty sessionScope.loginMember }">
        	<label>작성자</label>
        	<form:input path="writer" id="writer" placeholder="작성자" readonly="true"/>
			<form:hidden path="boardPw" id="boardPw" value="" /></c:when>
		<c:otherwise>
			<label>작성자(닉네임)</label>
			<form:input path="writer" id="writer" placeholder="작성자 닉네임을 입력해주세요." />
			<label>글 비밀번호</label>
			<form:password path="boardPw" id="boardPw" placeholder="수정/삭제 시 사용할 비밀번호를 입력해주세요." />
		</c:otherwise>
	</c:choose>
	<label>내용</label>
	<form:textarea path="content" id="content" rows="10" placeholder="내용을 입력하세요" />
		<div style="margin-top: 10px;">
			<button type="submit">등록</button>
			<a href="/?page=${searchDTO.page}&searchType=${searchDTO.searchType}&keyword=${searchDTO.keyword}">
				<button type="button">취소</button>
			</a>
		</div>
	</form:form>

	<script>
			function checkForm() {
			var title = document.getElementById("title");
					if (title.value.trim() == "") {
						alert("제목을 입력해주세요.");
						title.focus();
						return false;
						}
			var writer = document.getElementById("writer");
					if (writer.value.trim() == "") {
						alert("작성자를 입력해주세요.");
						writer.focus();
						return false;
						}
			var boardPw = document.getElementById("boardPw");
			if (boardPW && boardPw.type !== "hidden") {
				if(boardPw.value.trim() == ""){
					alert("게시글 비밀번호를 입력해주세요.");
					boardPw.focus();
					return false;
				}
			}
			var content = document.getElementById("content");
					if (content.value.trim() == "") {
						alert("본문을 입력해주세요.");
						content.focus();
						return false;
						}
				return true;
			}
			</script>
</body>
</html>