var data = '';
var numberpage = 0;

//arr này sẽ chứa các trang được thể hiện trên thanh phân trang
var arr =[];

$(document).ready(function(){

	showUserFullName();
	loadData();
	
	$('#ip_search').keypress(function(){
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13'){
			search();        				
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
	$.ajax({
		url:'/api/admin/getall',
		type:'get',
		success: function(response){
			var code = response.code;
			if(code == 0){
				data = response.data;
				$('.tr').remove();
				$("#next").remove();
				for( var i=0; i<response.data.length; i++){
					var id = data[i].id;
					var ten = data[i].firstname + data[i].lastname;
					var sdt = data[i].phone;
					var username = data[i].username;
					
					
					$('#data_table').append(
						"<tr class='tr hiddenpage' id = 'tr"+ id +"'>"
						+ "<td>" + (i+1) +"</td>" 
						+"<td>"+ten+"</td>"
						+"<td>"+sdt+"</td>"
						+"<td>"+username+"</td>"
						+"<td><button type='button' class='btn btn-warning' data-toggle='modal' data-target='#formedit' onclick='showedit("+i+")'><i class='fa fa-pencil' aria-hidden='true'></i></button></td>"
						+"</tr>"
						);
				}
				
				loadpagination();
				loadDetailSachPage(10, 1);
				
			}else {
				console.log("Faild");
			}
		},
		error: function () {
			console.log("Server error");
		}
	});
}


// ---------------------------phan trang-----------------------------

// hiển thị những quyển sách theo từng trang
loadDetailSachPage = function(numberitem, pagenumber){
	
	for(var i=0;i< numberpage; i++){
		$("#li"+(i+1)).removeClass("active");
	}
	
	$("#li"+pagenumber).addClass("active");
	
	for(var j=0; j< $('.tr').length; j++){
// var id1 = data[j].id;
// $("#tr"+id1).addClass("hiddenpage");
$(".tr:nth-child("+(j+1)+")").addClass("hiddenpage");
}

var indexbegin = (pagenumber-1)*numberitem +1;

var indexend = pagenumber*numberitem;
if(indexend > $('.tr').length) indexend = data.length;
for(var i= indexbegin; i<= indexend ; i++){
	$(".tr:nth-child("+(i+1)+")").removeClass("hiddenpage");
}
}

// hiển thị thanh phân trang
loadpagination = function(){
	var leng = $(".tr").length;
	numberpage = Math.ceil(leng/10);// mỗi trang có 10 dòng
	$(".li_pag").remove();
	
	// show het toan bo cac trang
	for(var i=0;i< numberpage; i++){
		$("#phantrang").append("<li class='li_pag ' id='li"+(i+1)+"' ><a  class='hiddenpage' style='cursor:pointer;' id='pg"+(i+1)+"' onclick='loadDetailSachPage(10,"+(i+1)+")'>"+(i+1)+"</a></li>");
	}
	
	$("#phantrang").append("<li class='page-item' id='next'><a class='page-link ' style='cursor:pointer;' aria-label='Next' onclick='next()'><span aria-hidden='true'>&raquo;</span><span class='sr-only'>Next</span></a></li>");
	
	
	if(numberpage  >= 4){
		// khoi tao mang cac trang hien len dau tien
		arr = [1,2,3,4];
	}else if(numberpage == 3){
		arr = [1,2,3];
	}else if(numberpage == 2){
		arr = [1,2];
	} else{
		// khoi tao mang cac trang hien len dau tien
		arr = [1];
	}
	
	showpaginpage();
}


// hiển thị số những trang trong thanh phân trang
showpaginpage = function(){
	for(var i=0;i< arr.length; i++){
		$("#pg" + arr[i]).removeClass("hiddenpage");
	}
}

// set tất cả các số trang lúc đầu là không hiện lên
sethiddenpage = function(){
	for(var i=0; i<numberpage; i++){
		$("#pg"+(i+1)).addClass("hiddenpage");
	}
}

// hàm khi click vào nút previous
previous = function(){
	sethiddenpage();
	
	if(arr[0] > 1){
		for(var i=0; i< arr.length; i++){
			arr[i] = arr[i] - 1;
		}
	}
	showpaginpage();
}

// //hàm khi click vào nút next
next = function(){
	sethiddenpage();
	var leng = arr.length;
	
	if(arr[leng - 1] <  numberpage){
		for(var i=0; i< arr.length; i++){
			arr[i] = arr[i] +  1;
		}
	}
	showpaginpage();
}

// -----------------------------------end phan trag------------------------

showedit = function(index){
	var id = data[index].id;
	var firstname = data[index].firstname;
	var lastname = data[index].lastname;
	var username = data[index].username;
	var phone = data[index].phone;
	$("#ip_userid_edit").val(id);
	$("#lb_username_edit").text(username);
	$("#ip_firstname_edit").val(firstname);
	$("#ip_lastname_edit").val(lastname);
	$("#ip_phone_edit").val(phone);
}


search = function(){
	var key = change_alias($('#ip_search').val()).toLowerCase();
	$(".tr").remove();
	$("#next").remove();
	var j = 0;
	for(var i=0; i<data.length; i++){
		var keysearch = data[i].firstname + " " + data[i].lastname +" "+ data[i].username;
		var tensearch = change_alias(keysearch).toLowerCase();
		
		if(tensearch.indexOf(key) >= 0){
			j++;
			var id = data[i].id;
			var ten = data[i].firstname + data[i].lastname;
			var sdt = data[i].phone;
			var username = data[i].username;
			
			
			$('#data_table').append(
				"<tr class='tr hiddenpage' id = 'tr"+ id +"'>"
				+ "<td>" + (i+1) +"</td>" 
				+"<td>"+ten+"</td>"
				+"<td>"+sdt+"</td>"
				+"<td>"+username+"</td>"
				+"<td><button type='button' class='btn btn-warning' data-toggle='modal' data-target='#formedit' onclick='showedit("+i+")'><i class='fa fa-pencil' aria-hidden='true'></i></button></td>"
				+"</tr>"
				);
		}
	}
	loadpagination();
	loadDetailSachPage(10, 1);
}

function change_alias(alias) {
	var str = alias;
	str = str.toLowerCase();
	str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g,"a"); 
	str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g,"e"); 
	str = str.replace(/ì|í|ị|ỉ|ĩ/g,"i"); 
	str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g,"o"); 
	str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g,"u"); 
	str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g,"y"); 
	str = str.replace(/đ/g,"d");
	str = str.replace(/!|@|%|\^|\*|\(|\)|\+|\=|\<|\>|\?|\/|,|\.|\:|\;|\'|\"|\&|\#|\[|\]|~|\$|_|`|-|{|}|\||\\/g," ");
	str = str.replace(/ + /g," ");
	str = str.trim(); 
	return str;
}