	function changeToMm(obj){
		var objValue = obj.val();
		var mmValue = obj.val();
		
		if(objValue.indexOf("in")!=-1){
			mmValue = convert("in", "mm", objValue);
		}else if(objValue.indexOf("ft")!=-1){
			mmValue = convert("ft", "mm", objValue);
		}
		
		obj.val(parseInt(mmValue));
	}
	
	function parseIntCustom(value){
		var rtnValue = 0;
		if(value!=""){
			rtnValue = parseInt(value);
		}
		return rtnValue;
	}
	
	function convert(fromUnit, toUnit, value){					
		var divideValue = 1;
		var calValue = 0;
		// 2in = ?mm			
		if(isNaN(value)){				
			value = value.replace(/(^\s*)|(\s*$)/g, "");
			if(value.indexOf("in")!=-1){
				value = value.substring(0, value.indexOf("in"));
			}
			
			if(value.indexOf("ft")!=-1){
				value = value.substring(0, value.indexOf("ft"));				
			}
		}
		//alert("fromUnit="+fromUnit+", "+"toUnit="+toUnit + ",  value="+value);
		if(toUnit=='in'){				
			if(fromUnit=='mm'){					
				calValue = (value / 25.4);
			}
		}else if(toUnit=='ft'){
			if(fromUnit=='mm'){
				calValue = (value / 304.8);					
			}else if(fromUnit=='in'){
				calValue = (value / 12);
			}
		}else if(toUnit=='mm'){	
			if(fromUnit=='in'){
				if(value.indexOf("/")!=-1){
					var spaceIndex = value.indexOf(" ");
					var front, back;
					if(spaceIndex==-1){ // only 1/16in
						front = 0;
						back = 	value;
					}else{ // 12 2/16in
						front = value.substring(0, spaceIndex);
						back = 	value.substring(spaceIndex+1, value.length);
					}
											
					calValue = (parseFloat(front) * 25.4) + (parseFloat(eval(back)) *  25.4);
				}else{
					calValue = (value * 25.4);
				}					
			}
			
			if(fromUnit=='ft'){
				if(value.indexOf("/")!=-1){
					var spaceIndex = value.indexOf(" ");
					var front, back;
					if(spaceIndex==-1){ // only 1/16in
						front = 0;
						back = 	value;
					}else{ // 12 2/16in
						front = value.substring(0, spaceIndex);
						back = 	value.substring(spaceIndex+1, value.length);
					}
											
					calValue = (parseFloat(front) * 12 * 25.4) + (parseFloat(eval(back)) * 12 *  25.4);
				}else{
					calValue = (value * 12 * 25.4);
				}					
			}
		}else if(toUnit=='sf'){
			if(fromUnit=='mm'){
				calValue = (value * 0.0000107639104);					
			}else if(fromUnit=='in'){
				calValue = (value / 144);
			}
		}		
		//calValue = Math.round((calValue*100)/100);
		// calValue = ajaxUtil.roundNumber(calValue,5);
		//alert("fromUnit="+fromUnit+", "+"toUnit="+toUnit + ",  value="+value + ",  calValue=" + calValue);
		return calValue;
	}
	
	/*
	function buildDoor(totalDoor, boxWidth, boxHeight){			
		var displayDivWidth = $("#displayDiv").width() - 100;
		var displayDivHeight = 0;	
		var pixWidth = 0;
		var pixHeight = 0;
		var ratio = mmToPixelRatio; //displayDivWidth / (boxWidth * totalDoor);
		
		pixWidth = parseInt(boxWidth * ratio);
		pixHeight = parseInt(boxHeight * ratio);		 
			
		for(var i=0; i<totalDoor; i++){
			$("<div class='ui-widget-content door-widget' style='width: "+pixWidth+"px; height: "+pixHeight+"px;'><h3 class='ui-widget-header'>Door "+(i+1)+"</h3></div>").appendTo("#displayDiv");					
		}
		$("<p>&nbsp;</p>").appendTo("#displayDiv");
		//
	}
	*/