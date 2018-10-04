$(document).ready(function() {
	$('.switcherRight').stop().animate({'marginRight':'-166px'},0);
	$('.open').click(function () {
		$('.switcherRight').click(
			function () {
				$('.panel',$(this)).stop().animate({'marginRight':'166px'},500);
                $('.toggle',$(this)).stop().animate({'marginRight':'166px'},500);
			}
		);
	});
	$('.close').click(function () {
		$('.switcherRight').click(
			function () {
				$('.panel',$(this)).stop().animate({'marginRight':'0px'},500);
                $('.toggle',$(this)).stop().animate({'marginRight':'0px'},500);
			}
		);
	});

	$(".toggleRight div").click(function () {
		$(".toggleRight div").toggle();
	});		
	
	var offset = $(".switcherRight").offset();
	var topPadding = 18;
	$(window).scroll(function() {
		if ($(window).scrollTop() > offset.top) {
			$(".switcherRight").stop().animate({
				marginTop: $(window).scrollTop() - offset.top + topPadding
			});
		} else {
			$(".switcherRight").stop().animate({
				marginTop: 0
			});
		};
	});
    
	
});