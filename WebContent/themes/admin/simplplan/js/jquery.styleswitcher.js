/*
 * Simplpan Admin Panel
 *
 * Copyright (c) 2011 Kasper Kismul
 *
 * http://themeforest.net/user/Kaasper/profile
 *
 * This adds cookie for style and width aswell as change style based on user input.
 *
 */

//-------------------------------------------------------------- */
//
// 	**** Styleswitch Cookie (Cookie) **** 
//
// 	For more information go to:
//	https://github.com/carhartl/jquery-cookie
//
//-------------------------------------------------------------- */

// Checks if you've set a theme already
if($.cookie("theme")) {
	// Sets the theme that the cookie finds
	$("link.theme").attr("href",("./css/theme/" + $.cookie("theme") + ".css"));
};

// Checks if you've set a width already
if($.cookie("width")) {
	// Sets the width that the cookie finds
	$("link.width").attr("href",("./css/" + $.cookie("width") + ".css"));
};

//-------------------------------------------------------------- */
// Style Switcher
//-------------------------------------------------------------- */

$(document).ready(function() { 
	
//-------------------------------------------------------------- */
// Width Switcher
//-------------------------------------------------------------- */
	// Checks if you've set a width already
	if($.cookie("width")) {
		// Sets the width that the cookie finds
		$("link.width").attr("href",("http://ls.forestwebsolution.com/themes/admin/simplplan/css/" + $.cookie("width") + ".css"));
	};
	
	// Checks if you've set a width already
	if($.cookie("width")) {
		// Sets cookie as selected
		$("#width li").find('a[href$=' + $.cookie("width") + ']').addClass("selected");
	}
	else {
		$("#width li").find('a[href$=' + "fixed" + ']').addClass("selected");
	}

	// If you change width..
	$("#width li a").click(function() { 
		
		// Gets the with you've selected and adds the .css
		var $widthCss = "http://ls.forestwebsolution.com/themes/admin/simplplan/css/" + ($(this).attr('href').replace("#","")) + ".css";
		
		// Replaces the width's css
		$("link.width").attr("href", $widthCss);
		$('table.stats').trigger('visualizeRefresh');
		
		// Removes other selected classes and adds it to the selected width
		$(this).parent().parent().find("a").removeClass("selected");
		$(this).addClass("selected");
		
		// Sets cookie
		$.cookie("width",$(this).attr('href').replace("#",""), {expires: 365, path: '/'});
		return false; // Prevents going to top when pressing "#" link
	});	


// jQuery End	
});