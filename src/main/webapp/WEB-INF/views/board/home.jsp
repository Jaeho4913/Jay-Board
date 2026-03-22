<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 목록</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <div style="width: 800px; margin: 0 auto; text-align: center;">

        <h2>📋 게시글 목록</h2>
		<div id="topMenuArea" style="border : 2px solid #eee; padding: 15px margin: 20px 0; border-radius: 10px; background-color: #f9f9f9;">
			로딩중...
			</div>
		<div style="margin-bottom: 10px;">
			<select id="searchType">
				<option value="title">제목</option>
				<option value="content">내용</option>
				<option value="writer">작성자</option>
			</select>
			<input type="text" id="keyword" placeholder="검색어를 입력하세요"/>
			<button id="searchBtn" onclick="loadList(1)">검색</button>
			</div>
			<table border="1" style="width: 100%; border-collapse: collapse; text-algin: center;">
				<thead>
					<tr style="background-color: #f2f2f2;">
						<th style="padding: 10px;">번호</th>
						<th>제목</th>
						<th>작성자</th>
						<th>작성일</th>
					</tr>
				</thead>
				<tbody id="boardListBody">
					<tr><td colspan="4">게시글을 불러오는 중입니다...</td></tr>
				</tbody>
			</table>

			<div id="pagenationArea" style="text-align: center; margin-top: 20px;">
			</div>
		</div>

		<script>
			$(document).ready(function() {
			const urlParams = new URLSearchParams(window.location.search);
			if(urlParams.get('searchType')) $("#searchType").val(urlParams.get('searchType'));
			if(urlParams.get('keyword')) $("#keyword").val(urlParams.get('keyword'));

			let page = urlParams.get('page') || 1;
			loadList(page);
			});

			function loadList(page) {
				let searchType = $("#searchType").val();
				let keyword = $("#keyword").val();

				$.ajax({
					type: "GET",
					url: "/board/getList",
					data: {page: page, searchType: searchType, keyword: keyword},
					dataType: "json",
					success: function(res) {
						renderTopMenu(res);
						renderTable(res.response);
						renderPagenation(res.response);
					},
					error: function(err) {
						alert("데이터 로딩 실패");
					}
				});
			}

			function renderTopMenu(res) {
				let pr = res.response;
				let writeUrl = '/write?page=' + pr.searchDTO.page + '&searchType=' + pr.searchDTO.searchType + '&keyword=' + pr.searchDTO.keyword;

				let html = '';
				if(res.isLogin) {
					html = `
						<div style="display: flex; justify-content: space-between; algin-items: center; padding: 0 20px;">
						<span style="font-size: 1.1em;">안녕하세요<strong>\${res.userName}</strong>님, 환영합니다.</span>
						<div>
							<button onclick="location.href='/member/updatePw'"  style="cursor:pointer; background-color:#ffc107; border: none; padding: 6px 12px; margin-right: 5px;">비밀번호 변경</button>
							<button onclick="location.href='\${writeUrl}'"  style="cursor:pointer; background-color:#28a745; color: white;  border: none; padding: 6px 12px; margin-right: 5px;">✏️ 글쓰기</button>
							<button onclick="location.href='/member/logout'"  style="cursor:pointer; background-color:#dc3545; color: white;  border: none; padding: 6px 12px;">로그아웃</button>
					</div>
				</div>`;
				} else {
					html = `
						<form action="/member/loginPost" method="post" style="display: flex; align-items: center; justify-content: center; gap: 10px">
							<label>ID : <input type="text" name="userId" required style="width: 150px; margin:0; padding: 5px;"></label>
                        	<label>PW : <input type="password" name="password" required style="width: 150px; margin:0; padding: 5px;"></label>
                        	<button type="submit" style="cursor: pointer; background-color: #007bff; color: white; border: none; padding: 6px 12px;">로그인</button>
                        	<button type="button" onclick="location.href='/member/save'" style="cursor: pointer; background-color: #6c757d; color: white; border: none; padding: 6px 12px;">회원가입</button>
                        	<button type="button" onclick="location.href='\${writeUrl}'" style="cursor: pointer; background-color: #28a745; color: white; border: none; padding: 6px 12px;">✏️ 글쓰기</button>
                    	</form>
                    	<div style="margin-top: 10px; font-size: 0.9em;">
                        	<a href="/member/find" style="color: #666; text-decoration: none;">아이디/비밀번호 찾기</a>
                    	</div>`;
           		}
            $("#topMenuArea").html(html);
			}

			function renderTable(pr) {
				let tbody = '';
				if( pr.boardList.length === 0) {
					tbody = '<tr><td colspan="4">등록된 게시글이 없습니다.</td></tr>';
				} else {
					pr.boardList.forEach(function(board) {
						let detailUrl = '/board/view?idx=' + board.idx + '&page=' + pr.searchDTO.page + '&searchType=' + pr.searchDTO.searchType + '&keyword=' + pr.searchDTO.keyword;
						tbody += `
							<tr>
								<td style="padding: 10px;">\${board.idx}</td>
								<td><a href="\${detailUrl}">\${board.title}</a></td>
								<td>\${board.writer}</td>
								<td>\${board.createdAt}</td>
							</tr>`;
					});
				}
				$("#boardListBody").html(tbody);
			}

			function renderPagenation(pr) {
				let pageHtml = '';
				if (pr.searchDTO.page > 1) {
					pageHtml += `<a href="#" onclick="loadList(1); return false;" style="font-weight: bold;">[<<]</a>`;
					pageHtml += `<a href="#" onclick="loadList(\${pr.searchDTO.page - 1}); return false;" style="font-weight: bold;">[<]</a>`;
				}
				for (let i = pr.startPage; i <= pr.endPage; i++) {
					if (pr.searchDTO.page === i) {
						pageHtml += `<span style="color: red; font-wight: bold; margin: 0 5px">[\${i}]</span>`;
					} else {
						pageHtml += `<a href="#" onclick="loadList(\${i}); return false;" style="margin: 0 5px;">[\${i}]</a>`;
					}
				}
				if (pr.searchDTO.page < pr.totalPage) {
					pageHtml += `<a href="#" onclick="loadList(\${pr.searchDTO.page + 1}); return false;" style="font-weight: bold;">[>]</a>`;
					pageHtml += `<a href="#" onclick="loadList(\${pr.totalPage}); return false;" style="font-weight: bold;">[>>]</a>`;
				}
				$("#pagenationArea").html(pageHtml);
			}
		</script>
</body>
</html>