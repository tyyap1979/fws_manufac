jQuery(document).ready(function() {	

    carouFredSel();
    sidebarCarouFredSel();
	carouFredSelFilter();
    carouFredSelSitemap();
	
});


function carouFredSel() {
	$('.list_items').carouFredSel({
		circular: false,
		infinite: false,
		auto : false,
		pagination  : {
			container : '.pager',
			items : 3
		}
	});
}

function sidebarCarouFredSel() {
	$('.sidebar_list_items').carouFredSel({
		circular: false,
		infinite: false,
		auto : false,
		pagination  : {
			container : '.pager',
			items : 1
		}
	});
}

function carouFredSelSitemap() {
	$('.list_items-sitemap').carouFredSel({
		circular: false,
		infinite: false,
		auto : false,
		pagination  : {
			container : '.pager-sitemap',
			items : 3
		}
	});
}
	
function carouFredSelFilter() {
	
	$('.list_carousel').append('<ul class="list_archive" />');
	$('.list_archive').hide();
	
	$('.filter_navi li a').click(function() {
			
            
			var filter_navi = $(this).attr('rel');
			
            $('.filter_navi li').first().addClass('alpha');
			$('.filter_navi li a').removeClass('active');
			$(this).addClass('active');
			
			if($(this).attr('rel') == 'all') {
				$('.item').attr('rel', 'categ');
			}
			else {
				$('.item').each(function() {
					if($(this).hasClass(filter_navi)) {
						$(this).attr('rel', 'categ');
					}
					else {
						$(this).attr('rel', 'archive');
					}
				});
			}
			
            $('.list_items').fadeOut(300, function() {
    			$('.item[rel="categ"]').appendTo('.list_items');
    			$('.item[rel="archive"]').appendTo('.list_archive');
            $('.list_items').fadeIn(300);
            if($('.list_items')) carouFredSel();
            });
    		
	});
    
	$('.filter_navi li a').eq(0).click();	
	
}