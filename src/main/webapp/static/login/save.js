function checkExistID() {
	var id = $("#userId").val();
	
	if(id == "") {
		alert("아이디를 입력해주세요.");
		return;
	}
	
	$.ajax({
		type:'get',
		url : '/member/save/userId/' + id,
		success : function (result) {
			if(result == 'existID') {
				alert("중복있음")
			} else {
				alert("중복없음");
			}
		},
		error: function(err) {
			console.log(err);
		}
	});
}
