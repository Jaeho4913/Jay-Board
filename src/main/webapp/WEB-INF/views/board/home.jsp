<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<title>게시글 목록</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>
	<div style="width: 800px; margin: 0 auto; text-align: center;">
		<h2>목록</h2>

	<div id="authArea" style="border: 2px solid #eee; padding: 15px; margin: 20px 0; border-radius: 10px; background-color: #f9f9f9;">
		로딩중입니다.
	</div>

	<div style="margin-bottom: 10px;">
		<select id="searchType">
			<option value="title">제목</option>
			<option value="content">내용</option>
			<option value="writer">작성자</option>
		</select>
		<input type="text" id="keyword" placeholder="검색어를 입력하세요"/>
		<button type="button" id="btnSearch">검색</button>
	</div>

	<table border="1" style="width: 100%; border-collapse: collapse; text-align: center;">
		<thead>
			<tr style="background-color: #f2f2f2;">
				<th style="padding: 10px;">번호</th>
				<th>제목</th>
				<th>작성자</th>
				<th>작성일</th>
			</tr>
		</thead>
		<tbody id="boardList">
			<tr><td colspan="4">로딩중입니다.</td></tr>
		</tbody>
	</table>

	<div id="pagination" style="text-align: center; margin-top: 20px;">
	</div>
</div>

<script>
	$(document).ready(function() {
		let urlParams = new URLSearchParams(window.location.search);
		let page=urlParams.get('page') || 1;

		$('#searchType').val(urlParams.get('searchType') || '');
		$('#keyword').val(urlParams.get('keyword') || '');

		getBoardList(page);

		$('#btnSearch').click(function() {
			getBoardList(1);
		});
		$('#keyword').keyup(function(e){
			if (e.keyCode == 13) {
				getBoardList(1);
			}
		});
	});

	function getBoardList(page) {
		let searchType = $('#searchType').val();
		let keyword = $('#keyword').val();

		$.ajax({
			type: 'GET',
			url: '/api/boards',
			data: {page: page, searchType: searchType, keyword: keyword},
			dataType: 'json',
			success: function(res){
				/* const url = `/board/list?page=${page}&searchType=${searchType}&keyword=${keyword}`;
*/
				setAuth(res);
				setBoardList(res.boardData);
				setPaging(res.boardData);
			},
			error: function() {
				alert("로딩 실패")
			}
		});
	}

	function setAuth(res){
		let authArea = $('#authArea');
		const boardData = res.boardData;
		const writeUrl = '/write?page=' + boardData.searchDTO.page + '&searchType=' +boardData.searchDTO.searchType + '&keyword=' + boardData.searchDTO.keyword;
		let html = '';

		if (res.isLogin) {
			html += `
			<div style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
				<span style="font-size: 1.1em;">안녕하세요 <strong>${res.loginName}</strong>님</span>
				<div>
					<button onclick="location.href='/member/updatePw'"
						style="cursor:pointer; background-color:#ffc107;border:none;padding:6px 12px;margin-right:5px;">
						비밀번호 변경
					</button>
					<button onclick="location.href='${writeUrl}'"
						style="cursor:pointer;background-color:#28a745;color:white;border:none;padding:6px 12px;margin-right:5px;">
						글쓰기
					</button>
					<button onclick="location.href='/member/logout'"
						style="cursor:pointer;background-color:#dc3545;color:white;border:none;padding:6px 12px;">
						로그아웃
					</button>
				</div>
			</div>
			`;
		} else {
		html += `
			<form action="/member/loginPost" method="post"
				style="display:flex; align-items:center; justify-content:center; gap:10px; margin:0;">

			<label>
				ID:
				<input type="text" name="userId" required
					style="width:120px; padding:5px;">
			</label>

			<label>
				PW:
				<input type="password" name="password" required
					style="width:120px; padding:5px;">
			</label>

			<button type="submit"
           		style="cursor:pointer; background-color:#007bff; color:white; border:none; padding:6px 12px;">
            	로그인
        	</button>

			 <button type="button"
            	onclick="location.href='/member/save'"
            	style="cursor:pointer; background-color:#6c757d; color:white; border:none; padding:6px 12px;">
            	회원가입
        	</button>

       	 	<button type="button"
            	onclick="location.href='${writeUrl}'"
            	style="cursor:pointer; background-color:#28a745; color:white; border:none; padding:6px 12px;">
             	글쓰기
        	</button>

            </form>

            <div style="margin-top:10px; font-size:0.9em;">
            	<a href="/member/find" style="color:#666; text-decoration:none;">
                아이디/비밀번호 찾기
            	</a>
       	 	</div>
        	`;
		}
		authArea.html(html);
	}

	function setBoardList(boardData) {
		const tbody = $('#boardList');
		tbody.empty();

		const items = boardData.boardList;

		if (!items || items.length === 0) {
			tbody.append('<tr><td colspan="4">등록된 게시글이 없습니다.</td></tr>');
			return;
		}

		let html = '';

		$.each(items, function(index, item){
			const detailUrl = '/board/view?idx=' + item.idx + '&page=' + boardData.searchDTO.page + '&searchType=' + boardData.searchDTO.searchType + '&keyword=' + boardData.searchDTO.keyword;
			html += `
			<tr>
				<td>${item.idx}</td>
				<td><a href="${detailUrl}">${item.title}</a></td>
				<td>${item.writer}</td>
				<td>${item.createdAt}</td>
			</tr>
			`;
		});
		tbody.append(html);
	}

	function setPaging(boardData) {
		const pagination = $('#pagination');
		const page = boardData.searchDTO.page;
		const startPage = boardData.startPage;
		const endPage = boardData.endPage;
		const totalPage = boardData.totalPage;

		const html = [];

		if (page > 1) {
			html.push(`<a href="#" onclick="getBoardList(1); return false;" style="font-weight:bold;">[<<]</a>`);
			html.push(`<a href="#" onclick="getBoardList(${page-1}); return false;" style="font-weight:bold;">[<]</a>`);
		}

		for (let i = startPage; i <= endPage; i++) {
			if(page === i) {
				html.push(`<span style="color:red; font-weight:bold; margin:0 5px;">[${i}]</span>`);
			} else {
				 html.push(`<a href="#" onclick="getBoardList(${i}); return false;" style="margin:0 5px;">[${i}]</a>`);
			}
		}

		if (page < totalPage) {
			html.push(`<a href="#" onclick="getBoardList(${page + 1}); return false;" style="font-weight:bold;">[>]</a>`);
	        html.push(`<a href="#" onclick="getBoardList(${totalPage}); return false;" style="font-weight:bold;">[>>]</a>`);
		}
		pagination.html(html.join(''));
	}

</script>
</body>
</html>