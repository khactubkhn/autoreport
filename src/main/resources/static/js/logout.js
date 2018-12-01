logout = function(){
	window.location.replace("/login");
	document.cookie = "authorization=";
	document.cookie = "fullname=";
}