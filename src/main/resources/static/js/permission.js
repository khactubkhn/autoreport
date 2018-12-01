var roles = [];

getCookiebyName = function(name){
	var pair = document.cookie.match(new RegExp(name + '=([^;]+)'));
	return !!pair ? pair[1] : null;
};

$(document).ready(function(){
	roles = getCookiebyName("roles").split(',');
	for(var i = 0; i< roles.length; i++){
		var classname = "per_"+roles[i];
		$("."+classname).removeClass("displayhidden");
	}
});