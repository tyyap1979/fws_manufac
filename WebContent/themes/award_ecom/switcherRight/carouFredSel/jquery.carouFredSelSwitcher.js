jQuery(document).ready(function() {	

    carouFredSelSwitcher1();
    carouFredSelSwitcher2();
    carouFredSelSwitcher3();
    carouFredSelSwitcher4();
	
});


function carouFredSelSwitcher1() {
	$('.list_styles1 ul').carouFredSel({
		infinite: false,
		auto : false,
		prev: '#prev1',
		next: '#next1',
		pagination  : {
			items : 1
		}
	});
}
function carouFredSelSwitcher2() {
	$('.list_styles2 ul').carouFredSel({
		infinite: false,
		auto : false,
		prev: '#prev2',
		next: '#next2',
		pagination  : {
			items : 1
		}
	});
}
function carouFredSelSwitcher3() {
	$('.list_styles3 ul').carouFredSel({
		infinite: false,
		auto : false,
		prev: '#prev3',
		next: '#next3',
		pagination  : {
			items : 1
		}
	});
}
function carouFredSelSwitcher4() {
	$('.list_styles4 ul').carouFredSel({
		infinite: false,
		auto : false,
		prev: '#prev4',
		next: '#next4',
		pagination  : {
			items : 1
		}
	});
}	