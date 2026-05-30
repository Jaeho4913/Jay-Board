<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
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
    <h2>✏️ 글 작성하기</h2>

    <form action="/board/save" method="post">

        <label>제목</label>
        <input type="text"
        		   name="title"
        		   id="title"
        		   maxlength="100"
        		   placeholder="제목을 입력하세요" required>
        	<div style="text-align:right; margin-bottom:10px;">
        		<span id="titleLength">0</span>/100
        	</div>

        <label>작성자</label>
        <input type="text" name="writer" placeholder="이름" required>

        <label>내용</label>
        <textarea name="content"
        				id = "content"
        				rows="10"
        				maxlength="3000"
        				placeholder="내용을 입력하세요"></textarea>
       	<div style="text-align:right; margin-bottom:10px;">
    		<span id="contentLength">0</span>/3000
		</div>

        <button type="submit">등록</button>
        <button type="button" onclick="history.back()">취소</button>
    </form>
    <script>
    	$(document).ready(function() {
    		console.log("write.jsp script loaded");
			$("#title").on("input", function(){
				$("#titleLength").text($(this).val().length);
			});

    		$("#content").on("input", function() {
					$("#contentLength").text($(this).val().length);
			});
    	});
    </script>
</body>
</html>