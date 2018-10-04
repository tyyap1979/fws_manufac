	var boxtype, width, height, depth, thickness, doormargin, adjustboardwidth, elevatedheight, doorthickness, description;
	
	// Global Setting
	var doorMaxWidth = 610; // ~2Ft	
	var kitchenDoorMaxWidth = 450; // ~2Ft
	var normalDoorMaxWidth = 610;
	var boxMaxHeight = 2400; // ~8Ft
	var backBoardThickness = 5; // 5mm
	var mmToPixelRatio = 0.2520; // 1mm = 3.7795px, scale 1 to 15
	var boardRatio = 0.2520; // 1mm = 3.7795px, scale 1 to 15
	var totalDoor = 0;
	var backBoardMargin = 0; // 3mm each side
	var dividerMinus = 10;
	
	function calculate(){
		$("#boardTable").empty();
		$("#materialDiv DIV").empty();
		$("#cutPiecesDiv").empty();
		$("#boardTable").empty();
		
		$("#detailTable tbody tr").each(function(i){
				description = $(this).find("#description").val();
				boxtype = $(this).find("#boxtype").val();
				width = parseIntCustom($(this).find("#width").val());
				height = parseIntCustom($(this).find("#height").val());
				depth = parseIntCustom($(this).find("#depth").val());
				thickness = parseIntCustom($(this).find("#thickness").val());
				doorthickness = parseIntCustom($(this).find("#doorthickness").val());
				doormargin = parseIntCustom($(this).find("#doormargin").val());
				totalDoor = parseIntCustom($(this).find("#totaldoor").val());
				doubledivider = 1;
				singledivider = 1;
				adjustboardwidth = parseIntCustom($(this).find("#adjustboardwidth").val());
				elevatedheight = parseIntCustom($(this).find("#elevatedheight").val());
				
				$("#displayDiv").empty();
				doorMaxWidth = kitchenDoorMaxWidth;
				frameCalculation($(this));
				
				$(this).find("#checkrow").attr("checked","");
			
		});
		
		// Organize Running No
		var i = 1;
		$("#boardTable tr").each(function(x){			
			if($(this).find("input[name=size]").val()!=undefined){
				$(this).find("input[name=no]").val(i);
				i++;
			}
		})
								
	}
	
	function frameCalculation(row){
		var no = 1;
		var boxHeight = 0;
		var boxWidth = 0;
		//var totalDoor = 0;
		var totalBox = 0;
		var stack = 1; // If more than 8ft height then make it 2 stack
		var additionalWallMount = 0; // kitchen +2 because to replace top board
		
		//if($("#boardTable").html()!=""){
			$("<tr id='newrow'><td colspan='5'><input type='text' name='desc' size='100' value='"+description+"'></td></tr>").appendTo("#boardTable");
		//}
		
		if(boxtype=="sliding"){
			depth = depth - 100; // Minus 4 inches for the track
		}
		
		boxHeight = height;
		stack = 1;
		
		if(adjustboardwidth>0){
			width = width - (adjustboardwidth*2);
			boxHeight = boxHeight - (adjustboardwidth);
		}
		
		// Get Total Box 
		//totalDoor = (width%doorMaxWidth);
		boxWidth = width / totalDoor;
		
		// Rounding Box Width, remove all decimal mm. 
		boxWidth = parseInt(boxWidth);
		boxHeight = boxHeight - elevatedheight;
		
		$("#totalDoor").val(totalDoor);
		
		var carcassDepth = depth - doorthickness - thickness - backBoardThickness;
		var carcassSingleInternalWidth = boxWidth - (thickness * 2);
		var carcassDoubleInternalWidth = (boxWidth * 2) - (thickness * 2);	
		
		// Door 
		addToBoardTable(boxWidth - (doormargin), boxHeight, totalDoor, "Door");
		
		// Elevated Board
		if(elevatedheight>0){
			addToBoardTable(width, elevatedheight, 1, "Elevated Board");		
		}	
		
			
		if(totalDoor==1){
			totalBox = 1;
			// Vertical Board
			addToBoardTable(carcassDepth, boxHeight, (totalBox * 2), "Vertical Board");
			
			// Horizontal Board
			addToBoardTable(carcassSingleInternalWidth, carcassDepth, (boxtype=="kitchenbase-legs")?totalBox:(totalBox * 2), "Horizontal Single Board");							
			
			// Divider Board 
			addToBoardTable(carcassSingleInternalWidth - 2, carcassDepth - dividerMinus, 1, "Single Box Divider Board");			
			
			// Horizontal Wall Mount
			addToBoardTable(boxWidth, 80, totalBox * 3, "Horizontal Wall Mount Single");
				
			if(boxtype=="kitchenbase-legs"){
				addToBoardTable(carcassSingleInternalWidth, 80, totalBox * 2, "Horizontal Top Wood Single");				
				addToBoardTable(carcassSingleInternalWidth, 50, totalBox * 1, "Horizontal Bar Single");	
			}
			
			// Back Board				
			addToBoardTable(boxWidth - backBoardMargin, boxHeight - backBoardMargin, totalBox, "Back Board Single");
			
		}else if(totalDoor>2 && totalDoor%2==1){
			// With 1 Single Door Box 
			totalBox = (totalDoor+1) / 2;
				
			// Vertical Board
			addToBoardTable(carcassDepth, boxHeight, (totalBox * 2), "Vertical Board");
						
			// Horizontal Board
			addToBoardTable(carcassSingleInternalWidth, carcassDepth, (boxtype=="kitchenbase-legs")?1:2, "Horizontal Single Board");
			addToBoardTable(carcassDoubleInternalWidth, carcassDepth, (boxtype=="kitchenbase-legs")?totalBox:((totalBox-1) * 2), "Horizontal Double Board");
			
			// Divider Board 
			addToBoardTable(carcassSingleInternalWidth - 2, carcassDepth - dividerMinus, 1, "Single Box Divider Board");	
			addToBoardTable(carcassDoubleInternalWidth - 2, carcassDepth - dividerMinus, 1, "Double Box Divider Board");	
			
			// Horizontal Wall Mount
			addToBoardTable(boxWidth, 80, totalBox * 3, "Horizontal Wall Mount Single");
			addToBoardTable(boxWidth * 2, 80, totalBox * 3, "Horizontal Wall Mount Double");
			
			if(boxtype=="kitchenbase-legs"){
				addToBoardTable(carcassSingleInternalWidth, 80, totalBox * 2, "Horizontal Top Wood Single");				
				addToBoardTable(carcassSingleInternalWidth, 50, totalBox * 1, "Horizontal Bar Single");	
				
				addToBoardTable(carcassDoubleInternalWidth, 80, totalBox * 2, "Horizontal Top Wood Double");				
				addToBoardTable(carcassDoubleInternalWidth, 50, totalBox * 1, "Horizontal Bar Double");	
			}
			
			// Back Board				
			addToBoardTable(boxWidth - backBoardMargin, boxHeight - backBoardMargin, totalBox, "Back Board Single");
			addToBoardTable((boxWidth * 2) - backBoardMargin, boxHeight - backBoardMargin, totalBox, "Back Board Double");
		}else{			
			// All Double Door Box
			totalBox = (totalDoor / 2);
			
			// Vertical Board (Box Depth = Actual Cabinet Depth - Door Thickness - Board Thickness (Wall Mount Wood)
			addToBoardTable(carcassDepth, boxHeight, (totalBox * 2), "Vertical Board");
			
			// Horizontal Board
			addToBoardTable(carcassDoubleInternalWidth, carcassDepth, (boxtype=="kitchenbase-legs")?totalBox:(totalBox * 2), "Horizontal Double Board");
						
			// Divider Board 			
			addToBoardTable(carcassDoubleInternalWidth - 2, carcassDepth - dividerMinus, 1, "Double Box Divider Board");
			
			// Horizontal Wall Mount
			addToBoardTable((boxWidth * 2), 80, totalBox * 3, "Horizontal Wall Mount Double");
				
			if(boxtype=="kitchenbase-legs"){
				addToBoardTable(carcassDoubleInternalWidth, 80, totalBox * 2, "Horizontal Top Wood Double");				
				addToBoardTable(carcassDoubleInternalWidth, 50, totalBox * 1, "Horizontal Bar Double");	
			}			
			
			// Back Board				
			addToBoardTable((boxWidth * 2) - backBoardMargin, boxHeight - backBoardMargin, totalBox, "Back Board Double");					
		}
		
		// Sliding track Panel
		if(boxtype=="sliding"){
			addToBoardTable(width, 300, 1, "Sliding Track Panel Top");
			addToBoardTable(width, depth, 1, "Sliding Track Panel Bottom");			
		}
		// Elevated Board
		//addToBoardTable(width + (adjustboardwidth*2), elevatedheight, 1, "Elevated Board");
		
		if(adjustboardwidth>0){
			//addToBoardTable(width + (adjustboardwidth*2), adjustboardwidth, 1, "Adjustment Board (W)");
			addToBoardTable(height, adjustboardwidth , 2, "Adjustment Board (H)");			
		}
		row.find("#totaldoor").text(totalDoor);
	}
	
	function addToBoardTable(width, height, quantity, description){		
		var rowTR = "<tr id='"+randomString(15)+"'>";
		rowTR += "<td><input type='text' name='no' size='1' value='' /></td>";
		rowTR += "<td><input type='text' name='size' size='10' value='"+parseInt(width) + " x " + parseInt(height)+"' /></td>";
		rowTR += "<td><input type='text' name='qty' size='1' value='"+quantity+"' /></td>";
		rowTR += "<td><input type='text' name='description' size='30' value='"+description+"' /></td>";
		//rowTR += "<td><button id='resetRow'>Reset</button></td>";
		rowTR += "<td><button id='removeRow'>Remove</button></td>";
		rowTR += "</tr>";
		$(rowTR).appendTo("#boardTable");
	}
	
	function buildWoodPieces(){
		var no, size, qty, description;
		var width = 0;
		var height = 0;
		var objHtml = "";
		var objNormal = "";
		var objDoor = "";
		var objPanel = "";
		var objDivider = "";
		var objBackBoard = "";
		var trID = "";
		var boardCreated = false;
		//$(".pieceDiv").remove();
		
		$("#boardTable tr").each(function(x){
			boardCreated = false;
			trID = $(this).get(0).id;			
			
			if(trID!="" && $("#board"+trID).size()>0){
				boardCreated = true; 
			}
			//if(!boardCreated){
			//	alert(trID+"No = "+$(this).find("input[name=no]").val());
			//}
			if(!boardCreated && $(this).find("input[name=size]").val()!=undefined){
				no = $(this).find("input[name=no]").val();
				size = $(this).find("input[name=size]").val();
				qty = $(this).find("input[name=qty]").val();
				description = $(this).find("input[name=description]").val();
				
				size = size.split(" x ");
				width = parseInt(size[0]);
				height = parseInt(size[1]);
				qty = parseInt(qty);
				for(var i=0; i<qty; i++){
					objHtml = "<div id='board"+trID+"' class='pieceDiv tooltip' title='"+description+"' style='width: "+parseInt((width + 2) * boardRatio)+"px; height: "+parseInt((height + 2) * boardRatio)+"px;";					
					if(description.indexOf("Door")!=-1){	
						objHtml += " background-color: Blue;";
					}else if(description.indexOf("Panel")!=-1){	
						objHtml += " background-color: Aqua;";
					}else if(description.indexOf("Divider Board")!=-1){	
						objHtml += " background-color: Green;";
					}else if(description.indexOf("Back Board")!=-1){	
						objHtml += " background-color: Gray";					
					}
					objHtml += "'>";
					objHtml += parseInt(width) + "mm x " + parseInt(height) +"mm";
					objHtml += "</div>";
					
					if(description.indexOf("Door")!=-1){	
						objDoor += objHtml;
					}else if(description.indexOf("Panel")!=-1){	
						objPanel += objHtml;
					}else if(description.indexOf("Divider Board")!=-1){	
						objDivider += objHtml;
					}else if(description.indexOf("Back Board")!=-1){	
						objBackBoard += objHtml;				
					}else{
						objNormal += objHtml;
					}
					
				}
			}
		});
		
		$(objNormal).appendTo("#cutPiecesDiv");
		$(objDivider).appendTo("#cutPiecesDiv");
		$(objPanel).appendTo("#cutPiecesDiv");
		$(objDoor).appendTo("#cutPiecesDiv");
		$(objBackBoard).appendTo("#cutPiecesDiv");
			
		makeDraggable();
		addToolTip();
		
		var original_position_offset = $('#cutPiecesDiv').offset();
	    sticky_offset = original_position_offset.top;
	}
	
	function addBoard(){
		var boardType = $("#boardSelect").val();
		var boardName = $("#boardName").val();
		var ftRatio = 12*25.4*boardRatio;
		var board;
		var ft4 = parseInt((4*ftRatio));
		var ft6 = parseInt((6*ftRatio));		
		var ft8 = parseInt((8*ftRatio));
		
		var ft5 = parseInt((5*ftRatio));
		
		var desc = $("#boardSelect").find("option:selected").text();
		var boardObj;
		board = "<span id='boardTitle'>" + boardName + "</span> <a href='#' id='btnDelete'>Delete</a><div class='boardDiv droppable' style='";
				
		if(boardType=="6x8"){
			board += "width:"+ft8+"px; height:"+ft6+"px'></div>";
		}else{
			board += "width:"+ft8+"px; height:"+ft4+"px'></div>";
		}			
		
		boardObj = $("<tr><td>"+board+"</td></tr>");
		boardObj.prependTo($("#boardPanelDiv tbody"));
		makeDropable(board);		
	}
	
	function makeDraggable(){		
		$( ".pieceDiv" ).unbind("click");
		
		$( ".pieceDiv" ).draggable();	
		$( ".pieceDiv" ).click(function(e){			
			var ele = $(e.target);
			var w = ele.css("width");
			var h = ele.css("height");
			var size = ele.text();		
			size = size.split(" x ");	
			ele.css({'width':h, 'height': w});
			
     		if(h>w){     		
     			ele.text(size[0] + " x " + size[1]);
     		}else{     			
     			ele.text(size[1] + " x " + size[0]);
     		}
     		
						
		});
		
		
	}
	
	function makeDropable(board){
		$(".droppable").droppable({
	      drop: function( event, ui ) {
	      	var cloneDiv;	      	
	      	var width = 0;
	      	var height = 0;
	      	var rotate = false;
	      	
	      	if($(ui.draggable).prop("tagName")!="TR"){
		      	$(ui.draggable).draggable( "destroy" );
		      	cloneDiv = $(ui.draggable).clone(true);
		      	
		      	if(parseInt(cloneDiv.css("height"))>parseInt($(this).css("height"))){
		      		rotate = true;
		      	}else{
			      	if(parseInt(cloneDiv.css("width"))>parseInt($(this).css("width"))){
			      		rotate = true;
			      	}
		      	}
		      	if(rotate){
			      	width = cloneDiv.css("height");
		     		height = cloneDiv.css("width");
		      		cloneDiv.css({'width': width, 'height': height});
		      		
		      		var text = cloneDiv.text().split(" x ");
		      		cloneDiv.text(text[1] + " x " + text[0]);
		      	}
		      	
		      	width = cloneDiv.css("height");
	     		height = cloneDiv.css("width");
	     		
		      	cloneDiv.appendTo($(this));	      	
		        $(ui.draggable).remove();
		        cloneDiv.css({'top': 'auto', 'left': 'auto'});	 
		        
		        cloneDiv.draggable();       
		     }	        
	      }
	    });
	}
	function print(){		
		boardDetail();
		differentiateBoard();
		//$("#copyText").val($("#boardPanelDiv").html());
		//window.print();
	}			
	
	function boardDetail(){
		var pieceMeasurement = "";		
		var boardTitle = "";
		var td;
		$("#boardPanelDiv tbody tr").each(function(){			
			boardTitle = "<span class='boardTitlePrint'>" + $(this).find("#boardTitle").text() + "</span> <br> ";
			pieceMeasurement = "";
			$(this).find(".pieceDiv").each(function(){
				pieceMeasurement += "<span class='boardMeasurement'>[" + $(this).text() + "] </span>";				
			});
			td = $(this).find("td:first");
			$(boardTitle + pieceMeasurement).prependTo(td);
			td.html("<div>"+td.html()+"</div>");				
		});			
	}
	
	function differentiateBoard(){
		var boardName = "";
		var prevBoardName = "";
		$("#boardPanelDiv tbody tr").each(function(){
			boardName = $(this).find("#boardTitle").text();	
			
			if(prevBoardName!="" && boardName!=prevBoardName){				
				$(this).css({'page-break-after': 'always'});				
			}else{
				$(this).css({'page-break-after': ''});				
			}	    
			prevBoardName = boardName;
		});			
	}
	
	function copy2Clipboard(){
		$("#detailTable tbody input").each(function(){
		    $(this).attr("value", $(this).val());
		});
		
		$("#boardTable input").each(function(){
		    $(this).attr("value", $(this).val());
		});
		/*
		var detailTableTBodyHTML = encodeURI($("#detailTable tbody").html());
		var boardTableHTML = encodeURI($("#boardTable").html());
		var boardPanelDivHTML = encodeURI($("#boardPanelDiv").html());
		var cutPiecesDivHTML = encodeURI($("#cutPiecesDiv").html());
		
		var jsonHTML = "{'detailTableTBodyHTML': '"+detailTableTBodyHTML+"', 'boardTableHTML': '"+boardTableHTML+"', 'boardPanelDivHTML': '"+boardPanelDivHTML+"', 'cutPiecesDivHTML': '"+cutPiecesDivHTML+"'}";
		$("#copyText").val(jsonHTML);
		*/
		
		var detailTableTBodyHTML = $("#detailTable tbody").html();
		var boardTableHTML = $("#boardTable").html();
		var boardPanelDivHTML = $("#boardPanelDiv").html();
		var cutPiecesDivHTML = $("#cutPiecesDiv").html();
		
		$("#copyText").val(detailTableTBodyHTML+"|"+boardTableHTML+"|"+boardPanelDivHTML+"|"+cutPiecesDivHTML);
		/*
		$("#detailTableText").val($("#detailTable tbody").html());
		$("#boardTableText").val($("#boardTable").html());
		$("#boardPanelText").val($("#boardPanelDiv").html());
		$("#pieceTableText").val($("#cutPiecesDiv").html());
		*/
	}
	
	function copyFromClipboard(){
		var detailTableTBodyHTML = $("#detailTable tbody");
		var boardTableHTML = $("#boardTable");
		var boardPanelDivHTML = $("#boardPanelDiv");
		var cutPiecesDivHTML = $("#cutPiecesDiv");
		
		/*
		var jsonHTML = $("#copyText").val();
		jsonHTML = eval('(' + jsonHTML + ')');
		detailTableTBodyHTML.html(decodeURI(jsonHTML.detailTableTBodyHTML));
		boardTableHTML.html(decodeURI(jsonHTML.boardTableHTML));
		if(jsonHTML.boardPanelDivHTML!=""){
			boardPanelDivHTML.html(decodeURI(jsonHTML.boardPanelDivHTML));
		}
		cutPiecesDivHTML.html(decodeURI(jsonHTML.cutPiecesDivHTML));
		*/
		var jsonHTML = $("#copyText").val().split("|");
		detailTableTBodyHTML.html(jsonHTML[0]);
		boardTableHTML.html(jsonHTML[1]);
		if(jsonHTML[2]!=""){
			boardPanelDivHTML.html(jsonHTML[2]);
		}
		cutPiecesDivHTML.html(jsonHTML[3]);
		
		$(".toMM").bind("change", function(e){	    			    	
			changeToMm($(e.target));			
		});
		
		makeDropable($(".droppable"));
		makeDraggable();
		$("#copyText").val("");			
		
		$(".boardDiv").each(function (i){
			if($(this).html()==""){
				$(this).remove();
			}
		});
		
		addToolTip();
		sumTotalCutLength();
	}
	
	function sumTotalCutLength(){
		var totalMm = 0;
		$("#boardTable tr").each(function(x){
			if($(this).find("input[name=size]").val()!=undefined){
				size = $(this).find("input[name=size]").val();
				qty = $(this).find("input[name=qty]").val();
				
				size = size.split(" x ");
				width = size[0];
				height = size[1];
				qty = parseInt(qty);
				
				totalMm += (parseInt(width) + parseInt(height)) * 2 * parseInt(qty);
			}
		});
		
		alert("Total Meter: "+ totalMm / 1000);
	}
	
	function buildSorting(){
		var fixHelper = function(e, ui) {
			ui.children().each(function() {
				$(this).width($(this).width());
			});
			return ui;
		};
		
		var sortStopHandle = function (e, ui) {
			var element = e.target;
		
			$("#"+element.id).children("tr").not("#newrow").each(function(i) {
				$(this).find("input[name=no]").val(i+1);					
			});
			return ui;
		};
					
		
		$(".sortable tbody").sortable({helper: fixHelper, stop: sortStopHandle});		
	}
	
	function addToolTip(){
		$(".tooltip").tooltip({
      		position: {
        		my: "center bottom-20",
        		at: "center top",
        		using: function( position, feedback ) {
          		$( this ).css( position );
          		$( "<div>" )
            		.addClass( "arrow" )
            		.addClass( feedback.vertical )
            		.addClass( feedback.horizontal )
            		.appendTo( this );
        		}
      	}});
	}
		
	function randomString(length) {
		var text = "";
    	var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    	for( var i=0; i < length; i++ )
        	text += possible.charAt(Math.floor(Math.random() * possible.length));

    	return text;
	}

	var sticky_offset;
	
	$(function() {	    		
		makeDropable($("#cutPiecesDiv"));
	    $(".toMM").bind("change", function(e){	    			    	
			changeToMm($(e.target));			
		});
		
		$("#boardPanelDiv").click(function(e){	
			var ele = $(e.target);			
			if(ele.attr("id")=='btnDelete'){									
				var tr = ele.parent();
				var trID = "";
				while(tr.get(0).tagName!='TR'){			
					tr = tr.parent();
				}
				$(tr.find(".boardDiv").html()).appendTo("#cutPiecesDiv");
				makeDraggable();
				tr.remove();						
				e.preventDefault();	return;
			}
		});
		
		$("#boardTable").click(function(e){	
			var ele = $(e.target);
			if(ele.attr("id")=='removeRow'){							
				var tr = ele.parent();
				var trID = "";
				while(tr.get(0).tagName!='TR'){			
					tr = tr.parent();
				}
				trID = tr.get(0).id;				
				$("#board"+trID).remove();
				tr.remove();	
				e.preventDefault();	return;
			}
			if(ele.attr("id")=='resetRow'){							
				var tr = ele.parent();
				var trID = "";
				while(tr.get(0).tagName!='TR'){			
					tr = tr.parent();
				}
				trID = tr.get(0).id;
				$("#board"+trID).appendTo("#cutPiecesDiv");				
				e.preventDefault();	return;
			}
		});
		
		$(".btnAddRow").click(function(e){						
			var parentTable = $(e.target).parent();
										
			while(parentTable.get(0).tagName!='TABLE'){			
				parentTable = parentTable.parent();
			}
			parentTable = parentTable.find("TBODY");			
			var tr = parentTable.find("tr:last").clone(true);		
			//tr.find("input").val("");
			tr.attr("id",randomString(15));
			tr.find("#checkrow").attr('checked', 'checked');
			tr.appendTo(parentTable);				
			e.preventDefault();	return;
		});
		
		buildSorting();					
		$("#boardPanelDiv tbody").sortable();
		
		// Scrolling
		function fixDiv() {
			 var sticky_height = $('#cutPiecesDiv').outerHeight();
			 var where_scroll = $(window).scrollTop();
			 var window_height = $(window).height();
			//alert("sticky_height:"+sticky_height+"\n"+"where_scroll:"+where_scroll+"\n"+"window_height:"+window_height);
			 if(where_scroll > sticky_offset) {		    
				 $('#cutPiecesDiv').css('top', where_scroll - sticky_offset);		        
			 }else{
				 $('#cutPiecesDiv').css('top', 0);
			 }			
		  }
		$(window).scroll(fixDiv);
		fixDiv();
	
	});
	  
	  