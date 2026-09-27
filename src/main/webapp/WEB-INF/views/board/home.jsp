<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<%@ taglib prefix="c" uri="jakarta.tags.core" %>
		<!DOCTYPE html>
		<html>

		<head>
			<link rel="stylesheet" href="/css/board-list.css">
			<meta charset="UTF-8">
			<title>게시글 목록</title>
			<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
		</head>

		<body>
			<div style="width: 800px; margin: 0 auto; text-align: center;">
				<h2>목록</h2>

				<c:forEach var="boardGroup" items="${boardGroups}">
					<a href="/board/list?boardGroupIdx=${boardGroup.boardGroupIdx}">
						<c:out value="${boardGroup.boardGroupName}" />
					</a>
				</c:forEach>

				<div id="authArea"
					style="border: 2px solid #eee; padding: 15px; margin: 20px 0; border-radius: 10px; background-color: #f9f9f9;">
					로딩중입니다.
				</div>

				<div class="board-search-area"></div>
					<span id="gallerySortArea" class="is-hidden">
						<select id="gallerySort" aria-label="게시글 정렬">
							<option value="latest">최신순</option>
							<option value="oldest">오래된순</option>
							<option value="viewDesc">조회수 높은순</option>
							<option value="viewAsc">조회수 낮은순</option>
							<option value="likeDesc">공감 높은순</option>
							<option value="likeAsc">공감 낮은순</option>
							<option value="replyDesc">댓글 많은순</option>
							<option value="replyAsc">댓글 적은순</option>
						</select>
					</span>
					<select id="searchType">
						<option value="title">제목</option>
						<option value="content">내용</option>
						<option value="writer">작성자</option>
					</select>
					<input type="text" id="keyword" placeholder="검색어를 입력하세요" />
					<button type="button" id="btnSearch">검색</button>
				</div>


				<table id="normalBoardTable" border="1"
					style="width: 100%; border-collapse: collapse; text-align: center;">
					<thead>
						<tr style="background-color: #f2f2f2;">
							<th style="padding: 10px;">번호</th>
							<th>제목</th>
							<th>작성자</th>
							<th>
								작성일
								<span style="cursor:pointer;" onclick="changeSort('latest')">▲</span>
								<span style="cursor:pointer;" onclick="changeSort('oldest')">▼</span>
							</th>
							<th>
								조회수
								<span style="cursor:pointer;" onclick="changeSort('viewDesc')">▲</span>
								<span style="cursor:pointer;" onclick="changeSort('viewAsc')">▼</span>
							</th>
							<th>
								공감
								<span style="cursor:pointer;" onclick="changeSort('likeDesc')">▲</span>
								<span style="cursor:pointer;" onclick="changeSort('likeAsc')">▼</span>
							</th>
							<th>
								댓글 수
								<span style="cursor:pointer;" onclick="changeSort('replyDesc')">▲</span>
								<span style="cursor:pointer;" onclick="changeSort('replyAsc')">▼</span>
							</th>
						</tr>
					</thead>
					<tbody id="boardList">
						<tr>
							<td colspan="7">로딩중입니다.</td>
						</tr>
					</tbody>
				</table>

				<div id="galleryBoardList" style="display: none;">


				</div>

				<div id="pagination" style="text-align: center; margin-top: 20px;">
				</div>
			</div>

			<script>
				let currentSortType = 'latest';
				let currentBoardGroupIdx;
				$(document).ready(function () {
					let urlParams = new URLSearchParams(window.location.search);
					let page = urlParams.get('page') || 1;
					currentBoardGroupIdx = urlParams.get('boardGroupIdx');

					$("#searchType").val(urlParams.get('searchType') || 'title');
					$("#keyword").val(urlParams.get('keyword') || '');
					currentSortType = urlParams.get('sortType') || 'latest';
					$("#gallerySort").val(currentSortType);
					
					getBoardList(page);

					$("#btnSearch").click(function () {
						getBoardList(1);
					});
					$("#gallerySort").on("change", function() {
						changeSort($(this).val());
					});
					$('#keyword').keyup(function (e) {
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
						data: {page: page, searchType: searchType, keyword: keyword, sortType: currentSortType, boardGroupIdx: currentBoardGroupIdx},
						dataType: 'json',
						success: function (res) {
							let url = '/board/list?page=' + page
								+ '&searchType=' + searchType
								+ '&keyword=' + keyword
								+ '&sortType=' + currentSortType;

							if (currentBoardGroupIdx) {
								url += '&boardGroupIdx=' + currentBoardGroupIdx;
							}

							history.replaceState(null, '', url);
							console.log(res);

							setAuth(res);
							setBoardList(res.boardData);
							setPaging(res.boardData);
						},
						error: function (xhr) {
							if (xhr.status === 400) {
								alert(xhr.responseJSON.message);
								location.href = "/";
								return;
							}
							alert("로딩 실패")
						}
					});
				}

				function changeSort(sortType) {
					currentSortType = sortType;
					getBoardList(1);
				}

				function setAuth(res) {
					let authArea = $('#authArea');
					const boardData = res.boardData;
					const writeUrl = '/write?page=' + boardData.searchDTO.page + '&searchType=' + boardData.searchDTO.searchType + '&keyword=' + boardData.searchDTO.keyword + '&sortType=' + boardData.searchDTO.sortType + '&boardGroupIdx=' + currentBoardGroupIdx;
					let html = '';

					if (res.isLogin) {
						html += `
			<div style="display: flex; justify-content: space-between; align-items: center; padding: 0 20px;">
				<span style="font-size: 1.1em;"> <strong>\${res.loginName}</strong>님, 환영합니다.</span>
				<div>
					<button onclick="location.href='/member/updatePw'"
						style="cursor:pointer; background-color:#ffc107;border:none;padding:6px 12px;margin-right:5px;">
						비밀번호 변경
						
					</button>
					<button onclick="goWrite('\${writeUrl}')"
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
					if (boardData.boardType === 'GALLERY') {
						$("#normalBoardTable").hide();
						$("#galleryBoardList").css("display", "grid");
						setGalleryBoardList(boardData);
						$("#gallerySortArea").removeClass("is-hidden");
						return;
					} else {
						$("#normalBoardTable").show()
						$("#galleryBoardList").hide();
						$("#gallerySortArea").addClass("is-hidden");
					}
					const tbody = $('#boardList');
					tbody.empty();

					const items = boardData.boardList;

					if (!items || items.length === 0) {
						tbody.append('<tr><td colspan="7">등록된 게시글이 없습니다.</td></tr>');
						return;
					}

					let html = '';


					let currentPage = boardData.searchDTO.page;
					let pageSize = boardData.searchDTO.size;

					$.each(items, function (index, item) {
						let boardNumber = ((currentPage - 1) * pageSize) + index + 1;
						let detailUrl = '/board/view?idx=' + item.idx + '&page=' + boardData.searchDTO.page + '&searchType=' + boardData.searchDTO.searchType + '&keyword=' + boardData.searchDTO.keyword + '&sortType=' + boardData.searchDTO.sortType;
						if (currentBoardGroupIdx) {
							detailUrl += '&boardGroupIdx=' + currentBoardGroupIdx;
						}

						let boardTime = item.createdAt.replace('T', ' ');

						html += `
			<tr>
				<td>\${boardNumber}</td>
				<td><a href="\${detailUrl}">\${item.title}</a></td>
				<td>\${item.writer}</td>
				<td>\${boardTime}</td>
				<td>\${item.viewCnt}</td>
				<td>\${item.likeCnt}</td>
				<td>\${item.replyCnt || 0}</td>
			</tr>
			`;
					});
					tbody.append(html);
				}

				function setGalleryBoardList(boardData) {
					$("#galleryBoardList").empty();
					const items = boardData.boardList;
					if (!items || items.length === 0) {
						$("#galleryBoardList").text("등록된 게시글이 없습니다.");
						return;
					}
					$.each(items, function (index, item) {
						let detailUrl = '/board/view?idx=' + item.idx + '&page=' + boardData.searchDTO.page + '&searchType=' + boardData.searchDTO.searchType + '&keyword=' + encodeURIComponent(boardData.searchDTO.keyword) + '&sortType=' + boardData.searchDTO.sortType;
						if (currentBoardGroupIdx) {
							detailUrl += '&boardGroupIdx=' + currentBoardGroupIdx;
						}
						const imageUrl = "/board/image?idx=" + item.idx;
						const card = $("<a>")
							.attr("href", detailUrl)
							.addClass("gallery-card");
						const image = $("<img>")
							.attr("src", imageUrl)
							.attr("alt", "게시글 대표 이미지")
							.addClass("gallery-image");
						const title = $("<div>")
							.addClass("gallery-title")
							.text(item.title);
						const writer = $("<div>")
							.addClass("gallery-writer")
							.text(item.writer);
						const createdAt = $("<div>")
							.addClass("gallery-date")
							.text(item.createdAt.replace('T', ' '));
						const stats = $("<div>")
							.addClass("gallery-stats");
						const viewCnt = $("<span>")
							.addClass("gallery-view")
							.text("조회수 " + (item.viewCnt ?? 0));
						const likeCnt = $("<span>")
							.addClass("gallery-like")
							.text(" 공감 " + (item.likeCnt ?? 0));
						const replyCnt = $("<span>")
							.addClass("gallery-reply")
							.text(" 댓글 수 " + (item.replyCnt ?? 0));

						stats.append(viewCnt, likeCnt, replyCnt);

						card.append(image, title, writer, createdAt, stats);

						$("#galleryBoardList").append(card);
					});
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
						html.push(`<a href="#" onclick="getBoardList(\${page-1}); return false;" style="font-weight:bold;">[<]</a>`);
					}

					for (let i = startPage; i <= endPage; i++) {
						if (page === i) {
							html.push(`<span style="color:red; font-weight:bold; margin:0 5px;">[\${i}]</span>`);
						} else {
							html.push(`<a href="#" onclick="getBoardList(\${i}); return false;" style="margin:0 5px;">[\${i}]</a>`);
						}
					}

					if (page < totalPage) {
						html.push(`<a href="#" onclick="getBoardList(\${page + 1}); return false;" style="font-weight:bold;">[>]</a>`);
						html.push(`<a href="#" onclick="getBoardList(\${totalPage}); return false;" style="font-weight:bold;">[>>]</a>`);
					}
					pagination.html(html.join(''));
				}

				function goWrite(writeUrl) {
					if (!currentBoardGroupIdx) {
						alert("게시판을 먼저 선택해주세요.")
						return;
					}
					location.href = writeUrl;
				}

			</script>
		</body>

		</html>