
var listRoom= [];

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

$(document).ready(function(){

	showUserFullName();
	$.ajax({
		url:'/api/room/joined',
		type:'get',
		success: function(response){
			var code = response.code;
			console.log(response);
			if(code == 0){
				listRoom = response.data;
				listRoom.sort(function(a,b){
					return - (a["active"] - b["active"]); 
				});
				console.log("Success");
				loadData();
			}else {
				console.log("Faild");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
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
	$('#listRoom').children().remove().end();  
	// bind data to listmeetingroom
	for (var i = 0; i < listRoom.length; i++) {
		var id = listRoom[i].id;
		var number = listRoom[i].number;
		var time = getTimeShow(new Date(listRoom[i].createdDTG)) +" - " + getTimeShow(new Date (listRoom[i].updatedDTG));
		var name = listRoom[i].name;
		var roles = listRoom[i].roles;
		var lstRolesName = [];
		var active = listRoom[i].active;
		for (var j = 0; j < roles.length; j++) {
			lstRolesName.push(roles[j].name);
		}
		addMettingRoom(id,number,time,name, JSON.stringify(lstRolesName),active);
	}
}

getTimeShow = function(time){

	var day = addZero(time.getDate());
	var month = addZero(time.getMonth()+1);
	var year = addZero(time.getFullYear());
	var h = addZero(time.getHours());
	var m = addZero(time.getMinutes());
	var s = addZero(time.getSeconds());
	return day + ". " + month + ". " + year + " (" + h + ":" + m + ":" + s +")";
}

function addZero(i) {
	if (i < 10) {
		i = "0" + i;
	}
	return i;
}
searchRoom= function(){
	var key = $("#txtSearch").val();
	for (var i = 0; i < listRoom.length; i++) {
		var idMeetingRoomLi = "li_mr_" + listRoom[i].id;
		$("#"+idMeetingRoomLi).css("display","list-item");
		if (listRoom[i].name.includes(key) == false) {
			$("#"+idMeetingRoomLi).css("display","none");
		}
	}
}

addMettingRoom = function(idroom, numberOfUser, time, name, roles, active){

	var roomElement = document.createElement('li');
	if (active == 1) {
		roomElement.style['background-color'] = 'cornflowerblue';
		roomElement.style['color'] = 'white';
	}
	roomElement.setAttribute("onclick","navigateToDetail("+ active+"," +idroom+","+roles+")");
	var idMeetingRoomLi = "li_mr_" + idroom;
	roomElement.setAttribute("id",idMeetingRoomLi);
	roomElement.classList.add('meetingroom');
	var avatarElement = document.createElement('i');
	var avatarText = document.createTextNode(numberOfUser);
	avatarElement.appendChild(avatarText);
	avatarElement.style['background-color'] = getAvatarColor(numberOfUser);
	roomElement.appendChild(avatarElement);
	var timeElement = document.createElement('span');
	timeElement.classList.add('style_time');
	var timeText= document.createTextNode(time);
	timeElement.appendChild(timeText);
	roomElement.appendChild(timeElement);
	var meetingNameElement = document.createElement('p');
	var meetingNameText = document.createTextNode(name);
	meetingNameElement.appendChild(meetingNameText);
	roomElement.appendChild(meetingNameElement);
	var lstRoomArea = document.querySelector('#listRoom');
	lstRoomArea.appendChild(roomElement);
}


getAvatarColor = function (numberOfUser){
	indexOfColor = numberOfUser % 8;
	return colors[indexOfColor];
}


closeReporter = function(id){
	var reporterid = "#reporter_"+id;
	$(reporterid).css("display","none");
	console.log(reporterid);
	
}


navigateToDetail = function(active,idroom, roles){	
	var url = ""; 
	if(active == 1){
		url="/meeting?roomID="+idroom;
	}else{
		url ="/meetingdetail?roomid="+idroom;
	}
	document.cookie = "roles="+roles;
	window.location.replace(url);
}

// tao mot cuoc hop moi
addRoom = function(){
	var speakers = [];
	var maxuser = parseInt($('#txtMaxUser').val());
	var objectReq = {name: $('#txtMeetingName').val(), description: $('#txtMeetingDescription').val(), maxUser: maxuser , speakers: speakers }
	$.ajax({
		url:'/api/room/create',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 1){
				alert("Không được để trống tên, mô tả và số người sử dụng tối đa");
			}else{
				alert("Tạo mới cuộc họp thành công");
				var url = "/meeting?roomID="+ response.data.id;
				window.location.replace(url);
			}
			
		},
		error: function () {
			console.log("Server error");
		}
	});
}

joinMeeting = function(){
	var code = $("#txtCode").val();
	var objectReq = {code: code};
	var status = true;
	if(code.trim().length == 0) {
		status = false;
		alert("Bạn chưa nhập mã code");
	}

	if(status){
		$.ajax({
			url:'/api/room/joinByCode',
			type:'post',
			contentType:'application/json',
			dataType: 'json',
			data: JSON.stringify(objectReq),
			success: function(response){
				var code = response.code;
				if(code == 0){
					console.log("Success");
					var roomID = response.data.id;
					var url = "meeting?roomID="+roomID;
					window.location.replace(url);
					
				}else {
					console.log("Faild");
				}
			},
			error: function () {
				console.log("Server error");
			}
		});
	}
}