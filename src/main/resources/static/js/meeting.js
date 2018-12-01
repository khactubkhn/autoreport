$(document).ready(function(){
	showUserFullName();
	var roomID = GetURLParameter("roomID");
	getRoomInfor(roomID);
	getRoomContent();
	setPersmisson();
	var url = "/api/room/upload/" + roomID;
	$("#formupfile").attr("action",url);

// uploadfile -------------

$("#formupfile").submit(function(evt){	 
	evt.preventDefault();
	var formData = new FormData($(this)[0]);
	$.ajax({
		url: url,
		type: 'POST',
		data: formData,
		async: false,
		cache: false,
		contentType: false,
		enctype: 'multipart/form-data',
		processData: false,
		success: function (response) {
			
			$('#popUploadFile').modal('hide');
			var code = response.code;
			if(code == 0){
				var fileSaveId = parseInt(response.data.id);
				sendFile(fileSaveId);
			}
		}
	});
	return false;
});

// -----------


	// connet chat serve
	connect();
});




liSpeakerClick =  function(){

	$("#speaker li").click(function(){
		removeActive();
		$(this).addClass("active");
		var lstChildren = $(this).children();
		var speakerID = lstChildren[0].value;
		var fullName = lstChildren[1].value;
		$("#hidSpeakerID").val(speakerID);
		$("#hidFullName").val(fullName);
		
		$("#btn_start").css("display","inline");
		if($("#message").val().trim().length < 0){
			sendMessage();
		}

	});
}


// trang thai cuoc hop dang dien ra hay da ket thuc
var active = 0;

loadData = function (){
	loadData2Popup();
}
removeActive = function(){
	var lstLi = $("#speaker").children();
	for(var i=0; i< lstLi.length; i++){
		lstLi[i].classList.remove("active");
	}
}

showUserFullName = function(){
	var fullName = getCookiebyName("fullname");
	$("#spFullName").text(fullName);
}

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};

var colors = [
'#2196F3', '#32c787', '#00BCD4', '#ff5652',
'#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

var roleRooms = 
[
{value: "READ", name: "Đọc"},
{value: "WRITE", name: "Ghi"},
{value: "EXPORT", name: "Xuất file"},
{value: "ADD_MEMBER", name: "Thêm người"},
{value: "DELETE_MEMBER", name: "Xóa người dùng"}
];



getAvatarColor = function (fullName){
	var lengthFName = fullName.length;
	indexOfColor = lengthFName % 8;
	return colors[indexOfColor];
}

function getActualFullDate() {
	var d = new Date();
	var day = addZero(d.getDate());
	var month = addZero(d.getMonth()+1);
	var year = addZero(d.getFullYear());
	var h = addZero(d.getHours());
	var m = addZero(d.getMinutes());
	var s = addZero(d.getSeconds());
	return day + ". " + month + ". " + year + " (" + h + ":" + m + ":" + s +")";
}

function addZero(i) {
	if (i < 10) {
		i = "0" + i;
	}
	return i;
}

onclickDelelte = function(){
	alert("Bạn có chắc muốn xóa lời thoại này không");
}

onclickSave = function(){
	alert("Bạn có chắc muốn lưu lời thoại này không");
}


$("#message").keyup(function(event){
	if (event.keyCode == 13) {
		sendMessage();
	}
});

addPermissionSelectBox = function(value, name){

	var perOptionElement = document.createElement('option');
	perOptionElement.setAttribute("value",value);
	var perTextNode = document.createTextNode(name);
	perOptionElement.appendChild(perTextNode);
	var lstPer = document.querySelector('#selectreporterpermission');
	lstPer.appendChild(perOptionElement);
}

addReporterSelectBox = function(userid, firstname, lastname, username){
	var fullname = firstname+" "+lastname +" - " + username;
	var optionElement = document.createElement('option');
	optionElement.setAttribute("value",userid);
	var textNode = document.createTextNode(fullname);
	optionElement.appendChild(textNode);
	var lstReporter = document.querySelector('#selectreporter');
	lstReporter.appendChild(optionElement);
}


// load du lieu len popup them nguoi
loadData2Popup = function(){
	$('#selectreporterpermission').children().remove().end(); 
	$('#selectreporter').children().remove().end();
	for (var i = 0; i < roleRooms.length; i++) {
		addPermissionSelectBox(roleRooms[i].value, roleRooms[i].name);
	}
	$('#selectreporterpermission').trigger("chosen:updated");
	loadData2SelectReporter();
}

// loaddata vao selectbox danh sach reporter
loadData2SelectReporter = function(){
	var roomID = GetURLParameter("roomID");
	$.ajax({
		url:'/api/room/get-reporters',
		type:'get',
		data:{roomId:roomID},
		success: function(response){
			var code = response.code;
			var token = response.token;
			if(code == 0){
				var lstReporters = response.data;
				for (var i = 0; i < lstReporters.length; i++) {
					var rpt = lstReporters[i];
					addReporterSelectBox(rpt.userId,rpt.firstName, rpt.lastName,rpt.username);
					$('#selectreporter').trigger("chosen:updated");
				}
			}else {
				$("#status_login").text("Tên đăng nhập hoặc mật khẩu không chính xác");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}

CLick = function(){
	var selected = $("#selectreporterpermission").chosen().val();
	console.log(selected);
}

var listObject = [];



var lstReporters = [];
var indexOfReporter = 1; // chi so cac reporter da them vao
addReporter = function(){
	var usrID =  parseInt($("#selectreporter").chosen().val());
	lstReporters.push({userId: usrID, roles: $("#selectreporterpermission").chosen().val() });
	var idRp = "reporter_" + $("#selectreporter").chosen().val();
	var name = $("#selectreporter option:selected").text()
	$(".lst_reporter").append(
		"<span class='user_span' id='"+idRp +"'>"+name+
		"<a onclick='closeReporter("+usrID+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
		"</span>");
	indexOfReporter++;

}
lstSpeaker = [];
var indexOfSpeaker = 0;
addSpeaker = function(){
	var firstName = $("#firstName_Speaker").val();
	var lastName = $("#lastName_Speaker").val();

	lstSpeaker.push({index: indexOfSpeaker, firstName: firstName, lastName: lastName});

	var idSp = "speaker_" + indexOfSpeaker;
	var name = firstName +" " + lastName;
	$(".lstSpeaker").append(
		"<span class='user_span' id='"+idSp +"'>"+name+
		"<a onclick='closeSpeaker("+indexOfSpeaker+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
		"</span>");
	indexOfSpeaker++;
	console.log(lstSpeaker);
}

removeReporter = function(userID){
	for(var i=0; i<lstReporters.length; i++){
		var uID = lstReporters[i].userID;
		if(uID == userID){
			lstReporters.splice(i,1);
		}
	}
}

closeReporter = function(id){
	var reporterid = "#reporter_"+id;
	$(reporterid).css("display","none");
	console.log(reporterid);
	removeReporter(id);
}

closeSpeaker = function(id){
	var spID = "#speaker_"+id;
	$(spID).css("display","none");
	console.log(spID);
	removeSpeaker(id);
}

removeSpeaker = function(id){
	for(var i=0; i<lstSpeaker.length; i++){
		if(id == lstSpeaker[i].index){
			lstSpeaker.splice(i,1);
		}
	}
}

getRoomContent = function(){
	var roomid = GetURLParameter('roomID');
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
				console.log(listContent);
				for(var i = 0; i< listContent.length; i++){
					var content = listContent[i];
					var message = content.content;
					var speakerInfo = content.speaker;
					var firstname ="";
					var lastname = "";
					
					if(speakerInfo != null){
						firstname = content.speaker.firstName;
						lastname =  content.speaker.lastName;
					}
					var starttime ="";
					if(content.startTime != null){
						starttime = getTimeShow(new Date(content.startTime));
					}

					var endtime ="";
					if(content.endTime != null){
						endtime = getTimeShow(new Date(content.endTime));
					}
					var createdDTG = getTimeShow(new Date(content.created));
					var rpFirstName = content.reporter.firstName;
					var rpLastName = content.reporter.lastName;
					var rpUserName = content.reporter.username;
					reciveMessage(message,firstname,lastname,starttime,endtime,rpFirstName,rpLastName,rpUserName,createdDTG);
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

// ------------------------------------- get room transcript merge ---------------------------

loadDatToPopTranscriptMerge = function(){
	getRoomTranscript();
}


getRoomTranscript = function(){
	var roomid = GetURLParameter('roomID');
	var objectReq = {roomId : parseInt(roomid)};
	$.ajax({
		url:'/api/room/get-room-transcripts',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				var listTranscript = response.data;
				$('.lstMessageMerge').children().remove().end(); 
				for (var i = 0; i < listTranscript.length; i++) {
					var transcript = listTranscript[i];
					var edited = transcript.edited;
					var fullname = transcript.speaker.firstName +" " + transcript.speaker.lastName;
					var id = transcript.id;
					var starttime = getTimeShow(new Date(transcript.start));
					var endtime = getTimeShow(new Date(transcript.end));
					var time  = starttime +" - " + endtime;
					var content = transcript.content;
					var userupdate = transcript.editingByUser;
					var nameUserUpdate = "";
					if(userupdate != null){
						nameUserUpdate = userupdate.firstName +" " + userupdate.lastName;
					}

					addTranscript(content,fullname,time, id,nameUserUpdate);
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

addHistoryItem = function(time, content, updatedBy){
	var historyItem = document.createElement('div');
	historyItem.classList.add('historyitem');

	var contentHistory = document.createElement('div');
	contentHistory.classList.add('contentHistory');
	var time = document.createElement('span');
	var timeText = "("+ time + ")" ;
	var timeTextNode= document.createTextNode(timeText);
	time.appendChild(timeTextNode);
	contentHistory.appendChild(time);

	var userupdate = document.createElement('span');
	var text = "Updated by " + updatedBy;
	var userupdateTextNode= document.createTextNode(text);
	userupdate.appendChild(userupdateTextNode);
	userupdate.style['font-style'] = 'italic';
	contentHistory.appendChild(userupdate);

	var contentUpdate = document.createElement('p');
	var contentTextNode = document.createTextNode(content);
	contentUpdate.appendChild(contentTextNode);
	contentHistory.appendChild(contentUpdate);

	// -- button undo
	var undoElement = document.createElement('div');
	var buttonElement = document.createElement('button');
	buttonElement.setAttribute("type","button");
	buttonElement.style['border'] = 'none';
	buttonElement.style['background'] = 'none';
	var iconElement = document.createElement('i');
	iconElement.setAttribute('class','fa fa-reply');
	iconElement.setAttribute('aria-hidden','true');
	buttonElement.appendChild(iconElement);
	undoElement.appendChild(buttonElement);
	historyItem.appendChild(contentHistory);
	historyItem.appendChild(undoElement);
	var historyArea = document.querySelector('.listHistory');
	historyArea.appendChild(historyItem);
}

addTranscript = function(message, fullName, time, id, nameUserUpdate){
	
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
	var functionNameEdit = "loadDataToPopupShowEdit(" +id +",'"+ message +"')";
	console.log(functionNameEdit);
	textElement.setAttribute('onfocus',functionNameEdit);
	var functionNameRemoveEdit = "removeEditing("+id+")";
	// textElement.setAttribute('onblur',functionNameRemoveEdit);
	var spanNotifi = document.createElement('h6');
	var textnameuserupdate ="";
	if(nameUserUpdate.trim().length > 0){
		textnameuserupdate = nameUserUpdate +" is editting .....";
	}
	
	var spanNotifiText= document.createTextNode(textnameuserupdate);
	spanNotifi.appendChild(spanNotifiText);


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
	iconhistory.setAttribute("onclick","onclickShowPopup(" +id + ")");
	
	textElement.appendChild(messageText);
	textElement.setAttribute("class","form-control");
	textElement.style['display']='inline-block';
	textElement.style['width']='90%';
	messageElement.appendChild(textElement);
	messageElement.appendChild(spanNotifi);
	messageElement.appendChild(icondelete);
	messageElement.appendChild(iconsave);
	messageElement.appendChild(iconhistory);
	var messageArea = document.querySelector('.lstMessageMerge');
	messageArea.appendChild(messageElement);
	messageArea.scrollTop = messageArea.scrollHeight;
}

onclickShowPopup = function(id){
	$("#popHistory").modal('show');
	loadDataToPopupHistory(id);
}

loadDataToPopupShowEdit = function(id, message){
	$("#hidTranscriptID").val(id);
	$("#popShowEdit").modal('show');
	editingTranscript(id);
	$("#areTranscript").val(message);
}

removeEditingTranscript = function(){
	var id = parseInt($("#hidTranscriptID").val());
	removeEditing(id);

}

saveEditTranscript = function(){
	var transcriptId = parseInt($("#hidTranscriptID").val());
	var roomID = parseInt(GetURLParameter("roomID"));
	var content = $("#areTranscript").val();
	saveEditTranscripModifi(transcriptId,content,roomID);
	$("#popShowEdit").modal('hide');

}

loadDataToPopupHistory = function(id){
	var roomid = GetURLParameter('roomID');
	var transcriptId = parseInt(id);
	var objectReq = {roomId : parseInt(roomid), transcriptId: transcriptId };
	$.ajax({
		url:'/api/room/get-history-transcripts',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				var listTranscriptHistory = response.data;

				for (var i = 0; i < listTranscriptHistory.length; i++) {
					var item = listTranscriptHistory[i];
					var time = getTimeShow(new Date(item.updatedDTG));
					var content = item.content;
					var updateBy = item.updateBy.firstName + " " + item.updateBy.lastName;
					addHistoryItem(time, content,updateBy);
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

// --------------------------------------------------------

addListUser = function(){
	addListSpeaker();
}

addListReporter = function(){
	var roomID = parseInt(GetURLParameter("roomID"));
	var objectReq = {roomId: roomID, members:lstReporters};
	$.ajax({
		url:'/api/room/add-members',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			if(code == 0){
				console.log("Success");
				alert("Đã thêm người thành công");
				var url = "/meeting?roomID="+  GetURLParameter("roomID");
				window.location.replace(url);
			}else {
				console.log("Faild");
				alert("Đã xảy ra lỗi trong quá trình thêm reporter");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}

addListSpeaker = function(){
	var roomId = parseInt(GetURLParameter("roomID"));
	var listSpeakerAdd =[];
	for (var i = 0; i < lstSpeaker.length; i++) {
		var spk = lstSpeaker[i];
		listSpeakerAdd.push({firstName:spk.firstName, lastName: spk.lastName});
	}
	var objectReq = {roomId: roomId, speakers:listSpeakerAdd};
	$.ajax({
		url:'/api/room/add-speakers',
		type:'post',
		contentType:'application/json',
		dataType: 'json',
		data: JSON.stringify(objectReq),
		success: function(response){
			var code = response.code;
			var status = true;
			if(code == 0){
				console.log("Success");
				status =  true;
			}else {
				console.log("Faild");
				status =  false;
			}

			if (status == true) {
				addListReporter();
				
			}else{
				alert("Đã xảy ra lỗi trong quá trình thêm speaker");
			}

		},
		error: function () {
			console.log("Server error");
		}
	});
}
/**
 * Lay tham so truyen tren duong dan
 * @param sParam ten tham so (roomID)
 * @returns gia tri roomID
 */

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

// danh sach cac id cua user bi remove
lstUserIDRemoved= [];

 // remove user khoi room
 removeUserFromRoom = function(userID){
 	var idRemoved = "#userremoved_"+userID;
 	$(idRemoved).css("display","none");
 	lstUserIDRemoved.push(userID);
 }

 addUserRemoved = function(userID, firstname, lastname, username){

 	var idRp = "userremoved_" + userID;
 	var name = firstname +" "+  lastname +" - " + username;

 	$("#lstUserExist").append(
 		"<span class='user_span' id='"+idRp +"'>"+name+
 		"<a onclick='removeUserFromRoom("+userID+")'><i class='fa fa-times' aria-hidden='true' ></i></a>"+
 		"</span>");

 }

 loadData2PopupRemove= function(){
 	var roomID = parseInt(GetURLParameter("roomID"));
 	var url = "/api/room/"+roomID;
 	$.ajax({
 		url:url,
 		type:'get',
 		contentType:'application/json',
 		dataType: 'json',
 		success: function(response){
 			var code = response.code;
 			if(code == 0){
 				console.log("Success");

 				loadData2LstUserRemove(response.data.members);
 			}else {
 				console.log("Faild");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});
 }

 loadData2LstUserRemove = function(lstuser){
 	var userName = getCookiebyName("username");
 	$('#lstUserExist').children().remove().end(); 
 	for(var i = 0; i<lstuser.length; i++){
 		var member = lstuser[i];
 		if (userName == member.username) {
 			continue;
 		}
 		addUserRemoved(member.userId, member.firstName, member.lastName, member.username);
 	}
 }

 RemoveUsers = function(){
 	var roomID = parseInt(GetURLParameter("roomID"));
 	if(lstUserIDRemoved.length > 0){
 		var dataRq = {roomId:roomID,members:lstUserIDRemoved};
 		$.ajax({
 			url:'/api/room/remove-members',
 			type:'post',
 			contentType:'application/json',
 			dataType: 'json',
 			data: JSON.stringify(dataRq),
 			success: function(response){
 				var code = response.code;
 				if(code == 0){
 					console.log("Success");
 					window.location.replace('/meeting?roomID='+roomID);
 				}else {
 					console.log("Faild");
 				}
 			},
 			error: function () {
 				console.log("Server error");
 			}
 		});
 	}else{
 		alert("Chưa chọn người dùng để xóa");
 	}
 	
 }

 // Share ma code
 addPermissionShare = function(value, name){
 	var perOptionElement = document.createElement('option');
 	perOptionElement.setAttribute("value",value);
 	var perTextNode = document.createTextNode(name);
 	perOptionElement.appendChild(perTextNode);
 	var lstPer = document.querySelector('#permissionShare');
 	lstPer.appendChild(perOptionElement);
 }

 loadPermission2PopupShareCode = function(){
 	for (var i = 0; i < roleRooms.length; i++) {
 		addPermissionShare(roleRooms[i].value, roleRooms[i].name);
 	}
 	$('#permissionShare').trigger("chosen:updated");
 }

 createCode = function(){
 	var roomID =  parseInt(GetURLParameter("roomID"));
 	var roles = $("#permissionShare").chosen().val();
 	var objectReq = {roomId: roomID, roles: roles};
 	console.log(objectReq);
 	// var rolesText = $("#permissionShare option:selected").text().join(', ');

 	var rolesText = [];
 	$("#permissionShare option:selected").each(function () {
 		rolesText.push(this.text);
 	});
 	rolesText.join(',');
 	$.ajax({
 		url:'/api/room/createCode',
 		type:'post',
 		contentType:'application/json',
 		dataType: 'json',
 		data: JSON.stringify(objectReq),
 		success: function(response){
 			
 			if(response.code == 0){
 				console.log("Success");
 				var code = response.data.code;
 				addRowShareCode(rolesText,code);
 			}else {
 				console.log("Faild");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});

 }

 addRowShareCode = function(lstRoles, code){
 	$("#tblstCodes").append(
 		"<tr>"+
 		"<td>" + lstRoles +"</td> <td>"+code+"</td>"+
 		"</tr>"
 		);
 }

 // lay thong tin cua phong vua tao

 var roomInfor ={};
 getRoomInfor = function(roomID){
 	var url = "/api/room/"+roomID;
 	$.ajax({
 		url:url,
 		type:'get',
 		success: function(response){
 			var code = response.code;
 			var token = response.token;
 			if(code == 0){
 				roomInfor = response.data;
 				$("#meeting_name").text(roomInfor.name);
 				var meetingStartTime = getTimeShow(new Date(roomInfor.createdDTG));
 				$("#meeting_time").text(meetingStartTime);
 				var lstSpeaker = response.data.speakers; // lay danh sach speaker trong room
 				for(var i= 0; i< lstSpeaker.length; i++){
 					var item = lstSpeaker[i];
 					appendSpeakerToList(item.firstName, item.lastName, item.id);
 				}
 				var lstReporter = response.data.members; // lay danh sach reporter trong room
 				for(var i=0; i< lstReporter.length; i++){
 					var item = lstReporter[i];
 					appendReporterToList(item.firstName, item.lastName, item.username);
 				}

 				var own = response.data.own.username;
 				var usrname_login = getCookiebyName("username");
 				if(own != usrname_login){
 					$("#btn_finish_room").css("display","none");
 				}else{
 					var lstRolesName = [];
 					for (var j = 0; j < roleRooms.length; j++) {
 						lstRolesName.push(roleRooms[j].value);
 					}
 					document.cookie = "roles="+lstRolesName;
 				}

 				liSpeakerClick();
 			}else {
 				console.log("Error trong get thong tin room theo roomid");
 			}
 		},
 		error: function () {
 			console.log("Server error");
 		}
 	});
 }

 getTimeShow = function(time){
 	if(time != null){
 		var day = addZero(time.getDate());
 		var month = addZero(time.getMonth()+1);
 		var year = addZero(time.getFullYear());
 		var h = addZero(time.getHours());
 		var m = addZero(time.getMinutes());
 		var s = addZero(time.getSeconds());
 		return day + ". " + month + ". " + year + " (" + h + ":" + m + ":" + s +")";
 	}else{
 		return "";
 	}
 }


 // kết thúc cuộc họp
 finishMeeting = function(){
 	var roomID = parseInt(GetURLParameter("roomID"));
 	var objectReq = {roomId: roomID};
 	$.ajax({
 		url:'/api/room/finish-room',
 		type:'post',
 		contentType:'application/json',
 		dataType: 'json',
 		data: JSON.stringify(objectReq),
 		success: function(response){
 			var code = response.code;
 			if(code == 0){
 				console.log("Success");
 				var url = "/meetingdetail?roomid="+roomID;
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

 // ------------------------------------------- SEND MESSAGE SOCKET -----------------------------------------------------

 var stompClient = null; 

 // connect to socket
 function connect() {

 	var socket = new SockJS('/ws');
 	stompClient = Stomp.over(socket);
 	stompClient.connect({}, onConnected, onError);
 }


 function onConnected() {
    // Subscribe to the Public Topic
    var urlSocket = "/topic/"+GetURLParameter("roomID");
    stompClient.subscribe(urlSocket, onMessageReceived);

    // Tell your username to the server
    var authorization = getCookiebyName("authorization");
    stompClient.send("/app/chat.addUser",
    	{},
    	JSON.stringify({data: {"authorization": authorization}, type: 'JOIN'})
    	)
}

function onError(error) {
	alert("Không thể kết nối với server, vui lòng refresh trang để thử lại");
}

// thoi gian bat dau va ket thuc loi noi cua mot nguoi
var startTimeSpeaker = 0;
var endTimeSpeaker = 0; 

showEndTime = function(){
	startTimeSpeaker = new Date().getTime();
	$("#btn_start").css("display","none");
	$("#btn_end").css("display","inline");

}

showStartTime = function(){
	endTimeSpeaker = new Date().getTime();
	$("#btn_end").css("display","none");
	$("#message").removeAttr('disabled');
}

function sendMessage() {
	$("#btn_start").css("display","inline");
	$("#message").attr("disabled", true)
	var speakerID = parseInt($("#hidSpeakerID").val());
	var roomID = parseInt(GetURLParameter("roomID"));
	var content = $("#message").val().trim();
	$("#message").val("");
	if(content && stompClient) {
		var chatMessage = {
			data: {
				roomId: roomID,
				speakerId: speakerID,
				content: content,
				startTime: startTimeSpeaker,
				endTime: endTimeSpeaker
			},
			type: 'CHAT'
		};

		stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
	}
}

function sendFile(fileSaveId) {
	
	var roomID = parseInt(GetURLParameter("roomID"));
	
	if(roomID && stompClient) {
		var chatMessage = {
			data: {
				roomId: roomID,
				fileSaveId: fileSaveId
			},
			type: 'ADD_FILE'
		};

		stompClient.send("/app/chat.sendFile", {}, JSON.stringify(chatMessage));
	}
}

var id_transcript_focus = '';
function editingTranscript(id) {
	
	
	var roomID = parseInt(GetURLParameter("roomID"));
	var transcriptId = parseInt(id);
	id_transcript_focus = transcriptId;
	
	if(transcriptId && stompClient) {
		var chatMessage = {
			data: {
				roomId: roomID,
				transcriptId: transcriptId
			},
			type: 'EDITING'
		};

		stompClient.send("/app/chat.notify", {}, JSON.stringify(chatMessage));
	}
}

function removeEditing(id) {
	
	
	var roomID = parseInt(GetURLParameter("roomID"));
	var transcriptId = parseInt(id);
	
	if(transcriptId && stompClient) {
		var chatMessage = {
			data: {
				roomId: roomID,
				transcriptId: transcriptId
			},
			type: 'REMOVE_EDITING'
		};

		stompClient.send("/app/chat.notify", {}, JSON.stringify(chatMessage));
	}
}

function saveEditTranscripModifi(transcriptId,content,roomId) {
	
	if(transcriptId && stompClient) {
		var chatMessage = {
			data: {
				roomId: roomId,
				transcriptId: transcriptId,
				content:content
			},
			type: 'EDIT'
		};

		stompClient.send("/app/chat.notify", {}, JSON.stringify(chatMessage));
	}
}

function onMessageReceived(payload) {
	console.log(payload);
	var message = JSON.parse(payload.body);
	if (message.type === 'CHAT') {
		var reporter = message.data.reporter;
		var speaker = message.data.speaker;
		var firstname_spk = speaker.firstName;
		var lastname_spk = speaker.lastName;
		var content = message.data.content;
		var starttime = getTimeShow(new Date (message.data.startTime));
		var endtime = getTimeShow(new Date (message.data.endTime));
		var rpFirstName = reporter.firstName;
		var rpLastName = reporter.lastName;
		var rpUserName = reporter.userName;
		var createdDTG = "";
		reciveMessage(content,firstname_spk, lastname_spk, starttime, endtime,rpFirstName,rpLastName,rpUserName,createdDTG);  
	}else if(message.type === 'ADD_FILE'){
		var content1 = message.data.newFile.name;
		var createdDTG = parseInt(message.data.newFile.CreatedDTG);
		var rpFirstName = message.data.user.firstName;
		var rpLastName = message.data.user.lastName;
		var rpUserName = message.data.user.username;
		reciveMessage(content1,"", "", "", "",rpFirstName,rpLastName,rpUserName,createdDTG);  
	}else if(message.type ==='PULL_TRANSCRIPT'){
		getRoomTranscript();

	}
}

reciveMessage = function(message, firstname, lastname, starttime, endtime, rpFirstName, rpLastName, rpUserName,createdDTG){
	
	if (message.trim().length > 0) {
		var fullName = firstname +" " + lastname;
		var lstFullNameSplit = [];
		if (fullName.trim().length > 0) {
			lstFullNameSplit = fullName.split(" ");
			var nameDisplay = "";
			for(var i=0; i< lstFullNameSplit.length -1 ; i++){
				nameDisplay += lstFullNameSplit[i].charAt(0);
			}
			nameDisplay+="."+lstFullNameSplit[lstFullNameSplit.length - 1];
		}else{
			nameDisplay = "Upload file";
		}
		var currentDate = '';
		if(endtime.length > 0){
			currentDate = starttime +" - "+ endtime;
		}else{
			currentDate = createdDTG;
		}
		
		var messageElement = document.createElement('li');
		messageElement.classList.add('message');
		var avatarElement = document.createElement('i');
		var avatarText = '';
		if (lstFullNameSplit.length > 0) {
			avatarText = document.createTextNode(lstFullNameSplit[lstFullNameSplit.length - 1][0]);
		}else{
			avatarText = document.createTextNode("UF");
		}
		
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

		var reporterElement = document.createElement('span');
		reporterElement.style['margin-left'] ='20px';
		reporterElement.style['font-weight'] ='700';
		var nameRpt = "[" + rpFirstName +" "+ rpLastName +" - " + rpUserName +"]";
		var reporterText = document.createTextNode(nameRpt);
		reporterElement.appendChild(reporterText);


		messageElement.appendChild(timeElement);
		messageElement.appendChild(reporterElement);
		var textElement = document.createElement('p');
		var messageText = document.createTextNode(message);
		textElement.appendChild(messageText);
		messageElement.appendChild(textElement);
		var messageArea = document.querySelector('.lstMessage');
		messageArea.appendChild(messageElement);
		messageArea.scrollTop = messageArea.scrollHeight;

	}
}
//----------------------------------- Phan quyen --------------------------------------------
setPersmisson = function(){
	$("#addUser").addClass("displayhidden");
	$("#addUser").addClass("per_ADD_MEMBER");

	$("#btn_share_code").addClass("displayhidden");
	$("#btn_share_code").addClass("per_ADD_MEMBER");
	
	$("#removeUser").addClass("displayhidden");
	$("#removeUser").addClass("per_DELETE_MEMBER");
	$("#message").addClass("displayhidden");
	$("#message").addClass("per_WRITE");
	$("#btn_send").addClass("displayhidden");
	$("#btn_send").addClass("per_WRITE");

}

// hien thi danh sach cac speaker trong cuoc hop ỏ thanh bên phải	
appendSpeakerToList  = function(firstname, lastname, speakerid){
	var speakerElement = document.createElement("li");
	speakerElement.classList.add("user");
	var spIDElement = document.createElement("input");
	spIDElement.setAttribute("type","hidden");
	spIDElement.setAttribute("value",speakerid);
	speakerElement.appendChild(spIDElement);
	var spNameElement = document.createElement("input");
	spNameElement.setAttribute("type","hidden");
	spNameElement.setAttribute("value",firstname +" " + lastname);
	speakerElement.appendChild(spNameElement);
	var avatarElementA = document.createElement("a");
//	avatarElementA.setAttribute("onclick","liSpeakerClick("+firstname+" " +lastname+"," +speakerid +")");
avatarElementA.setAttribute("href","#");
var avatarElement = document.createElement("i");
avatarElement.style['background-color'] = getAvatarColor(firstname +" "+lastname);
var avatarText  = document.createTextNode(lastname[0]);
avatarElement.appendChild(avatarText);
avatarElementA.appendChild(avatarElement);
speakerElement.appendChild(avatarElementA);
var nameDisplay =document.createElement("span");
nameDisplay.classList.add("style_name");
var nameText = document.createTextNode(firstname +" " + lastname);
nameDisplay.appendChild(nameText);
speakerElement.appendChild(nameDisplay);
var ulSpeaker = document.querySelector('#speaker');
ulSpeaker.appendChild(speakerElement);
speakerElement.scrollTop = speakerElement.scrollHeight;

}

appendReporterToList = function(firstname, lastname, username){
	var reporterElement = document.createElement("li");
	reporterElement.classList.add("user");
	var avatarElementA = document.createElement("a");
	avatarElementA.setAttribute("href","#");
	var avatarElement = document.createElement("i");
	avatarElement.style['background-color'] = getAvatarColor(firstname +" "+lastname);
	var avatarText  = document.createTextNode(lastname[0]);
	avatarElement.appendChild(avatarText);
	avatarElementA.appendChild(avatarElement);
	reporterElement.appendChild(avatarElementA);
	var nameDisplay =document.createElement("span");
	nameDisplay.classList.add("style_name");
	var nameText = document.createTextNode(firstname +" " + lastname +" - "+ username);
	nameDisplay.appendChild(nameText);
	reporterElement.appendChild(nameDisplay);
	var ulReporter = document.querySelector('#users');
	ulReporter.appendChild(reporterElement);
	reporterElement.scrollTop = reporterElement.scrollHeight;
}

gotoHome = function(){
	window.location.replace("/default");
}