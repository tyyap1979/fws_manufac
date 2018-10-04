$(document).ready(function() {


$('select#layout').change(function () { 
	var b = $(this).children(":selected").val();
	if (b == 'streched') {
		window.location.href = "index.html";
	}	
	else if (b == 'boxed') {
		window.location.href = "index-boxed.html";
	}
});	


$('select#skin').change(function () { 
	var b = $(this).children(":selected").val();
	if (b == 'light') {
		window.location.href = "index-boxed.html";
	}	
	else if (b == 'light') {
		window.location.href = "index-boxed.html";
	}	
	else if (b == 'dark') {
		window.location.href = "../dark/index-boxed.html";
	}
});	


 $("select#colors option").click(function(){
  var color = $(this).attr('value');
  $("#css_color_style").remove();
  $("head").append("<link>");
  css = $("head").children(":last");
  css.attr({
    rel:  "stylesheet",
    type: "text/css",
    id: "css_color_style",
    href: "css/color/" + color + ".css"
  });
 })

 $("#panel a.open").click(function(){
  var margin_left = $("#panel").css("margin-left");
  if (margin_left == "-210px"){
  $("#panel").animate({marginLeft: "0px"});
 }
 else{
  $("#panel").animate({marginLeft: "-210px"});
 }

 })

}); 