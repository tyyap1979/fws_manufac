	var width, height, unit;	
	
	// Round Unit
	function roundUpSqft (value) {		
		if(typeof(_companyid)!='undefined' && _companyid=='ls'){
			return roundUpSqft_LS(value);
		}else{
			var converted = parseFloat(value); // Make sure we have a number
		   	var decimal = (converted - parseInt(converted, 10));		   		   	
		   	decimal = Math.round(decimal * 100);
		   	
		   	if(decimal<=8){
		   		converted = parseInt(converted, 10);
		   	}else if(decimal>=9 && decimal<=58){
		   		converted = parseInt(converted, 10) + 0.5;
		   	}else if(decimal>=59){
		   		converted = parseInt(converted, 10) + 1;
		   	}
		   	//alert("value = "+value + ", converted = "+converted);
		   	return converted;
		}
	} 		
	
	// Round Width & Height
	function roundMeasurement (value) {		
		var converted = parseFloat(value); // Make sure we have a number
	   	var decimal = (converted - parseInt(converted, 10));		   		   	
	   	decimal = Math.round(decimal * 100);
	   	
	   	if(decimal>=1 && decimal<=59){
	   		converted = parseInt(converted, 10);
	   	}else if(decimal>=60 && decimal<=99){
	   		converted = parseInt(converted, 10) + 1;
	   	}
	   	//alert("roundMeasurement value = "+value + ", converted = "+converted);
	   	return converted;		
	} 
	
	function roundUpSqft_LS (value) {
		// 2.1[0] = 2.1[0]
		// 2.1[1] = 2.1[5]
		// 2.1[6] = 2.2[0]
		var converted = 0;
		var lastDigit = 0;
		var afterDotDigit = 0;
		var beforeDotDigit = 0;
		var dotIndex = value.indexOf(".");
		if(dotIndex!=-1 && value.substring(dotIndex+1,value.length).length>1){
			beforeDotDigit = value.substring(0, dotIndex);
			afterDotDigit = value.charAt(dotIndex+1);
			lastDigit = value.charAt(dotIndex+2);
			if(lastDigit>0 && lastDigit<5){
				lastDigit = 5;
			}else if(lastDigit>5){
				lastDigit = 0;
				afterDotDigit= parseInt(afterDotDigit) + 1;
			}
			if(afterDotDigit>9){
				beforeDotDigit= parseInt(beforeDotDigit) + 1;
				afterDotDigit = 0;
			}
			//alert(beforeDotDigit +", " + afterDotDigit + ", " + lastDigit);
			converted = beforeDotDigit +"."+ afterDotDigit + "" + lastDigit;
		}else{
			converted = value;
		}
	   	//alert("value = "+value + ", converted = "+converted);
	   	return converted;
	}
	
	
	
	function funcS1_cal(){
		var ovl = [0,15,40,65,90];	
		calS1toS4(ovl);	
	}
	
	function funcS1_customise(){
		var ovl = [0,15,40,65,90];	
		customiseS1(ovl);
		return RowData.customData;			
	}
	
	function funcS2_cal(){
		var ovl = [0,25,60,95,130];	
		calS1toS4(ovl);	
	}
	
	function funcS2_customise(){
		var ovl = [0,25,60,95,130];	
		customiseS2toS4(ovl);
		return RowData.customData;			
	}
	
	function funcS3_cal(){
		var ovl = [0,35,80,125,170];	
		calS1toS4(ovl);	
	}
	
	function funcS3_customise(){		
		var ovl = [0,35,80,125,170];
		customiseS2toS4(ovl);
		return RowData.customData;			
	}
	
	function funcS4_cal(){
		var ovl = [0,45,100,155,210];	
		calS1toS4(ovl);	
	}
	
	function funcS4_customise(){
		var ovl = [0,45,100,155,210];
		customiseS2toS4(ovl);
		return RowData.customData;			
	}
	
	function calS1toS4(ovl){
		var data = eval(RowData.customData);
		var tcol = data.colw.length;
		var trow = data.rowh.length;
		var totalWidth = 0
		var totalHeight = 0;		
		
		for(var i=0;i<tcol; i++){
			totalWidth+=data.colw[i];
			
			//trow = data.rowh[i].length;
			//for(var a=0;a<trow; a++){
			//	totalHeight+=data.rowh[i][a];
			//}			
		}
		//totalHeight = totalHeight / data.rowh.length;		
		totalHeight = RowData.height - 35		
		//totalHeight += 66 + ((trow-1)*12);
		RowData.curentTR.find("[name$=width]").val(ajaxUtil.roundNumber(totalWidth, 0));
		RowData.curentTR.find("[name$=height]").val(ajaxUtil.roundNumber(totalHeight, 0));	
		
		RowData.minOrder = eval(RowData.minOrder); // 17*tcol
	}
	
	function customiseS1(ovl){		
		var col, row;
		var json;
		if(RowData.customData==''){
			col = 2; row = 2;
			RowData.customData = mfgUtil.buildDefaultData(col, row);
		}
		json = eval(RowData.customData);
		col = json.colw.length;
			
		RowData.customWidth = RowData.width + ovl[col-1];
		//alert("RowData.customWidth = "+RowData.customWidth);
		for(var i=0; i<col; i++){
			row = json.rowh[i].length;
			RowData.customHeight[i] = RowData.height - 35; // (46 handle poll width)
			switch(row-1){
				case 0: RowData.customHeight[i] -= 4; break;				
				case 1: RowData.customHeight[i] -= (10); break;
				case 2: RowData.customHeight[i] -= (13); break;
				case 3: RowData.customHeight[i] -= (15); break;
				case 4: RowData.customHeight[i] -= (18); break;
				case 5: RowData.customHeight[i] -= (21); break;
			}
			//alert("RowData.customHeight["+i+"] = "+RowData.customHeight[i]);
		}	
		// Have to minus out 1st.
		//RowData.customHeight = RowData.height - 35 - 66 - ((row-1)*12);
	}
	
	function customiseS2toS4(ovl){		
		var col, row;
		var json;
		
		if(RowData.customData==''){
			col = 2; row = 2;
			RowData.customData = mfgUtil.buildDefaultData(col, row);			
		}
		json = eval(RowData.customData);
		col = json.colw.length;
			
		RowData.customWidth = RowData.width + ovl[col-1];
		for(var i=0; i<col; i++){
			row = json.rowh[i].length;
			RowData.customHeight[i] = RowData.height - 35 - 66 - ((row-1)*12); 
		}	
		// Have to minus out 1st.
		//RowData.customHeight = RowData.height - 35 - 66 - ((row-1)*12);
	}		