$(function() {
    const boardGroupIdx = $("#livePostList").attr("data-board-group-idx");
    const sentinel = document.getElementById("livePostSentinel");

    let nextCursor = null;
    let hasNext = true;
    let isLoading = false;
    let isSubmiting = false;

    function loadPosts() {
        if (isLoading || !hasNext) {
            return;
        }
        isLoading = true;
        $("#livePostRetry").addClass("is-hidden");
        sentinel.textContent = "글을 불러오는 중입니다.";
        const params = {
            boardGroupIdx: boardGroupIdx
        };
        if (nextCursor != null) {
            params.beforePostIdx = nextCursor;
        }
        $.ajax({
            type: "GET",
            url: "/board/live/posts",
            data: params,
            dataType: "json",
            success: function(response) {
                appendPosts(response.items);
                nextCursor = response.nextCursor;
                hasNext = response.hasNext;
                if (!hasNext) {
                    if ($("#livePostList").children(".live-post").length === 0) {
                        sentinel.textContent = "등록된 글이 없습니다.";
                    } else {
                        sentinel.textContent = "모든 글을 불러왔습니다.";
                    }
                    observer.unobserve(sentinel);
                } else {
                    sentinel.textContent = "이전 글 불러오기";
                    observer.observe(sentinel);
                }
            },
            error: function(xhr) {
                sentinel.textContent = "글을 불러오지 못했습니다. 다시 시도해주세요.";
                observer.unobserve(sentinel);
                $("#livePostRetry").removeClass("is-hidden");
            },
            complete: function() {
                isLoading = false;
            }
        });
    }

    function createPostCard(post) {
        const card = $("<div>").addClass("live-post").attr("data-post-idx", post.livePostIdx);
        const writer = $("<div>").addClass("live-post-writer").text(post.writer);
        const content = $("<div>").addClass("live-post-content").text(post.content);
        const date = $("<div>").addClass("live-post-date").text(post.createdAt);
        card.append(writer, content, date);
		return card;
    }

    function appendPosts(items) {
        items.forEach(function(post) {
			const card = createPostCard(post);
            $("#livePostList").append(card);
        });
    }
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(function(entry) {
            if (entry.isIntersecting) {
                loadPosts();
            }
        });
    });
    $("#livePostRetry").on("click", function() {
        loadPosts();
    });
    $("#livePostForm").on("submit", function(event) {
        event.preventDefault();
        if (isSubmiting) {
            return;
        }
        const content = $("#livePostContent").val();
        if (content.trim() === "") {
            alert("내용을 입력해주세요.");
            return;
        }
        if (Array.from(content).length > 500) {
            alert("내용을 500자 이내료 입력해주세요.");
            return;
        }
        isSubmiting = true;

        $.ajax({
            type: "POST",
            url: "/board/live/posts",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            data: JSON.stringify({ boardGroupIdx: Number(boardGroupIdx), content: content }),
            success: function(savedPost) {
                console.log(savedPost);
				const card = createPostCard(savedPost);
				$("#livePostList").prepend(card);
                $("#livePostContent").val("");
            },
            error: function(xhr) {
                alert("라이브 글을 저장하지 못했습니다.");
            },
            complete: function() {
                isSubmiting = false;
            }
        });
    });
    observer.observe(sentinel);
    loadPosts();
});