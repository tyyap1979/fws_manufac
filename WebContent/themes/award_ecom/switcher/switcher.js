$(document).ready(function() {
	$('.switcher').stop().animate({'marginLeft':'-166px'},0);
	$('.open').click(function () {
		$('.switcher').click(
			function () {
				$('.panel',$(this)).stop().animate({'marginLeft':'166px'},500);
                $('.toggle',$(this)).stop().animate({'marginLeft':'166px'},500);
			}
		);
	});
	$('.close').click(function () {
		$('.switcher').click(
			function () {
				$('.panel',$(this)).stop().animate({'marginLeft':'0px'},500);
                $('.toggle',$(this)).stop().animate({'marginLeft':'0px'},500);
			}
		);
	});

	$(".toggleLeft div").click(function () {
		$(".toggleLeft div").toggle();
	});		
	
	var offset = $(".switcher").offset();
	var topPadding = 18;
	$(window).scroll(function() {
		if ($(window).scrollTop() > offset.top) {
			$(".switcher").stop().animate({
				marginTop: $(window).scrollTop() - offset.top + topPadding
			});
		} else {
			$(".switcher").stop().animate({
				marginTop: 0
			});
		};
	});
    
	
});