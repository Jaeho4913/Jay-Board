<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 목록</title>
</head>
<body>
    <div style="width: 800px; margin: 0 auto; text-align: center;">

        <h2>📋 게시글 목록</h2>

		<div style="border: 2px solid #eee; padding: 15px; margin: 20px 0; border-radius: 10px; background-color: #f9f9f9;">
			<c:choose>
				<%--로그인을 안 했을 경우 (세션이 비어있음)--%>
				<c:when test="${empty sessionScope.loginMember}">
					<form action="/member/loginPost" method="post" style="display: flex; align-items: center; justify-content: center; gap: 10px">
						<label>ID : <input type="text" name="userId" required style="width: 150px; margin:0; padding: 5px;
						"></label>
						<label>PW : <input type="password" name="password" required style="width: 150px; margin:0; padding: 5px;"></label>

						<button type="submit" style="cursor: pointer; background-color: #007bff; color: white; border: none; padding: 6px 12px;">로그인</button>
						<button type="button" onclick="location.href= '/member/save'" style="cursor: pointer; background-color: #6c757d; color: white; border: none; padding: 6px 12px;">회원가입</button>
					</form>

					<div style="margin-top: 10px; font-size: 0.9em;">
								<a href="/member/find" style="color: #666; text-decoration: none;">아이디/비밀번호 찾기</a>
					</div>
				</c:when>
				<%--로그인을 했을 경우 (세션에 정보가 있음)--%>
				<c:otherwise>
					<div style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
						<span style="font-size: 1.1em;">안녕하세요<strong>${sessionScope.loginMember.userName}</strong>님, 환영합니다!</span>

						<div>
							<button onclick="location.href='/write?page=${response.searchDTO.page}&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}'"
							        style="cursor: pointer; background-color: #28a745; color: white; border: none; padding: 6px 12px; margin-right: 5px;">
								✏️ 글쓰기
							</button>

							<button onclick="location.href='/member/logout'"
									style="cursor: pointer; background-color: #dc3545; color: white; border: none; padding: 6px 12px;">
								로그아웃
							</button>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<h3>현재 searchType 값: [${searchType}]</h3>
		<div style="margin-bottom: 10px;">
			<form action="/" method="get">
				<select name="searchType">
				    <option value="title" <c:if test="${response.searchDTO.searchType == 'title'}">selected</c:if>>제목</option>
				    <option value="content" <c:if test="${response.searchDTO.searchType == 'content'}">selected</c:if>>내용</option>
				    <option value="writer" <c:if test="${response.searchDTO.searchType == 'writer'}">selected</c:if>>작성자</option>
				</select>

				<input type="text" name="keyword" value="${response.searchDTO.keyword}" placeholder="검색어를 입력하세요"/>

				<button id="searchBtn">검색</button>
			</form>

        <table border="1" style="width: 100%; border-collapse: collapse; text-align: center;">
            <tr style="background-color: #f2f2f2;">
                <th style="padding: 10px;">번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
            </tr>

            <c:forEach items="${response.boardList}" var="board">
                <tr>
                    <td style="padding: 10px;">${board.idx}</td>
                    <td><a href="/board/view?idx=${board.idx}&page=${response.searchDTO.page}&keyword=${response.searchDTO.keyword}&searchType=${response.searchDTO.searchType}">
						${board.title}
						</a>
					</td>
                    <td>${board.writer}</td>
                    <td>${board.createdAt}</td>
                </tr>
            </c:forEach>
        </table>

        <div style="text-align: center; margin-top: 20px;">

            <c:if test="${response.searchDTO.page > 1}">
                <a href="/?page=1&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}" style="font-weight: bold;">[<<]</a>
            </c:if>

            <c:if test="${response.searchDTO.page > 1}">
                 <a href="/?page=${response.searchDTO.page - 1}&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}">[<]</a>
            </c:if>

            <c:forEach begin="${response.startPage}" end="${response.endPage}" var="i">
                <c:choose>
                    <c:when test="${response.searchDTO.page == i}">
                        <span style="color: red; font-weight: bold; margin: 0 5px;">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="/?page=${i}&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}" style="margin: 0 5px;">[${i}]</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${response.searchDTO.page < response.totalPage}">
                 <a href="/?page=${response.searchDTO.page + 1}&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}">[>]</a>
            </c:if>

            <c:if test="${response.searchDTO.page < response.totalPage}">
                <a href="/?page=${response.totalPage}&searchType=${response.searchDTO.searchType}&keyword=${response.searchDTO.keyword}" style="font-weight: bold;">[>>]</a>
            </c:if>

        </div>
        </div>

</body>
</html>