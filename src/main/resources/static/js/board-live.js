$(function() {
    const boardGroupIdx = $("#livePostList").attr("data-board-group-idx");
    const sentinel = document.getElementById("livePostSentinel");

    let nextCursor = null;
    let hasNext = true;
    let isLoading = false;

    function loadPosts() {
        if (isLoading || !hasNext) {
            return;
        }
        isLoading = true;
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
					sentinel.textContent ="모든 글을 불러왔습니다.";
					observer.unobserve(sentinel);
				}
            },
            error: function(xhr) {
                alert("라이브 글을 불러오지 못했습니다.")
            },
            complete: function() {
                isLoading = false;
            }
        });
    }
    function appendPosts(items) {
        items.forEach(function(post) {
            const card = $("<div>").addClass("live-post").attr("data-post-idx", post.livePostIdx);
            const writer = $("<div>").addClass("live-post-writer").text(post.writer);
            const content = $("<div>").addClass("live-post-content").text(post.content);
            const date = $("<div>").addClass("live-post-date").text(post.createdAt);
            card.append(writer, content, date);
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
    observer.observe(sentinel);
    loadPosts();
});