	var AdminConfig = {
		overlayOpacity: 0.8,   // controls transparency of shadow overlay
		overlayMinTime: 500, 

		// element id
		sortableField: 			'sortableField',	
		actionUpload:			'action.upload',
		actionSave: 			'action.save',		
		actionAddRecord:		'action.addrecord',
		actionShowMoreSearch: 	'action.show.more.search',
		checkAllAction: 		'action.check.all',
		checkNoneAction: 		'action.check.none',
		 
		// Request & JSON
		sortName: 				'sortName',  
		sortDirectionName: 		'sortDirection',		
	
		// Action Value
	    actionName: 			'action1',         
		actionCreate: 			'action.create',	
		actionUpdate: 			'action.update',	
	    actionRetrieveEdit: 	'action.retrieve.edit', // Element ID & Action Value
	    actionRetrieveCopy: 	'action.retrieve.copy', // Element ID & Action Value
		actionDelete: 			'action.delete',		// Element ID & Action Value
		actionSearch: 			'action.search',		// Element ID & Action Value	
		
		// Form ID	
		entryForm: 				'entryForm',
		searchForm: 			'searchForm',
		listingForm: 			'listingForm',				
		sortableColumnAscClass: 'ui-state-active',
		sortableColumnDescClass: 'ui-state-active'
	}	
	


Admin.prototype = {
    // initialize()
    // Constructor runs on completion of the DOM loading. Calls updateImageList and then
    // the function inserts html at the bottom of the page which is used to display the shadow 
    // overlay and the image container. 
    //
	startTime: 				undefined,	
	sortBy: 				new Array(),				// Sort Field Name
	sortByDirection: 		new Array(),	// Asc Desc	
	activeModuleId:			'',						
	currentElement: undefined,
	activeEditTR: undefined,	
	
	initialize: function() {  		
		//var objBody = $$('body')[0];
		var _this = this;			
		_this.checkSession();	
		_this.processListingEvent();
		_this.commonEventListener();	
		this.buildPanel();	
		$(".validateForm").validationEngine();						
		this.buildSorting();
	},		

	checkSession: function(){
		var query = "return=json&action1=checksession";
		ajaxUtil.postJsonQuery(null,query,function(json){			
			if(json.redirect){
				window.location=json.redirect;
				return;
			}	
		}, null);
	},
	
	processSearchEvent: function(){
		/*
		$('div[class=searchDiv]').find("form").each(function(i){
			$(this).bind('submit', (function(event){
				var element = event.target;
				alert("element.id = "+element.id);
			}));
		});
		*/
		$('div[class=searchDiv]').each(function(i){		
			$(this).bind('click', (function(event){
				var element = event.target;
				
				if (element.id==AdminConfig.actionShowMoreSearch) {						
					var tempValue;
					var searchMoreOption = $(this).find("#searchMoreOption");
					
					if(searchMoreOption.is(":visible")){
						searchMoreOption.hide();					
					}else{
						searchMoreOption.show();
					}
					tempValue = element.innerHTML;
					element.innerHTML = element.rel;
					element.rel = tempValue;	
					event.preventDefault();	return;
		        }
		        
		        if (element.id==AdminConfig.actionSearch) {	
		        	var id = $(this).attr("id");
		        	
					adminUtil.activeModuleId = id + 'Module';																
					adminUtil.searchRecord(element);				
					event.preventDefault(); return;
		        }
			}));
			
			// Auto Search
			if(typeof(autoSearch)!='undefined' && autoSearch==false){
			
			}else{
				var id = $(this).attr("id");		        	
				adminUtil.activeModuleId = id + 'Module';		
				//adminUtil.searchRecord();	
			}
		});
	},
	
	commonEventListener: function(){		
		$("#btnSubmit").bind('click', (function(event){			
			var element = event.target;
			adminUtil.commonJsonSubmit(ajaxUtil.getForm($(element)));
			event.preventDefault(); return;
		}));
		
		
		$("#btnSave").click(function(event){			
			var entryScreen = $("#"+adminUtil.activeModuleId+"Entry");
			var gotError = $.validationEngine.submitValidation(entryScreen.find('.validateForm'));				
			if(!gotError){				
				adminUtil.saveRecord();				
			}
			event.preventDefault();	return;
		});
		
		$("#btnCancel").click(function(event){
			adminUtil.hideEntryEvent();
			event.preventDefault();	return;
		});
		
		$("#btnPrint").click(function(event){
			// Set data to Textarea
			var myTextarea;
			$("textarea[class=editor]").each(function(i){						
				myTextarea = nicEditors.findEditor($(this).attr("id"));
				if(myTextarea){								
					$(this).val(myTextarea.getContent());		
				}				
			});
			var popup=window.open("print.htm",'printWindow','height=600,width=800,menubar=yes,scrollbars=yes');
			event.preventDefault();	return;
		});
	},
	
	processListingEvent: function(){		
		$('[class$=listingTable]').each(function(i){					
			$(this).bind('click', (function(event){
				var element = event.target;
				currentElement = element;				
		        
				if (element.id==AdminConfig.actionDelete) {					
					var id = $(this).attr("id");		        
					
					adminUtil.activeModuleId = id + 'Module';					
					var dialog_buttons = {};
					dialog_buttons["Yes"] = function(){														
						adminUtil.getModuleElement(AdminConfig.listingForm).find("input[name=action1]").val(AdminConfig.actionDelete);						
						adminUtil.deleteRecord();
						return;
					}; 
					dialog_buttons["No"] = function(){ 							
						ajaxUtil.hidePanel();
						return;
					};	
							
					
					ajaxUtil.showPanel("", "Confirm Delete?", dialog_buttons);		
					
					event.preventDefault();
					return;
		        }
	
				if (element.id==AdminConfig.actionAddRecord) {								
					if(typeof(beforeAdd)=='function'){
						beforeAdd();
					}				
					var id = $(this).attr("id");					
					//id = id.substring("listingTable".length, id.length);
					
					adminUtil.activeModuleId = id + 'Module';		
																	
					$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionCreate);																
					adminUtil.formReset();	
					/*
					if("mfg_transactionModule"==adminUtil.activeModuleId 
					|| "galleryModule"==adminUtil.activeModuleId
					|| "materialModule"==adminUtil.activeModuleId
					|| "imageDetailModule"==adminUtil.activeModuleId
					|| "customerprofileModule"==adminUtil.activeModuleId
					){
						
					}else{
						adminUtil.resetClone(true);
					}
					*/
					adminUtil.resetClone(false);		
					adminUtil.showEntryEvent();											
					event.preventDefault();
					return;
		        }
		        
		        if(element.id=='action.clone.add'){				        		        			        		
		        	adminUtil.rowClone($(element), true);		        	
		        	event.preventDefault();
					return;
		        }
		        
		        if(element.id=='action.clone.add.new'){				        		        			        		
		        	adminUtil.rowClone($(element), false);		        	
		        	event.preventDefault();
					return;
		        }
		        
		        if(element.id=='action.clone.delete'){			
		        	var parentTable = $(element).parent();
											
					while(parentTable.get(0).tagName!='TABLE'){			
						parentTable = parentTable.parent();
					}	
					
		        	var id = parentTable.attr("id"); 	
		        	var module = id.substring(0, id.indexOf('EntryTable'));		        	
			        var moduleEntryTableBody = parentTable.find("#"+module+"EntryTableBody"); //$("#"+module+"EntryTableBody")
			        var moduleEntryTableHead = parentTable.find("thead");
			        var moduleEntryClone = moduleEntryTableHead.find("[id="+module+"Clone]");	
			       	var rowType = moduleEntryClone.get(0).tagName;	
			       	
			       	
					var checkbox;			
				   				        	       
					moduleEntryTableBody.children(rowType).each(function(i){				
						checkbox = $(this).find('input[type=checkbox]');
						$.validationEngine.closePrompt($(this));	
						if(checkbox.attr("checked")){
							var status = $(this).find('input[name$=row-status]').val();							
							if(status=='N'){								
								$(this).remove();							
							}else{
								$(this).find('input[name$=row-status]').val('D');
								$(this).hide();		
							}
						}
					});						
					
					if(typeof(afterCloneDelete)=='function'){
						afterCloneDelete();
					}
					adminUtil.updateSorting(moduleEntryTableBody);		
					event.preventDefault();				
					return;
				}		        
		        
		        if (element.id==AdminConfig.actionRetrieveEdit) {		        
		        	var TRObject = $(element).closest('tr');
					adminUtil.activeEditTR = TRObject;																						
					adminUtil.editPopUp(element);					
					event.preventDefault();
					return;
		        }
		        
		        if (element.id==AdminConfig.actionRetrieveCopy) {
					adminUtil.activeModuleId = $(this).attr("id") + 'Module';
		        	var formName = "#"+adminUtil.activeModuleId+" #"+AdminConfig.listingForm;		       
		        	var checkbox = $(formName).find('input[type=checkbox]:checked');	
		        			        	
		        	if(checkbox.size()>1){
		        		alert("Please select only ONE record for copying.");
		        	}else{
			        	checkbox.each(function(i){
			        		if($(this).attr("checked")){	
			        			var TRObject = $(element).closest('tr');
								adminUtil.activeEditTR = TRObject;																						
								adminUtil.copyPopUp($(this));
			        		}						
						});
		        	}
					event.preventDefault();
					return;
		        }
		        
		        if(typeof(processListingEventCustom)=='function'){
		        	adminUtil.activeModuleId = $(this).attr("id") + 'Module';
					processListingEventCustom(element.id);				
				}		
		        /*
		        if (element.id==AdminConfig.sortableField) {	
		        	var id = $(this).attr("id");		        					
		        	adminUtil.activeModuleId = id + 'Module';	        	
					adminUtil.updateSorting(element);
					adminUtil.searchRecord(element);				
					event.preventDefault();
					return;
		        }
		        */
			}));
		});
	},
	
	updateSorting: function(objTBody){
		objTBody.children("tr").each(function(i) {
			$(this).find("input[name$=position]").val(i+1);					
		});
	}, 
	
	buildSorting: function(){
		var fixHelper = function(e, ui) {
			ui.children().each(function() {
				$(this).width($(this).width());
			});
			return ui;
		};
		
		var sortStopHandle = function (e, ui) {
			var element = e.target;
		
			$("#"+element.id).children("tr").each(function(i) {
				$(this).find("input[name$=position]").val(i+1);					
			});
			return ui;
		};
					
		
		$(".sortable tbody").sortable({helper: fixHelper, stop: sortStopHandle});		
	},
	
	addCloneID: function(obj){
		var time = new Date().getTime();
		var name = "";
		obj.find("input, select, textarea").each(function(i){
			name = $(this).attr("name");
			$(this).attr("id", new Date().getTime() + "-" + name);
		});
	},
	
	rowClone: function(element, deriveLastRow){
		var parentTable = element.parents("table");
			
		
       	var id = parentTable.attr("id"); 	
       	var module = id.substring(0, id.indexOf('EntryTable'));		        	
        var moduleEntryTableBody = parentTable.find("#"+module+"EntryTableBody"); //$("#"+module+"EntryTableBody")
        var moduleEntryTableHead = parentTable.find("thead");
        var moduleEntryClone = moduleEntryTableHead.find("[id="+module+"Clone]");			        
       	var newClone;
		
		// If selected then clone selected						
		var checkedEle = moduleEntryTableBody.find('input[name=chkDetail]:checked');					
		if(checkedEle.size()>0){
			moduleEntryClone = checkedEle.parents("tr");
		}else{						
			if(deriveLastRow && moduleEntryTableBody.children("tr").size()>0){						
				moduleEntryClone = moduleEntryTableBody.children("tr:last");						
				if(moduleEntryClone.find("[name=chkDetailGroup]").size()>0){
					moduleEntryClone = moduleEntryTableHead.find("[id="+module+"Clone]");
				}							
			}				
		}	
   		newClone = moduleEntryClone.clone(true);

   		var select = moduleEntryClone.find("select");
   		var newSelect = newClone.find("select");
   		var selvals = [];
   		var selName = [];
   		select.each(function(i){		       			
   			selvals.push($(this).val());		
   			selName.push($(this).attr("name"));
   		});    				 
   		newSelect.each(function(i){
   			$(this).val(selvals.shift());
   			$(this).attr("name", selName.shift());		 		       			
   		});        	
   		newClone.attr("id","");		
   		       		
   		newClone.find('input[type=checkbox]').attr("checked", false);
   		newClone.find('input[type=text]').val('');
   		newClone.find('input[type=hidden]').val('');
		newClone.find('input[name$=row-status]').val('N');
		newClone.find('img').attr('src','');
		
		adminUtil.addCloneID(newClone);				
	
		newClone.show();
		if(checkedEle.size()>0){			
			newClone.insertAfter(moduleEntryClone);
			moduleEntryClone.find("input[name=chkDetail]:checked").attr("checked", false);
		}else{			
        	newClone.appendTo(moduleEntryTableBody);
        }    
                			
        ajaxUtil.resetGlobalComponentEvent(moduleEntryTableBody);        
        adminUtil.updateSorting(moduleEntryTableBody);
        return newClone;
	},
		
	getModuleElement: function(elementId){				
		return $("#"+this.activeModuleId).find('#'+elementId);
	},	
	
	buildPanel: function(){				
		$('.dialogView').each(function(i){ 					
			try{												
				var width = $(this).width();							
				//width = ($.browser.msie)?maxWidth:"auto";

				if($(this).css("vertical-align")=='top'){
					$(this).dialog({ autoOpen: false, 
					'position':'top', 
					'modal': true, 
					'width': width,					
					'minHeight': 500,
					//'height': $(this).height(),
					'close': function(ev, ui) { $(".formError").remove();}
					});
				}else{				
					$(this).dialog({ 
					autoOpen: false, 
					'modal': true, 
					'width': width, 					
					'minHeight': 500,
					//'height': $(this).height(),
					'close': function(ev, ui) { $(".formError").remove();}
					});
				}		
				
									
			}catch(e){
				alert(e);
			}
		 });		
	},
	
	showPanelSuccess: function(body){		
		ajaxUtil.showPanel(null,"<div id='overlayMsg_progBod_text'>"+body+"</div>", null);				
	},
	
	hideOverlay: function(){		
		var d = new Date();
		var dif = AdminConfig.overlayMinTime - (d.getTime() - startTime);		
		if(dif > 0){
			setTimeout(function (){
				ajaxUtil.hidePanel();
			},dif);
		}else{
			ajaxUtil.hidePanel();
		}
		
	},
	
	checkAll: function(form){	
		var trid;
		form.find("input[type='checkbox']").each(function(i){
			trid = $(this).parents("tr").attr("id");			
			if(!adminUtil.endsWith(trid,"Clone")){
				$(this).attr("checked", true);
			}
		});		
	},
	
	endsWith: function (chkString, s) {
  		return chkString.length >= s.length && chkString.substr(chkString.length - s.length) == s;
	},
	
	
	checkNone: function(form){	
		form.find("input[type='checkbox']").each(function(i){
			$(this).attr("checked", false);
		});	
	},
	
	// Retrieve Data When click on edit
	commonJsonSubmit: function(form){							
		var query = "return=json" + "&" + ajaxUtil.formData2QueryString(form,false);					
		ajaxUtil.showPanel('', $('#js-processing-request').html(), null);
		ajaxUtil.postJsonQuery(window.location.pathname, query, adminUtil.handleCallback, adminUtil.handleFallback);				
	},
		
	// Retrieve Data When click on edit
	editPopUp: function(target){	
		adminUtil.formReset();		
		adminUtil.resetClone(false);						
		var query;
		if(target){
			query = "return=json" + "&" + $(target).attr("rel") + "&" + AdminConfig.actionName + "=" + AdminConfig.actionRetrieveEdit;
		}else{
			query = "return=json" + "&" + AdminConfig.actionName + "=" + AdminConfig.actionRetrieveEdit;
		}		
		
		ajaxUtil.showPanel('', $('#js-processing-request').html(), null);
		ajaxUtil.postJsonQuery(window.location.pathname, query, adminUtil.handleCallback, adminUtil.handleFallback);				
	},
	
	// Retrieve Data When click on edit
	copyPopUp: function(target){	
		adminUtil.formReset();		
		adminUtil.resetClone(false);						
		var query = "return=json" + "&" + $(target).attr("name") + "=" + $(target).val() + "&" + AdminConfig.actionName + "=" + AdminConfig.actionRetrieveCopy;					
		ajaxUtil.showPanel('', $('#js-processing-request').html(), null);
		ajaxUtil.postJsonQuery(window.location.pathname, query, adminUtil.handleCallback, adminUtil.handleFallback);				
	},
	
	showEntryEvent: function(moduleName, buttons){			
		$("#searchDiv").hide();
		$("#entryDiv").show();
		$("#entryButtonBar").show();
		$(".entryfocus").focus();
	},
	/*
	showEntryEvent: function(moduleName, buttons){	
		var entryScreen;
		
		if(moduleName){			
			entryScreen = $("#"+moduleName);								
		}else{									
			entryScreen = $("#"+adminUtil.activeModuleId+"Entry");
		}				
		
		var activeModuleId = adminUtil.activeModuleId;
		var dialog_buttons = {};	
		
			
		if(buttons!=null){
			dialog_buttons = buttons;
		}else{			
			if(typeof(customEntryButton)!='undefined'){		
				dialog_buttons = customEntryButton;
			}					
			dialog_buttons["Save"] = function(){																				
				var gotError = $.validationEngine.submitValidation(entryScreen.find('.validateForm'));				
				if(!gotError){
					adminUtil.activeModuleId = activeModuleId;
					adminUtil.saveRecord();
				}
			};	
		}
		
		entryScreen.dialog('option', 'buttons', dialog_buttons);
		entryScreen.dialog('open');			
	},
	*/
	hideEntryEvent: function(moduleName){			
		/*
		if(moduleName){
			entryScreen = $("#"+moduleName);			
		}else{						
			entryScreen = $("#"+adminUtil.activeModuleId+"Entry");
		}	
		entryScreen.dialog("close");
		*/
		$("#searchDiv").show();
		$("#entryDiv").hide();
		$("#entryButtonBar").hide();
	},
	
	deleteRecord: function(){		
		var formName = "#"+adminUtil.activeModuleId+" #"+AdminConfig.listingForm;		
		ajaxUtil.showPanel('', $('#js-processing-request').html(),'');
		var query = "return=json" + "&" + ajaxUtil.formData2QueryString(formName);		
		
		ajaxUtil.postJsonQuery(window.location.pathname, query, this.handleCallback, this.handleFallback);		
	},
	
	refreshSearch: function(){
		var oTable = $('table.sorting').dataTable();
		oTable.fnDraw();
	},
		
	searchRecord: function(element){		
		var searchForm;
		
		//if(!element) element = currentElement;
		/*
		if(element){			
			searchForm = $(element);
												
			while(searchForm.get(0) && searchForm.get(0).tagName!='FORM'){			
				searchForm = searchForm.parent();
			}	
			if(!searchForm){
				alert("No search Form");
			}
		}else{
		*/						
			searchForm = $("#"+adminUtil.activeModuleId).find("#"+AdminConfig.searchForm+"");
		//}
		
		searchForm.find("input[name='action1']").val(AdminConfig.actionSearch);
		
		var sortBy = this.sortBy[adminUtil.activeModuleId];
		var sortByDirection = adminUtil.sortByDirection[adminUtil.activeModuleId];
		
		sortBy = (sortBy)?sortBy:"";
		sortByDirection = (sortByDirection)?sortByDirection:"";
					
		//ajaxUtil.showPanel('', $('#js-processing-request').html(),'');				
		var query = "return=json" + "&" + ajaxUtil.formData2QueryString(searchForm);		
		query = query + AdminConfig.sortName + "=" + sortBy;
		query = query + "&" + AdminConfig.sortDirectionName + "=" + sortByDirection;   	
		
		ajaxUtil.postJsonQuery(searchForm.attr("action"), query, adminUtil.handleCallback, adminUtil.handleFallback);			
	},
		
	saveRecord: function(){				
		if(typeof(customSave)=='function'){
			customSave();			
		}else{
			if(typeof(beforeSave)=='function'){
				beforeSave();			
			}
			
			// Remove NicEdit
			var myTextarea;
			$("textarea[class=editor]").each(function(i){						
				myTextarea = nicEditors.findEditor($(this).attr("id"));
				if(myTextarea){								
					$(this).val(myTextarea.getContent());				
				}				
			});
					
			var	entryForm  = $("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.entryForm+"");
			var detailForm = $("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.searchForm+"");		
			ajaxUtil.showPanel('', $('#js-processing-request').html(), null);
			
			var query = "return=json" + "&" + ajaxUtil.formData2QueryString(entryForm, true);	
			
			if(detailForm.size()>0){			
				query+=ajaxUtil.formData2QueryString(detailForm, true);
			}	
			
			ajaxUtil.postJsonQuery(window.location.pathname, query, adminUtil.handleCallback, adminUtil.handleFallback);
		}				
	},
		
	handleCallback:function(json){					
		var rc = json.rc;	
		var rd = json.rd;
		
		if(json.redirect){
			window.location=json.redirect;
			return;
		}	
						
		if(rc=='9001'){ // No record Found
			adminUtil.showPanelSuccess(rd);			
			adminUtil.getModuleElement('paginationData').html("");
			adminUtil.getModuleElement('viewRecordNumber').html("");
						
			adminUtil.hideOverlay();
		}else if(rc=='9002'){ // Duplicate Key
			adminUtil.showPanelSuccess(rd);
			adminUtil.showEntryEvent();
		}else if(rc=='0'){	// Create Successfully		
			// Add Record to list
			//$('table.sorting').dataTable().fnAddData(json.record);
			//adminUtil.getModuleElement('paginationData').find("tr:first").attr("class", "editedrecord");
			$(unescape(json.record)).prependTo(adminUtil.getModuleElement('paginationData'));
			
			adminUtil.hideEntryEvent();
			adminUtil.showPanelSuccess(rd);
			adminUtil.hideOverlay();
		}else if(rc=='1'){	// Update Successfully				
			adminUtil.showPanelSuccess(rd);			
			adminUtil.hideEntryEvent();			
			if(json.autoreload=='Y'){
				adminUtil.refreshSearch();
			}
			adminUtil.hideOverlay();
			if(adminUtil.activeEditTR){
				adminUtil.activeEditTR.attr("class", "editedrecord");
			
				if(adminUtil.activeModuleId!='uploadModule' && json.record){
					var oTable = $('table.sorting').dataTable();
					var iPos = adminUtil.activeEditTR.prevAll().length;
					//var aPos = oTable.fnGetPosition(adminUtil.activeEditTR);
					//alert("aPos = "+aPos);
					oTable.fnUpdate( json.record, iPos, 0, false );
					//adminUtil.activeEditTR.html($(unescape(json.record)).html());										
				}

				if(json.image!=''){
					adminUtil.activeEditTR.find('#productImageTD').html(json.image);
				}		
			}
		}else if(rc=='2'){	// Delete Successfully					
			adminUtil.hideOverlay();
			adminUtil.refreshSearch();
		}else if(rc=='3'){	// Edit Popup
			var moduleName = (json.md)?(json.md+'Module'):adminUtil.activeModuleId;
			var name, value, control, field;		
			var record="";		
			var form = $("#"+moduleName+"Entry").find("#entryForm");
			var formName = '';
	        var formType = '';
	        var formValue = '';
	        	        
	        if(json.R){
	        	record = json.R[0];	
	        }
        	
        	if(record){        		
				for(var i=0; i<record.length; i++){	
					fieldname = record[i].n;				
					fieldvalue = record[i].v;
					control = record[i].c;									
					field = form.find('[name="'+fieldname+'"]');
					
					field.each(function(x){
						formType = $(this).attr("type");	
																							
						if(formType=='checkbox'){	
							if(fieldvalue=='Y'){	
								$(this).attr("checked", true);
							}else{
								$(this).attr("checked", false);
							}
						}else if(formType==undefined){							
							$(this).html(fieldvalue);	
						}else if(formType=='textarea'){
							$(this).val(fieldvalue);							
							if(field.attr("class").indexOf("editor")!=-1 && nicEditors.findEditor(field.attr("id"))){																							
								nicEditors.findEditor(field.attr("id")).setContent(fieldvalue);
							}								
						}else if(formType!='radio'){
							if(fieldvalue){											
								$(this).val(fieldvalue);
							}else{
								$(this).val("");
							}	
							if(control=="L"){				
								if(formType=='select-one'){
									$(this).attr("disable", true);
								}else{
									$(this).attr("readOnly", true);								
								}
								
							}
							if(control=="R"){					
								//field.parentNode.parentNode.remove();
							}
						}						
					});
				}
			}
			// For clone row						
			if(json.subclasslength){
				for(var i=0; i<json.subclasslength; i++){					
					adminUtil.processEditSubClass(eval('json.SUBR'+i), true);
				}
			}
		
			// Handle Sub Search
			/*
			if(json.SUBR){
				adminUtil.processEditDetail(json);
			}*/
			
			if(typeof(processEdit)=='function'){
				processEdit(json);
			}

			adminUtil.activeModuleId = moduleName;					
			adminUtil.showEntryEvent();		
					
			if(json.isCopy==true){
				$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionCreate);
			}else{
				$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionUpdate);
			}
			
			ajaxUtil.hidePanel();
		}else if(rc=='4'){	// Search Result						
			var resultHTML = json.resultHTML;			
			var moduleName = (json.md)?(json.md+'Module'):adminUtil.activeModuleId;
			
			if(json.pagination){
				$('#'+moduleName).find('#paginationDiv').html(json.pagination);	
				$('#'+moduleName).find('a[id^=viewPageNumber]').address();
			}			
			
			if(json.resultHTMLAppend){								
				var appendObj = $(json.resultHTMLAppend);								
				appendObj.each(function(i){					
					$(this).attr("class","newrecord");	
					$(this).find("input[name$='row-status']").val("N");
				});			
				
				var paginationData = $('#'+moduleName).find('#paginationData');					
				if(paginationData.find("tr").size()>1){
					appendObj.insertBefore(paginationData.find("tr:first"));
				}else{
					appendObj.appendTo(paginationData);
				}
			}else{
				//if(resultHTML){
					$('#'+moduleName).find('#paginationData').html(unescape(resultHTML));
				//}
			}
						
			adminUtil.sortBy[moduleName] = json[AdminConfig.sortName];
			adminUtil.hideOverlay();
		}else if(rc=='5'){	// Shop Edit
			adminUtil.hideOverlay();
			adminUtil.showPanelSuccess(rd);
		}else if(rc=='9'){	// Validation Error
			adminUtil.showPanelSuccess(rd);
			adminUtil.showEntryEvent();
		}else if(rc=='10'){
			adminUtil.showPanelSuccess(rd);
		}else{				
			adminUtil.showPanelSuccess("Unknown Error, Please Contact Administrator. Error Code : "+rd);						
		}
		
		if(typeof(afterJson)=='function')
			afterJson(json); // Must Exist on every page
	},
	
	processEditSubClass: function(json, empty){
		var key;
        var mainKey;
        var mainArray;
        var innerData;
        var myArr=new Array();  	
        var path;
        
        var printString;
        
        var module = json.md;
        var data = json.data;                        
                
        var moduleEntryTableHead = $("#"+module+"EntryTable").children('thead');
        var moduleEntryTableBody = $("#"+module+"EntryTableBody");
        //var moduleEntryClone = moduleEntryTableHead.find("[id="+module+"Clone]");
        var moduleEntryClone = $("[id="+module+"Clone]"); // Change because of prod image in <UL> 
       	var newClone;
       	var field;
		var tagName;
		var arrRow = ["rowEven", "rowOdd"];		
		
		
		
       	if(moduleEntryClone.html()){
       		var parentTag = moduleEntryClone.parent().get(0).tagName;
			//alert("parentTag = "+parentTag);
			if(parentTag=='UL'){
				moduleEntryTableBody = moduleEntryClone.parent();
			}
       		//tagName = moduleEntryClone.get(0).tagName;
       		//alert("data.length = "+data.length);
       		if(empty){
       			moduleEntryTableBody.empty();
       		}            	
       		
			for(var i=0; i<data.length; i++){
	        	mainArray = data[i];    
	        		        		
	        	for(var x=0; x<mainArray.length; x++){  
	        		printString = "";    
	        		
	        		if(moduleEntryClone){	        			
	        			newClone = moduleEntryClone.clone(true);	        			
	        			newClone.attr("id", "");
	        			newClone.attr("class", arrRow[(i % 2)]);	        			
	        			newClone.appendTo(moduleEntryTableBody);
	        			        			
	        			for (key in mainArray[x]) {     	    
	        				field = newClone.find('[name='+key+']');	        	
	        				
	        				//if(field.is("span")){
	        					//fieldValue = (mainArray[x][key+"_value"])?mainArray[x][key+"_value"]:mainArray[x][key];
	        				//}else{
	        					//fieldValue = mainArray[x][key];
	        				//}
	       					
	        				//alert("fieldValue = "+fieldValue);		
	        				this.assignValue(field, mainArray[x][key], mainArray[x][key+"_value"]);	        			
		        			printString += (key +":"+   mainArray[x][key]) + '\n';   	        			 			  	       				       			 			        	
						}
						newClone.show();
						newClone.find("textarea[class=editor]").each(function(y){	
							$(this).attr("id", new Date().getTime());						
							$(this).width("500px");
							$(this).height("150px");		
							new nicEditor({fullPanel : true}).panelInstance($(this).attr("id"));										
						});									
						adminUtil.addCloneID(newClone);	
						ajaxUtil.resetGlobalComponentEvent(newClone);
	        		}
	        		
					//alert(printString);        		       					 					
	        	}    	  
	        }
		}		
		
		$('tbody[id$=EntryTableBody]').find("select[class=replaceText]").each(function(i){			
			if($(this).val()==''){
				$(this).parent().html("");
			}else{
				$(this).parent().html($(this).find("option:selected").text());
			}			
		});
	},
	
	assignValue: function(fieldArray, fieldvalue, labelValue){	
		var field, formType, formName;		
		fieldArray.each(function(i){
			field = $(this);
			formType = field.attr("type");		
			formName = field.attr("name");
			if($(this).get(0).tagName=='IMG'){
				formType = 'img';
			}else if($(this).get(0).tagName=='SPAN'){
				formType = 'span';
			}			
			//alert(formName + "["+formType+"] = "+	fieldvalue);	
			if(formType=='checkbox'){	
				if(fieldvalue=='Y'){	
					field.attr("checked", true);
				}else{
					field.attr("checked", false);
				}						
			}else if(formType=='textarea'){
				field.val(fieldvalue);
				//nicEditors.findEditor(field.attr("id")).setContent(fieldvalue);
			}else if(formType=='radio'){
				
			}else if(formType=='img'){
				var lastIndex = fieldvalue.lastIndexOf('/') + 1;
				var src = fieldvalue.substring(0, lastIndex) + "tn_" + fieldvalue.substring(lastIndex);				
				field.attr("src", src);								
			}else if(formType=='span'){			
				field.html(fieldvalue);
			}else if(formType=='hidden'){
				field.val(fieldvalue);
				var span = field.parents("td").find("span[name="+formName+"]");
				if(span){
					span.html((labelValue!='')?labelValue:fieldvalue);
				}
			}else{
				if(fieldvalue){					
					field.val(fieldvalue);
				}else{
					field.val("");
				}	
			}
		});	
		
	},
	
	processEditDetail: function(json){
		var rc = json.SUBR.rc;
				
		if(rc==4){			
			var parentid = $('#'+adminUtil.activeModuleId+'Entry').find('input[name='+json.SUBR.parentkey+']').val();			
			var paginationHTML = json.SUBR.pagination;
			var resultHTML = json.SUBR.resultHTML;
			var moduleName = json.SUBR.md;
			
			var moduleObj = $('#'+moduleName+'Module');
			var searchForm = moduleObj.find('#searchForm');			
			moduleObj.find('#paginationDiv').html(paginationHTML);
			moduleObj.find('#paginationData').html(resultHTML);
			moduleObj.find('input[name='+json.SUBR.parentkey+']').val(parentid);
			if(searchForm.find("input[name='subaction']").size()==0){															
				$("<input type='hidden' name='subaction' value='attach'>").appendTo(searchForm);														
			}				
		}	
	},
		
	handleFallback:function(xmlHttpReq){
		strResponse = xmlHttpReq.responseText;
       	switch (xmlHttpReq.status) {
       		// Page-not-found error
            case 404:
	           	alert('Error: Not Found. The requested URL could not be found.');
	           	break;
           	// Display results in a full window for server-side errors
           	case 500:
			// handleErrFullPage(strResponse);
                break;
            default:
			// Call JS alert for custom error or debug messages
                if (strResponse.indexOf('Error:') > -1 || 
               		strResponse.indexOf('Debug:') > -1) {
               		alert(strResponse);
                }
                // Call the desired result function
                else {
					alert(strResponse);
                }
                break;
       	}
	},
	
	formReset: function(){	
		var formObject = $("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.entryForm+"");	
		formObject.find("input, select, textarea").each(
        	function(i){
        		formName = $(this).attr("name");
        		formType = $(this).attr("type");
        		formValue = $(this).val();        		
        		switch (formType) {
	                case 'text':
					case 'password':
					case 'textarea':					
						$(this).val("");
						//$(this).attr("readOnly", false);
						//$(this).attr("enable", true);						
						break;
					case 'checkbox':
						$(this).attr("checked", false);
						break; 
				}
				
			}
		);
		
		$("textarea[class=editor]").each(function(i){						
			myTextarea = nicEditors.findEditor($(this).attr("id"));
			if(myTextarea){								
				myTextarea.setContent("");				
			}				
		});
		
		$("#"+adminUtil.activeModuleId+"Entry").find("#paginationData").html("");
		$("#"+adminUtil.activeModuleId+"Entry").find("#paginationDiv").html("");								    
	},
	
	resetClone: function(makeNewRow){
		// Reset Detail Table 
		var cloneObject;
		var cloneId = "";		
				
		$("[class$=detail-listingTable]").each(function(i){						
			var moduleEntryTableHead = $(this).children("thead");
			var moduleEntryTableBody = $(this).children("tbody");
						
			moduleEntryTableBody.empty();
				
			cloneObject = moduleEntryTableHead.children("[id$=Clone]");	
			//if(module!='imageDetail'){					
			if(makeNewRow){				
				newClone = cloneObject.clone(true);				
	       		newClone.attr("id","");		       		
	       		newClone.find('input[type=checkbox]').attr("checked", false);
				newClone.find('input[name$=row-status]').val('N');
				var newChildClone = newClone.find("[id$=Clone]");
				var childBody = newClone.find("tbody[id$=EntryTableBody]");
				
				if(newChildClone.size()>0){						
		        	newChildClone.appendTo(childBody);
		        	newChildClone.show();
				}
				adminUtil.addCloneID(newClone);
				newClone.attr("style","");		
				newClone.appendTo(moduleEntryTableBody);     
		        newClone.show();
		        
				newClone.find("textarea[class=editor]").each(function(y){	
					$(this).attr("id", new Date().getTime());						
					$(this).width("500px");
					$(this).height("150px");								
					new nicEditor({fullPanel : true}).panelInstance($(this).attr("id"));										
				});				
				ajaxUtil.resetGlobalComponentEvent(newClone);
		    //}	
		    }
		});		
		
					
	}
}

	function Admin(){
		
	}

	var adminUtil;
	$(window).ready(function () {
		adminUtil = new Admin(); 
		adminUtil.initialize();
	});


