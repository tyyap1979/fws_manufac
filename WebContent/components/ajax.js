	var startTime;

	function Util(){
		this.init();
	}
	
	Util.prototype = {
		init: function(){
			this.buildOverlayAndPanel();			
			this.buildValidate();			
			this.attachAutoSuggestEvent($(document));
			this.attachDatePicker($(document));			
			$(".tabs").tabs();
			//this.dynamicSelectLoad();								
		},
		
		attachDatePicker: function(cloneObject){
			// trigger when create clone row	
			
				
			cloneObject.find(".datepicker").each(function(i){
				$(this).datepicker();
				$(this).datepicker("option", "dateFormat", 'yy-mm-dd');	
			});
			/*
			cloneObject.find( ".datepicker" ).datepicker( "destroy" );
			cloneObject.find( ".datepicker" ).datepicker();
			cloneObject.find( ".datepicker" ).datepicker("option", "dateFormat", 'yy-mm-dd');
			*/				
		},

		resetGlobalComponentEvent: function(cloneObject){	
			ajaxUtil.attachAutoSuggestEvent(cloneObject);		
		}, 
		
		attachAutoSuggestEvent: function(obj){		
			obj.find("[class*=autosuggest]").each(function(i){
				var parentTr = $(this).parents("tr");
				var className = $(this).attr("class");
				var trId = ""; 
				if(parentTr){
					trId = parentTr.attr("id");					
				}	
								
				if(trId.indexOf("Clone")==-1 && (className.indexOf("ui-autocomplete-input")==-1)){																					
					var splitClass = className.split(" ");	
					for(var i=0; i<splitClass.length; i++){
						if(splitClass[i].indexOf("autosuggest")!=-1){
							className = splitClass[i];
						}
					}						
					var page = className.substring(0, className.indexOf("-")) + ".htm?return=json&action1=action.auto.suggest";			
					var keyField = $(this).attr("name");					
					keyField = keyField.substring(0, keyField.lastIndexOf("_value"));								
					
					$(this).autocomplete({
						source: page,
						minLength: 2,
						highlight: true,
						change: function(event, ui) {				
							var value = $(this).val();												
							if(value==""){								
								$(this).parents("tr").find('input[name='+keyField+']').val("");
							}												
						},
						select: function(event, ui) {																										
							$(this).parents("tr").find('input[name='+keyField+']').val(ui.item.key);		
							if(typeof(processAfterAutoComplete)=='function'){
								processAfterAutoComplete(ui.item);
							}		
						}
					});
				}
			});
		},
		
		dynamicSelectLoad: function(){
			var loadMod="";								
			$(document).find("select[class*=dynamicselect]").each(function(i){							
				var className = $(this).attr("class");				
				var obj = $(this);				
				var splitClass = className.split(" ");	
				for(var i=0; i<splitClass.length; i++){
					if(splitClass[i].indexOf("dynamicselect")!=-1){
						className = splitClass[i];
						break;
					}
				}										
				mod = className.substring(0, className.lastIndexOf("-"));
				if(loadMod.indexOf(mod+",")==-1){
					loadMod += mod + ",";							
					var url = "select.htm";
					var query = "return=json&action1=action.dynamic.select&mod="+mod;				
					$.ajax({
					   type: "POST",				   
					   url: url,				   
					   data: query,				   
					   cache: false,			 
					   dataType: "json",				   				   
					   success: function (json) {					   		
					   		obj = $(document).find("select[id="+obj.attr("id")+"]");					   		
					   		obj.empty();				   			   	
					   		obj.append($(json.options));
					  	}	
				   });					
				}
			});
		},

	 	customTrim: function(val){
	 		if(val){
	 			val = val.replace(/(^\s*)|(\s*$)/g, "");
	 		}
	 		return val;
	 	},
		
		bindLink: function(){
			$("a[href!='#']").bind('click', (function(event){				
				if($(this).attr("href").indexOf("#")==-1 && $(this).attr("name")!="fb_share"){						
					ajaxUtil.showPanel("", $('#js-processing-request').html(), null);
				}
			}));
			
			$(window).unload(function() {
				setTimeout(function (){
					ajaxUtil.hidePanel();
				},3000);
			  
			});
			
		},
		
		postJsonQuery: function(url, query, callback, fallback){				
			if(!url) url = window.location.pathname;					
			if(url.substr(url.length-5,5)=='.html'){
				url = url.substring(0, url.length-1);
			}
			
			  $.ajax({
				   type: "POST",				   
				   url: url,				   
				   data: query,				   
				   cache: false,			 
				   dataType: "json",				   				   
				   success: callback
			   });
		},
		
		updater: function(objectName, url){								
			$.ajax({
					type: "POST",					
					url: url,					
					success: function(html){		
						//$("#"+objectName+" #ajaxOverlayBody").html(html);						
						$("#"+objectName).html(html);															   
					    $("#"+objectName).dialog('open');	
					    ajaxUtil.hidePanel();
					}					
				});	
		},		
		
		getForm: function(element){			
			var formObject = $(element);
									
			while(formObject.get(0).tagName!='FORM'){		
				formObject = formObject.parent();
			}
			
			return formObject;
		},
		
		buildValidate: function(){			
			var requireMsg = "<span id='reqmark'>*</span>";			
			var formItemParent;
			$('.validateForm').find("input[class^='validate'], select[class^='validate'], textarea[class^='validate']").each(function(i){
				if($(this).attr("type")!="radio"){		
					formItemParent = $(this).parent().prev("th");		
					formItemParent.find("#reqmark").remove();
					$(requireMsg).appendTo(formItemParent);
					
				}
			});
			
		},
		
		validateForm: function(element, formElement){				
			var formObject;
			if(formElement==null){
				formObject = ajaxUtil.getForm(element);
			}else{				
				formObject = formElement;
			}
						
			var formName = '';
	        var formType = '';
	        var formValue = '';
	        var formClass = '';
	        var formItemParent;
	        var validationName = '';
	        var validationMsg = '';
	        var isValid = true;
	        
		    //var checkFieldsString = checkFields.toString();    
	        formObject.find("input, select, textarea").each(
	        	function(i){
	        		formItemParent = $(this).parent();
	        		formClass = $(this).attr("class");
	        		formName = $(this).attr("name");
	        		formType = $(this).attr("type");
	        		formValue = $(this).val();
	        			    
	        		if(formClass!='' && formType!='radio'){	   	        									
	        			if(formItemParent.find('#display-msg').size()==0){
	        				$("<div id='display-msg'></div>").appendTo(formItemParent);
	        			}else{
	        				formItemParent.find('#display-msg').html("");
	        			}
	        			
	        			validationName = formClass.split(' ');
	        			validationMsg = "";
	        			for(var i=0; i<validationName.length; i++){
	        				if(validationName[i]=='required'){	        								
	        					if(formValue==''){
	        						validationMsg = $('#js-required').html();
	        						isValid = false;
	        						break;        						
	        					}
	        				}else if(validationName[i].indexOf('match-with')!=-1){
	        					var match_with_field = validationName[i].substring('match-with'.length+1);     
	        					
	        					//alert(formValue + " = " + formObject.find("[name='"+match_with_field+"']").val());   					
	        					if(formValue!=formObject.find("[name='"+match_with_field+"']").val()){
	        						validationMsg = $('#js-password-field-match').html();
	        						isValid = false;
	        						break;        	
	        					}        					
	        				}        				
	        			}        			
	        			formItemParent.find('#display-msg').html(validationMsg);
	        		}
				}
			);
	   
			return isValid;
		},

		formData2QueryString: function(docForm, unCheckValue) {
	        var strSubmit       = '';
	        var formObject;
	        
	        var formName = '';
	        var formType = '';
	        var formValue = '';	        
	        var formObject = $(docForm);	     
	        	        	        			
	        formObject.find("input, select, textarea").each(
	        	function(i){	        		
	        		formName = $(this).attr("name");	        		
	        		formValue = $(this).val();	   	        		
	        		if (this.tagName.toLowerCase() == "input") {
	        			
					    formType = $(this).attr("type");   //returns radio or text (the attr type)
					} else {
					    formType = this.tagName.toLowerCase();        //returns select (the element type)
					}
					//alert(formName +"["+formType+"] : " + formValue);	        	
	        		switch (formType) {
		                case 'text':
		                case 'select':
		                case 'hidden':
		                case 'password':
		                case 'textarea':               											
		                    strSubmit += formName + '=' +  encodeURIComponent(ajaxUtil.customTrim(formValue)) + '&';
							break;
						case 'checkbox':
							if(unCheckValue){	
								if($(this).attr("checked")){
									strSubmit += formName + '=Y&';
								}else{
									strSubmit += formName + '=N&';
								}							
								//strSubmit += formName + '=' + formValue + '&';	
							}else{
								if($(this).attr("checked")){
									strSubmit += formName + '=' + ajaxUtil.customTrim(formValue) + '&';
								}
							}
							break;
						case 'radio':																
							if($(this).is(":checked")){		
								formValue = $(this).attr("id").replace(formName,"");																				
								strSubmit += formName + '=' + ajaxUtil.customTrim(formValue) + '&';	
							}												
							break;	                
					}
					
				}
			);			
			
			return strSubmit;
		},
		
		reverseConvert: function(str){
			var converted = "";
			var temp;	
			var i=0;
			var endposition = 0;
			
			if(str.indexOf('&#x')!=-1){
				while((i = str.indexOf('&#x', i))!=-1){			
					endposition = str.indexOf(';', i+2);
					temp = str.substring(i+3, endposition);					
					converted = String.fromCharCode(parseInt(temp, 16));			
					str = str.substring(0, i) + converted + str.substring(endposition+1, str.length);					
				}
			}else{			
				while((i = str.indexOf('&#', i))!=-1){			
					endposition = str.indexOf(';', i+2);
					temp = str.substring(i+2, endposition);
					converted = String.fromCharCode(temp);			
					str = str.substring(0, i) + converted + str.substring(endposition+1, str.length);					
				}
			}
			return str;
		},
	
		convertToEntities: function(elementValue) {
			var tstr = elementValue;
		  	var bstr = '';		  
		  
		  	if(tstr!=null){		  	
			  	for(var i=0; i<tstr.length; i++){
			    	if(tstr.charCodeAt(i)>127){
			      		bstr += '&#' + tstr.charCodeAt(i) + ';';			      						 
			    	}else{
			      		bstr += tstr.charAt(i);
			    	}
			  	}	
		  	}
			
		  	return (bstr);
		},
		
		commaFormatted: function(amount){
			if(amount==0) return 0;
			var delimiter = ","; // replace comma if desired
			var a = amount.split('.',2);
			var d = a[1];
			var i = parseInt(a[0]);
			if(isNaN(i)) { return ''; }
			var minus = '';
			if(i < 0) { minus = '-'; }
			i = Math.abs(i);
			var n = new String(i);
			var a = [];
			while(n.length > 3)
			{
				var nn = n.substr(n.length-3);
				a.unshift(nn);
				n = n.substr(0,n.length-3);
			}
			if(n.length > 0) { a.unshift(n); }
			n = a.join(delimiter);		
			if(d.length < 1) { amount = n; }
			else { amount = n + '.' + d; }
			amount = minus + amount;
			return amount;
		},
	
		showPanel: function(hearder, body, buttons){								
			var d = new Date();
			startTime = d.getTime();	
			
			try{					
				//$('#dialog #ajaxOverlayHeader').html(hearder);				
				$('#dialog').html("<div class='panelMessage'>"+body+ "</div>");
				
				if(buttons){
					$("#dialog").dialog('option', 'buttons', buttons);
				}
				$("#dialog").dialog('open');
			}catch(e){
				alert(e);
			}		
		},
		
		updatePanelMessage: function(body, buttons){
			if(buttons){
				$('#dialog').dialog('option', 'buttons', buttons); 
			}							
			$('#dialog').html("<div class='panelMessage'>"+body+ "</div>");				
		},
	
		hidePanel: function(){				
			var d = new Date();
			var dif = 800 - (d.getTime() - startTime);				
			if(dif > 0){				
				setTimeout(function (){
					$("#dialog").dialog('close');
				},dif);
			}else{
				$("#dialog").dialog('close');
			}		
				
		},
	
		buildOverlayAndPanel: function(){			
			//var dialogContent = '<table width="100%" border="0" cellpadding="0" cellspacing="0" height="100%"><tbody><tr height="10%"><td id="ajaxOverlayHeader"></td></tr><tr valign="top"><td id="ajaxOverlayBody"></td></tr><tr align="right" height="10%" valign="bottom"><td id="ajaxOverlayFooter"></td></tr></tbody></table>';
			var dialogContent = "";
			var dialog = '<div id="dialog" title="&nbsp;">'+dialogContent+'</div>';
			$(dialog).appendTo("body");		
			$("#dialog").dialog({ autoOpen: false, 'modal': true });			
		},

		loadUploader: function(uploadPage, params, fileType, fileTypeDesc, fileSizeLimit) {
			if(!fileType){
				fileType = "*.*";
			}				
			if(!fileTypeDesc){
				fileTypeDesc = "All Files";
			}
			if(!fileSizeLimit){
				fileSizeLimit = "1 MB";
			}
			params['return'] = 'json';
			if(swfu){
				//swfu.setPostParams(params);
			}else{
				var settings = {
					flash_url : "/components/swfupload/swfupload2.5b3.swf",
					upload_url: uploadPage,			
					post_params: params,
					file_size_limit : fileSizeLimit,
					file_types : fileType,
					file_types_description : fileTypeDesc,
					file_upload_limit : 50,
					file_queue_limit : 0,
					custom_settings : {
						progressTarget : "fsUploadProgress",
						cancelButtonId : "btnCancel"
					},
					debug: false,
			
					// Button Settings
					button_placeholder_id : "spanButtonPlaceholder",		
					button_text: "<span class='uploadButton'>Upload</span>",
					button_text_style : ".uploadButton { font-weight: bold;text-decoration: underline; font-family: verdana; font-size: 12px; }", 
					button_text_top_padding: 0,
					button_text_left_padding: 0,
					button_width: 60,
					button_height: 18,
					button_cursor: SWFUpload.CURSOR.HAND,
			
					// The event handler functions are defined in handlers.js
					swfupload_loaded_handler : swfUploadLoaded,
					file_queued_handler : fileQueued,
					file_queue_error_handler : fileQueueError,
					file_dialog_complete_handler : fileDialogComplete,
					upload_start_handler : uploadStart,
					upload_progress_handler : uploadProgress,
					upload_error_handler : uploadError,
					upload_success_handler : uploadSuccess,
					upload_complete_handler : customUploadComplete,
					queue_complete_handler : customQueueComplete
				};
				swfu = new SWFUpload(settings);									
				//$("<span id='spanButtonPlaceholder'></span>").appendTo('#mySpanButtonPlaceholder');				
			}								
						
			
		},
		
		cloneObject: function(destObj){
			var newClone = destObj.clone(true);
			var select = destObj.find("select");
       		var newSelect = newClone.find("select");
       		var selvals = [];
       		select.each(function(i){
       			selvals.push($(this).val());		       			
       		});    				 
       		newSelect.each(function(i){
       			$(this).val(selvals.shift());		       			
       		});
       		
       		return newClone;
		},
		
		roundNumber: function(rnum, rlength) { // Arguments: number to round, number of decimal places
			var num = Math.round(rnum*Math.pow(10,rlength))/Math.pow(10,rlength);
			num = num.toFixed(rlength);		
  			return num;  
		}
	}
	
	var swfu;
	var ajaxUtil;
	
	$(document).ready(function(){ajaxUtil = new Util();});
	