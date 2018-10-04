	function Shop(){
		this.initialize();
	}
	
	var shopUtil;

Shop.prototype = {
    initialize: function() {
    	$(document).bind('click', (function(event){
			var element = event.target;  					   			     					
			if (element.id=='btnProcess') {																					
				shopUtil.submit($(element));				
				event.preventDefault(); return;
			}					
        }));        
        
        $("select[name=currencycode]").bind("change", (function(event){
			var element = event.target;
			ajaxUtil.showPanel("", "Changing Currency", null);
			var query = "return=json&action1=chgcurrency&tocurrency="+$(element).val();			
			ajaxUtil.postJsonQuery("currency.htm", query, (function(json){				
				location.reload();
			}), shopUtil.fallback);
			event.preventDefault();	return;
		}));
		
		this.updateCurrency($(document));
	},
	
	updateCurrency: function(obj){
		var orgAmount = 0;							
		obj.find(".currencysymbol").html(currencysymbol);	
		
		if(currencyrate>0){			
			obj.find(".currency").each(function(i){							
				if(!isNaN(parseFloat($(this).html())) && isFinite($(this).html())){
					orgAmount = parseFloat($(this).html());
					$(this).html(ajaxUtil.commaFormatted(ajaxUtil.roundNumber(orgAmount*currencyrate,2)));
				}			
			});	
		}
	},
	
	updateSingleObjCurrency: function(obj){
		var orgAmount = 0;				
		orgAmount = parseFloat(obj.html());
		obj.html(ajaxUtil.commaFormatted(ajaxUtil.roundNumber(orgAmount*currencyrate,2)));		
	},
	
	updater: function(objectName, url){				
		$.ajax({
			type: "POST",					
			url: url,					
			success: function(html){
				$("#"+objectName).html(html);																							   					  
			}					
		});	
	},
	
	fallback:function (xmlhttp) {
		alert("Unknown Error");
	},
			
	submit: function(ele){				
		var form = ele.closest('form');			
		var url = form.attr("action");		
		var infoMsg = ele.closest("#info-msg");			
		var gotError = $.validationEngine.submitValidation(form);					
		if(!gotError){									
			ajaxUtil.showPanel("", "Processing", null);
			var query = "return=json" + "&" + ajaxUtil.formData2QueryString(form);				
			ajaxUtil.postJsonQuery(url, query, (function(json){				
				ajaxUtil.updatePanelMessage(json.rd);						
				if(json.rc=='0'){
					infoMsg.attr("class","info-msg-success");								
				}else{
					infoMsg.attr("class","info-msg-fail");			
				}
				if(typeof(afterJson)=='function'){
					afterJson(json);					
				}else{				
					if(json.redirect){						
						window.location.href=json.redirect;											
					}
				}
				//setTimeout(function (){ajaxUtil.hidePanel();},3000);
			}), shopUtil.fallback);
			
		}
	}
}
	
	$(document).ready(function () { shopUtil = new Shop(); });
