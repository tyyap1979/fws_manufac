    $(document).ready(function($){
    	$('#mega-menu').dcMegaMenu({
    		rowItems: '4',
    		speed: 'slow',
    		effect: 'fade',
    		fullWidth: true,
    	});
        
        	// Fluid Menu
    	if ($(".mega-menu").length) {
    		$('.mega-menu li').click(function(){
    			var url = $(this).find('a').attr('href');
    			document.location.href = url;
    		});
    		$('.mega-menu li').hover(function(){
    			$(this).find('.hover').slideDown();
    		},
    		function(){
    			$(this).find('.hover').slideUp();
    		});
    	}
        
    });