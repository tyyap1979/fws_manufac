/*
 * Simplpan Admin Panel
 *
 * Copyright (c) 2011 Kasper Kismul
 *
 * http://themeforest.net/user/Kaasper/profile
 *
 * This file configures all the different jQuery scripts used in the Simplpan Admin Panel template. 
 *
 */


//-------------------------------------------------------------- */
// Inserts "v"'s for dropdowns before documment load
//-------------------------------------------------------------- */

// Inserts the "v" Icon
$("<span class='v'></span>").insertBefore("#userpanel ul.dropdown ul.subnav");
	
// Inserts the "v" Icon
$("<span class='v'></span>").insertAfter("ul#navigation.dropdown ul.subnav");

//-------------------------------------------------------------- */
// When document is loaded, load custom jQuery
//-------------------------------------------------------------- */

	(function ($){
	   $(document).ready(function() {

//-------------------------------------------------------------- */
// Dropdown z-index Fix for IE7 (Added in 1.2)
//-------------------------------------------------------------- */
	
	$(function() {
		var zIndexNumber = 1000;
		$('.ie7 #wrap div').each(function() {
			$(this).css('zIndex', zIndexNumber);
			zIndexNumber -= 10;
		});
	});


/* End - Added in 1.2 */

//-------------------------------------------------------------- */
//
// 	**** Graphs, Bars, Pie (Visualize) **** 
//
// 	For more information go to:
//	http://www.filamentgroup.com/lab/update_to_jquery_visualize_accessible_charts_with_html5_from_designing_with/
//
//-------------------------------------------------------------- */

	$('table.stats').each(function() {
		
		var $stats_table = $(this);

		// Gets width of box container
		if ($(this).parent('div').width() > "840") {	
			var $stats_width = "833";
		}
		else {
			var $stats_width = ($(this).parent('div').width()) - 100;
		}
		
		// If there is a tab, find the active
		if ($(this).parent().prev().children('ul.sorting').length) {
			
			// Sorting shortcut
			var $stats_sorting = $(this).parent().prev().children('ul.sorting');
			
			// Adds 
			$($stats_sorting).find('a').each(function() {
				
				// Finds the href and removes the #
				var $stats_type = $(this).attr('href').replace("#","");
				
				// If it active, then show it
				if ($(this).hasClass('active')) {
					

					if($stats_type == 'line' || $stats_type == 'pie') {		
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c'],
							lineDots: 'double',
							interaction: true,
							multiHover: 15,
							tooltip: true,
							tooltiphtml: function(data) {
								var html ='';
								for(var i=0; i<data.point.length; i++){
									html += '<p class="stats_tooltip"><strong>'+data.point[i].value+'</strong> '+data.point[i].yLabels[0]+'</p>';
								}	
								return html;
							}
						}).addClass($stats_type);
					} else {
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c']
						}).addClass($stats_type);
					};
					

				}
				
				// Else hide it
				else {
					
					if($stats_type == 'line' || $stats_type == 'pie') {		
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c'],
							lineDots: 'double',
							interaction: true,
							multiHover: 15,
							tooltip: true,
							tooltiphtml: function(data) {
								var html ='';
								for(var i=0; i<data.point.length; i++){
									html += '<p class="stats_tooltip"><strong>'+data.point[i].value+'</strong> '+data.point[i].yLabels[0]+'</p>';
								}	
								return html;
							}
						}).hide().addClass($stats_type);
					} else {
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c']
						}).hide().addClass($stats_type);
					};
					
				};
				
			});

			// When one of the tabs are clicked
			$($stats_sorting).children().children('a').click(function(){
				
				// The href without #
				var $stats_sorting_type = "." + $(this).attr('href').replace("#","");
				
				// Hides all
				$($stats_table).parent().find(".visualize").hide();
				
				// Fades inn the one thats clicked
				$($stats_table).parent().find($stats_sorting_type).fadeIn('slow');
				
				// Removes 'active' class
				$($stats_sorting).children().children('a').removeClass('active');
				
				// Adds active state to the one you clicked
				$(this).addClass('active');
				return false; //Prevent the browser jump to the link anchor
			}); 
			
		}
		// If there is no tabs
		else {
			if ($(this).hasClass('bar')) {
				var $stats_type = 'bar';
			}
			else if ($(this).hasClass('line')) {
				var $stats_type = 'line';
			}
			else if ($(this).hasClass('pie')) {
				var $stats_type = 'pie';
			}
			else if ($(this).hasClass('area')) {
				var $stats_type = 'area';
			}
			else {
				// Default stats type
				var $stats_type = 'line';
			};
			
			if($stats_type == 'line' || $stats_type == 'pie') {		
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c'],
							lineDots: 'double',
							interaction: true,
							multiHover: 15,
							tooltip: true,
							tooltiphtml: function(data) {
								var html ='';
								for(var i=0; i<data.point.length; i++){
									html += '<p class="stats_tooltip"><strong>'+data.point[i].value+'</strong> '+data.point[i].yLabels[0]+'</p>';
								}	
								return html;
							}
						}).addClass($stats_type);
					} else {
						$($stats_table).hide().visualize({
							type: $stats_type,	// 'bar', 'area', 'pie', 'line'
							width: $stats_width,
							height: '240px',
							colors: ['#6fb9e8', '#ec8526', '#9dc453', '#ddd74c']
						}).addClass($stats_type);
				};
			
		};
		
	});
	
	/* IE Fix - Added in 1.1 */
	// Change inner color on mouseover intecation
	if(!$.browser.msie) { // IE is a bit slow, but is possible. Future versions may solve this problem
		var currentHoverPoint = null;
		// listen for hovering events
		$('table.stats')
			.bind('vizualizeOver',function(e,data){
				currentHoverPoint = data.point;
				$(data.point.elem).parents('table').trigger('visualizeRedraw');
			})
			.bind('vizualizeOut',function(e,data){
				currentHoverPoint = null;
				$(data.point.elem).parents('table').trigger('visualizeRedraw');
			});
	
		// Modify painting for hovering effect
		$('table.stats').bind('vizualizeBeforeDraw',function hoverBeforeDraw(e,data){
			if(currentHoverPoint) {
				var item,i,j,len = data.tableData.allItems.length;
				for(i=0;i<len;i+=1) { item = data.tableData.allItems[i];
					if(currentHoverPoint == item) {
						item.innerColor = item.color;
						// item.dotSize = item.dotSize*1.4;
						// item.dotInnerSize = item.dotInnerSize*1.4;
					}
				}
			}
		});
		
	};
	/* IE Fix End - Added in 1.1 */
	
//-------------------------------------------------------------- */
// Box Slider
//-------------------------------------------------------------- */

	// When top box is clicked, it slides up
	$(".box_top h2").click(function(){
		$(this).toggleClass("toggle_closed").parent().next().slideToggle("slow");
		return false; //Prevent the browser jump to the link anchor
	}); 


//-------------------------------------------------------------- */
// Box Tabs
//-------------------------------------------------------------- */

	$('div.tabs').each(function() {
		

		var $tabs_name = $(this);
		
		// If there is a tab, find the active
		if ($(this).parent().prev().children('ul.sorting').length) {
			
			// Sorting shortcut
			var $tabs_tabs = $(this).parent().prev().children('ul.sorting');
			
			// If it active, then show it
			if ($($tabs_tabs).children().children('a').hasClass('active')) {
					
				// Finds the href and removes the #
				var $stats_type = $($tabs_tabs).children().find('.active').attr('href');
					
				$($tabs_tabs).children().children('a').each(function() {
					var $tab_names = $(this).attr('href');
					// Hides all
					$($tabs_name).find($tab_names).hide();
				});
					
				$($tabs_name).find($stats_type).show();
			};
	
			// When one of the tabs are clicked
			$($tabs_tabs).children().children('a').click(function(){
				
				$($tabs_tabs).children().children('a').each(function() {
					var $tab_names = $(this).attr('href');
					// Hides all
					$($tabs_name).find($tab_names).hide();
				});
				
				// The href without #
				var $tabs_tabs_type = $(this).attr('href');
				
				// Fades inn the one thats clicked
				$($tabs_name).parent().find($tabs_tabs_type).fadeIn('slow');
				
				// Removes 'active' class
				$($tabs_tabs).children().children('a').removeClass('active');
				
				// Adds active state to the one you clicked
				$(this).addClass('active');
				return false; //Prevent the browser jump to the link anchor
			}); 
		};	
		
	});

//-------------------------------------------------------------- */
//
// 	**** Table Sorting (DataTables) **** 
//
// 	For more information go to:
//	http://www.datatables.net/
//
//-------------------------------------------------------------- */

	// Table Sorting with every feature (search, pagination..)	
	var ajaxSrc;
	
	$('table.sorting').each(function(){
		var formAction = $(this).parents("form").attr("action");
		if(formAction){
			ajaxSrc = formAction;
		}else{
			ajaxSrc = window.location.pathname;	
			if(ajaxSrc.substr(ajaxSrc.length-5,5)=='.html'){
				ajaxSrc = ajaxSrc.substring(0, ajaxSrc.length-1);
			}
		}		
		var myTable = $(this).dataTable( {		
			"bProcessing": true,
			"bServerSide": true,		
			"bSort": false,		
			"sPaginationType": "full_numbers",
			"bAutoWidth": true,
			"sAjaxSource": ajaxSrc+"?return=json&action1=action.search",
			"fnServerData": function ( sSource, aoData, fnCallback ) {
		    	$.ajax( {
			        "dataType": 'json',
			        "type": "POST",
			        "url": sSource,
			        "data": aoData,
			        "success": function(json){ if(json.redirect){ window.location=json.redirect; }else{	fnCallback(json); }}
		      });}
		});
		
	  	$(this).parent().find('.dataTables_filter input')
	    	.unbind('keypress keyup')
	    	.bind('keypress keyup', function(e){
	      		if (e.keyCode == 13){
	      			myTable.fnFilter($(this).val());
	      			e.preventDefault(); return;
	      		}
	    });  
	});
	
	
	// Removes sorting information if there are table actions due to space issues
	if ($('.table_actions')) {
		$('.table_actions').each(function() {
			$(this).parent().find('.dataTables_info').hide();
		});	
	};
	
//-------------------------------------------------------------- */
//
// 	**** Tips (Poshytip) **** 
//
// 	For more information go to:
//	http://vadikom.com/demos/poshytip/
//
//-------------------------------------------------------------- */

	// Tip for home icon etc.
	$('.tip').poshytip({
		className: 'tip-theme',
		showTimeout: 1,
		alignTo: 'target',
		alignX: 'center',
		alignY: 'bottom',
		offsetX: 0,
		offsetY: 16,
		allowTipHover: false,
		fade: false,
		slide: false
	});
	
	// Tip that stays
	$('.tip-stay').poshytip({
		className: 'tip-theme',
		showOn:'focus',
		showTimeout: 1,
		alignTo: 'target',
		alignX: 'center',
		alignY: 'bottom',
		offsetX: 0,
		offsetY: 16,
		allowTipHover: false,
		fade: false,
		slide: false
	});

//-------------------------------------------------------------- */
// Removal of notice boxes when pressed
//-------------------------------------------------------------- */

	// When notice box is clicked
	$("div.notice, p.error, p.warning, p.info, p.note, p.success").click(function() { 
		$(this).fadeOut('slow');
	});   

//-------------------------------------------------------------- */
// Drop Downs
//-------------------------------------------------------------- */

// User Panel Dropdown
	
	// When hovering over a ul element with "dropdown" class
	$("#userpanel ul.dropdown").hover(function() { 
	  
		// Sets the top text as active (the dropdown)
		$(this).find(".top").addClass("active");
		
		// Slides the "subnav" on hover
		$(this).find("ul.subnav").show();
		// Shows the white 'v'
		$(this).find("span.v").addClass("active");
	  
		// On hover off
		$(this).hover(function() {  
		}, function(){  
			
			// Hides the subnavigation when isn't on the "subnav" anymore
			$(this).find("ul.subnav").stop(false, true).hide(); 
			
			// Removes top text as active (the dropdown)
			$(this).find(".top").removeClass("active");
			
			// Hides the white 'v'
			$(this).find("span.v").removeClass("active");
		});  
	});  

// Navigation Dropdown

	// When hovering over a ul element with "dropdown" class
	$("ul#navigation.dropdown li.topnav").hover(function() { 
	
		// Copys the navigation name and the "v" icon to the top of the dropdown
		$(this).clone().prependTo("ul#navigation.dropdown .subnav");
		$("ul#navigation.dropdown .subnav .subnav").remove();	
		
		// Slides the "subnav" on hover
		$(this).find("ul.subnav").show();
		
		// On hover off
		$("ul#navigation.dropdown ul.subnav").parent().hover(function() {  
		}, function(){  
			
			// Hides the subnavigation when isn't on the "subnav" anymore
			$(this).find("ul.subnav").stop(false, true).hide(); 
			
			// Removes the top part to prevent duplication
			$("ul#navigation.dropdown .subnav .topnav").remove();
		});  
	});  
 
//-------------------------------------------------------------- */
// Check all checkboxes
//-------------------------------------------------------------- */

	$('.checkall').click(function () {
		var checkall =$(this).parents('table').find(':checkbox').attr('checked', this.checked);
		$.uniform.update(checkall);
	});


// End jQuery
   });
})(jQuery);
