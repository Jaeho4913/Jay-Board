<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>글 수정</title>
    <style>
        body { width: 800px; margin: 0 auto; padding: 20px; }
        input, textarea { width: 100%; margin-bottom: 10px; padding: 10px; box-sizing: border-box; }
        button { padding: 10px 20px; cursor: pointer;}
    </style>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <h2>글 수정하기</h2>

	<div id="updateFormArea">
        <input type="hidden" id="idx" value="">
        <input type="hidden" id="boardPw" value="">

        <label>제목</label>
        <input type="text" id="title" required>

        <label>작성자</label>
        <input type="text" id="writer" readonly>

        <label>내용</label>
        <textarea id="content" rows="10"></textarea>

        <div style="margin-top: 10px;">
        	<button type="button" onclick="updateBoard()" style="background-color: #28a745; color: white; border: none;">수정</button>
        	<button type="button" onclick="cancelUpdate()" style="padding: 10px 20px;">취소</button>
        </div>
	</div>

    <script>
    	$(document).ready(function(){
		const urlParams = new URLSearchParams(window.location.search);

		const idx = urlParams.get('idx');
		const boardPw = urlParams.get('boardPw') || "";

		$.ajax({
			type: "GET",
			url: "/board/getDetail",
			data: {idx: idx},
			dataType: "json",
			success: function(board){

				console.log(board);
				$("#idx").val(board.idx);
				$("#boardPw").val(boardPw);
				$("#title").val(board.title);
				$("#writer").val(board.writer);
				$("#content").val(board.content);
			},
			error: function() {
				alert("로딩 실패");
			}
		});
    });

    function updateBoard() {
			var idx = $("#idx").val();
			var title = $("#title").val().trim();
			var writer = $("#writer").val().trim();
			var content = $("#content").val().trim();
			var boardPw = $("#boardPw").val().trim();

			if(title === "" || content === "") {
				alert("제목과 내용은 필수 입력입니다.");
				return;
			}

    	$.ajax({
			type: "POST",
			url: "/board/update",
			data: {
					idx: idx,
					title: title,
					writer: writer,
					content: content,
					boardPw: boardPw
					},
			success: function(result) {

				console.log(result);
				if (result === "success") {
					alert("수정 완료되었습니다.");
					location.href = "/board/view?idx=" + idx;
				} else {
					alert("게시글을 수정할 수 없습니다.");
				}
			},
			error: function() {
				alert("오류 발생")
			}
    	});
    }

    function cancelUpdate() {
		var idx = $("#idx").val();
		location.href = '/board/view?idx=' + idx;
    }
    </script>
</body>
</html>