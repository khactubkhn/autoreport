var listUser= []; // reporter
var listSpeaker = [];
$(document).ready(function(){
	showUserFullName();
	var roomid = GetURLParameter('roomid');
	var url = "/api/room/"+parseInt(roomid);
	console.log("roomid: "+roomid);
	console.log("url: "+ url);
	$.ajax({
		url:url,
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 0){
				listUser = response.data.members;
				listSpeaker = response.data.speakers;
				$('#meeting_name').text(response.data.name);
				var time = new Date(response.data.createdDTG);
				$('#meeting_time').text(getTimeShow(time));
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

	setPermission();
	// hien thi thon tin chat
	getRoomContent();
});

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


showUserFullName = function(){
	var fullName = getCookiebyName("fullname");
	$("#spFullName").text(fullName);
}

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};

loadData = function(){
	// load data to selectbox
	for (var i = 0; i < listSpeaker.length; i++) {
		var spk = listSpeaker[i];
		var firstName = spk.firstName;
		var lastName = spk.lastName;
		addSpeakerToSelectBox(firstName,lastName);
	}

	// Hien thi danh sach reporter
	for (var i = 0; i < listUser.length; i++) {
		var reporter = listUser[i];
		var firstName = reporter.firstName;
		var lastName = reporter.lastName;
		var username = reporter.username;
		addReporterMember(firstName,lastName,username);
	}
}



addReporterMember = function(firstName, lastName, username){
	var nameShow = firstName+" "+ lastName + " - "+ username;
	var userSpanElement = document.createElement('span');
	userSpanElement.classList.add('user_span');
	var userTextNode = document.createTextNode(nameShow);
	userSpanElement.appendChild(userTextNode);
	var lstUserArea = document.querySelector('.lst_user');
	lstUserArea.appendChild(userSpanElement);
}

addSpeakerToSelectBox = function(firstName, lastName){
	var nameShow = firstName+" "+ lastName;
	var userOptionElement = document.createElement('option');
	var userTextNode = document.createTextNode(nameShow);
	userOptionElement.appendChild(userTextNode);
	var lstUserSelect = document.querySelector('#selectuser');
	lstUserSelect.appendChild(userOptionElement);
}

function GetURLParameter(sParam) {
	var sPageURL = window.location.search.substring(1);
	var sURLVariables = sPageURL.split('&');
	for (var i = 0; i < sURLVariables.length; i++){
		var sParameterName = sURLVariables[i].split('=');
		if (sParameterName[0] == sParam)
		{
			return sParameterName[1];
		}
	}
}

addMessage = function(message, fullName, time){
	
	var lstFullNameSplit = fullName.split(" ");
	var nameDisplay = "";
	var lstMessage = $(".lstMessage");
	for(var i=0; i< lstFullNameSplit.length -1 ; i++){
		nameDisplay += lstFullNameSplit[i].charAt(0);
	}
	nameDisplay+="."+lstFullNameSplit[lstFullNameSplit.length - 1];
	var currentDate = time;
	var messageElement = document.createElement('li');
	messageElement.classList.add('message');
	var avatarElement = document.createElement('i');
	var avatarText = document.createTextNode(lstFullNameSplit[lstFullNameSplit.length - 1][0]);
	avatarElement.appendChild(avatarText);
	avatarElement.style['background-color'] = getAvatarColor(fullName);
	messageElement.appendChild(avatarElement);
	var usernameElement = document.createElement('span');
	var usernameText = document.createTextNode(nameDisplay);
	usernameElement.appendChild(usernameText);
	usernameElement.style['font-weight'] = 'bold';
	messageElement.appendChild(usernameElement);
	var timeElement= document.createElement('span');
	var timeText = document.createTextNode(currentDate);
	timeElement.appendChild(timeText);
	timeElement.style['margin-left'] ='25px';
	timeElement.style['font-style'] ='italic';
	messageElement.appendChild(timeElement);
	var textElement = document.createElement('textarea');
	var messageText = document.createTextNode(message);
	var icondelete = document.createElement('i');
	var iconsave = document.createElement('i');
	var iconhistory = document.createElement('i');
	icondelete.setAttribute("class","fa fa-trash-o");
	icondelete.style['color']='red';
	icondelete.style['left']='unset';
	icondelete.style['cursor']='-webkit-grab';
	icondelete.setAttribute("aria-hidden","true");
	icondelete.setAttribute("onclick","onclickDelelte()");
	
	iconsave.setAttribute("class","fa fa-floppy-o");
	iconsave.style['color']='#4cae4c';
	iconsave.style['left']='unset';
	iconsave.style['cursor']='-webkit-grab';
	iconsave.style['margin-left']='20px';
	iconsave.setAttribute("aria-hidden","true");
	iconsave.setAttribute("onclick","onclickSave()");
	
	iconhistory.setAttribute("class","fa fa-history");
	iconhistory.style['color']='black';
	iconhistory.style['left']='unset';
	iconhistory.style['cursor']='-webkit-grab';
	iconhistory.style['margin-left']='42px';
	iconhistory.setAttribute("aria-hidden","true");
	iconhistory.setAttribute("onclick","onclickShowPopup()");
	
	textElement.appendChild(messageText);
	textElement.setAttribute("class","form-control");
	textElement.style['display']='inline-block';
	textElement.style['width']='90%';
	messageElement.appendChild(textElement);
	messageElement.appendChild(icondelete);
	messageElement.appendChild(iconsave);
	messageElement.appendChild(iconhistory);
	var messageArea = document.querySelector('.lstMessage');
	messageArea.appendChild(messageElement);
	messageArea.scrollTop = messageArea.scrollHeight;
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


getRoomContent = function(){
	var roomid = GetURLParameter('roomid');
	var objectReq = {roomId : parseInt(roomid)};
	$.ajax({
		url:'/api/room/get-room-content',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				var listContent = response.data;
				for(var i = 0; i< listContent.length; i++){
					var content = listContent[i];
					var message = content.content;
					var fullname = content.speaker.firstName + content.speaker.lastName;
					var time = getTimeShow(new Date(content.startTime))  + " - " + getTimeShow(new Date(content.endTime)) ;
					addMessage(message,fullname,time);
				}

			}else {
				console.log("Faild");
				return false;
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}

onclickShowPopup = function(){
	$("#popHistory").modal('show');
}

backToHome = function(){
	window.location.replace("/default");
}

// ---------------------------------------------- Phan quyen ----------------------------------
setPermission = function(){
	$("#export").addClass("displayhidden");
	$("#export").addClass("per_EXPORT");
}
