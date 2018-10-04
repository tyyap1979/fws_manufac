	var RowData = {
		width: 0,
		height: 0,
		customWidth: 0,
		customHeight: new Array(),
		customFormula: '',
		customData: ''
	}

	function TransUtilClass(){
		this.init();
	}
	
	TransUtilClass.prototype = {
		tableHeight: 400,
		ratio: 0,
		mmDecimalToInch: [],
		_m:'',
		
		init: function(){			
			
		},	
				
		roundNumber: function(rnum, rlength) { // Arguments: number to round, number of decimal places
  			return Math.round(rnum*Math.pow(10,rlength))/Math.pow(10,rlength);  
		},
		
		addGroup: function(){
			var trLast = $('tbody[id=transtdEntryTableBody]').find("tr:last");
			var grpCount = $('tbody[id=transtdEntryTableBody]').find("tr").size();
			var trGroup = transUtil.getGroupElement();
			
			grpCount = $('tbody[id=transtdEntryTableBody]').find("[name=chkDetailGroup]").size();
			
			if(grpCount==0){
				$(trGroup).insertBefore($('tbody[id=transtdEntryTableBody]').find("tr:first"));
			}else{
				$(trGroup).appendTo($('tbody[id=transtdEntryTableBody]'));
			}			
		},
		
		getGroupElement: function(){
			var rowTd = $('#transtdClone').find("td");
			var trGroup = "";
			trGroup = "<tr><td><input name='chkDetailGroup' type='checkbox'>" +
			"<input type='hidden' name='transtdgrprow-status' value='N' />" +
			"<input type='hidden' name='transtdgrpgroupid' value='' />" +
			"<input type='hidden' name='transtdgrpgrpcode' value='' />" +
			"</td>"+
			"<td style='background-color: #A9E2F3;' colspan='"+(rowTd.size()-1)+"'><b>Group Name:</b> <input type='text' size='80' name='transtdgrpgrpdesc' /></td></tr>";
			
			return trGroup;
		},
		
		createGroup: function(json){
			var subr = json.SUBR1.data;		
			var md = json.SUBR1.md;		
			
			var status;
			var mainArray;
			var printString;
			var grpcode, grpdesc, groupid;
			var currentGroupCode;
			
			var trGroup = transUtil.getGroupElement();
			var addGroupEle;   
			var currentTr;
			for(var i=0;i<subr.length; i++){
				mainArray = subr[i];
				printString = "";
				
				/*
				for (key in mainArray[0]) {     	      				
	       			printString += (key +":"+mainArray[0][key]) + '\n';   	        			 			  	       				       			 			        	
				}
				alert(printString);
				*/
				
				grpcode = mainArray[0][md+"grpcode"];
				grpdesc = mainArray[0][md+"grpdesc"];
				groupid = mainArray[0][md+"groupid"];
				currentTr = null;
				$('tbody[id=transtdEntryTableBody]').find("tr").each(function(i){
					currentGroupCode = $(this).find("input[name$=grpcode]").val();
					if(grpcode==currentGroupCode){
						currentTr = $(this);
						return false;
					}
				});
				addGroupEle = $(trGroup);							
				addGroupEle.find("[name$=row-status]").val("U");
				addGroupEle.find("[name$=grpcode]").val(grpcode);
				addGroupEle.find("[name$=grpdesc]").val(grpdesc);
				addGroupEle.find("[name$=groupid]").val(groupid);     
				addGroupEle.insertBefore(currentTr);    				
			}		
		},
		
		updateGroup: function(){
			var currentGroup;
			var grpCode = "";
			var groupCount = 0; 
			$('tbody[id=transtdEntryTableBody]').find("tr").each(function(i){
				currentGroup = $(this).find("[name=chkDetailGroup]");
				if(currentGroup.size()==1){
					groupCount += 1;
					grpCode = groupCount;
					$(this).find("input[name$=grpcode]").val(grpCode); // Group Row
				}else{
					// No group means may be is product
					if(grpCode!=''){
						if($(this).find("input[name$=row-status]").val()=='D'){
							$(this).find("input[name$=grpcode]").val("");
						}else{
							$(this).find("input[name$=grpcode]").val(grpCode);
						}
					}
				}
			});
		}
	}


var transUtil;
$(document).ready(function(){transUtil = new TransUtilClass();});