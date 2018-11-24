$(document).ready(function(){
	showUserFullName();
	loadData();
	
});


showUserFullName = function(){
	var fullName = getCookiebyName("fullname");
	$("#spFullName").text(fullName);
}

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};

loadData = function(){
	$.ajax({
		url:'/api/getinformation',
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 0){
				var data = response.data;
				$("#hid_id").val(data.id);
				$("#hoten").text(data.firstname +" " + data.lastname);
				$("#username").text(data.username);
				$("#ip_sdt").val(data.phone);
				$("#avatar_name").text(data.username);
				$("#avatar_text").text(data.lastname[0]);
				var color = getAvatarColor(data.firstname +" " + data.lastname);
				$("#avatar_text").css("background-color",color);
				
			}else {
				console.log("Faild");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}

getAvatarColor = function (fullName){
	var lengthFName = fullName.length;
	indexOfColor = lengthFName % 8;
	return colors[indexOfColor];
}

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];