jQuery(document).ready(function() {	

    megaMenu();
    
    ///delay #navi-logo overflow
    setTimeout("megaMenuLoadingFix()", 100);
    
    /// homepage info widget
    infoWidget();
    
    /// footer icons
	hoverEffects();
    
    table();
    blog();
    
    /// posrtfolio post
    //portfolio();
    
    /// portfolio filter
    //portfolioFilter();
    
    /// hover effect
    //portfolioZindex()
    
    /// hover effect
    portfolioItem();
    
    /// vertical aligns
    sloganButton();
	
});

function megaMenu() {

    $('.sub-container.non-mega').find("li:first").css('margin', '0');
    $('.sub').find(".row:last").css('margin', '0');

	$("#mega-menu li").removeClass("current");
	var url = window.location.pathname;		
	var filename = url.substring(url.lastIndexOf('/')+1);			
	$("#mega-menu li a[href="+filename+"]").parent().addClass("current");
}

function megaMenuLoadingFix() {
    $('#navi-logo').css('overflow', "visible");
}

function infoWidget() {

    $(".infowidget").children('.widgetWrap').addClass('add');
    $(".infowidget").addClass('addBg');
    $(".infowidget li:last").css({'background': "none", "marginBottom": 0});
    
  $(".infowidget").mouseenter(function() {
    $( this ).animate({boxShadow: '0 0 15px rgba(0, 0, 0, 0.3)'}, 200);
    $( this ).switchClass( "addBg", "removeBg", 200 );
    $( this ).children('.widgetWrap').switchClass( "add", "remove", 400 );
    
  }).mouseleave(function(){
    $( this ).animate({boxShadow: '0 0 0'}, 200);
    $( this ).switchClass( "removeBg", "addBg", 200 );
    $( this ).children('.widgetWrap').switchClass( "remove", "add", 400 );
    
  });
    
}
 
function hoverEffects() {
    
jQuery("a.vimeo").hover(function() {
	jQuery(this).animate({ backgroundColor: "#44bbff" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
jQuery("a.twitter").hover(function() {
	jQuery(this).animate({ backgroundColor: "#1ec7ff" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
jQuery("a.facebook").hover(function() {
	jQuery(this).animate({ backgroundColor: "#3b5998" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
jQuery("a.youtube").hover(function() {
	jQuery(this).animate({ backgroundColor: "#c4302b" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
jQuery("a.google").hover(function() {
	jQuery(this).animate({ backgroundColor: "#d84937" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
jQuery("a.linkedin").hover(function() {
	jQuery(this).animate({ backgroundColor: "#006699" },{duration:200,queue:false}, 'easeInSine');
},function() {
	jQuery(this).animate({ backgroundColor: "#121111" },{duration:300,queue:false}, 'easeOutSine');
});
}
function table() {
    
    $(".table ul").each(function(){
        $(this).find("li:odd").css("background-color", "#f0f0f0");
    });
    $(".table .column:last").find('li').css("border-right", "none");
    
}
function blog() {
    
    $(".sidebar .posts .post:last").css("background", "none");
}
function portfolioItem() {

    $(".portfolio-item").children('.portfolio-wrap').addClass('add');
    $(".portfolio-item").addClass('addBg');
    
  $(".portfolio-item").mouseenter(function() {
    $( this ).animate({boxShadow: '0 0 15px rgba(0, 0, 0, 0.3)'}, 200);
    $( this ).switchClass( "addBg", "removeBg", 200 );
    $( this ).children('.portfolio-wrap').switchClass( "add", "remove", 400 );
    
  }).mouseleave(function(){
    $( this ).animate({boxShadow: '0 0 0'}, 200);
    $( this ).switchClass( "removeBg", "addBg", 200 );
    $( this ).children('.portfolio-wrap').switchClass( "remove", "add", 400 );

  });
    
}

function sloganButton() {
    
    var moduleHeight = $('.slogan .style2 .grid_4').prev('.grid_12').height();
    var bnHeight = $('.slogan .style2 .grid_4 p').height();
    var Height = moduleHeight-bnHeight;
    
    $(".slogan .style2 .grid_4").css('paddingTop', Height/2);

}