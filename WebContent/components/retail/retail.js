var prodAttrJson;

// login.htm
	function login(){
		var form = $("#entryForm");										
		var valid = $(".validateForm").validationEngine('validate');					
		if(valid){						
			ajaxUtil.showPanel($("#info-msg"), "Signing In...");		
			var query = "return=json&action1=signin" + "&" + ajaxUtil.formData2QueryString(form, true);
			
			ajaxUtil.postJsonQuery(null, query, function(json){
				ajaxUtil.updatePanelMessage(json.rd);
				if(json.rc=='0000'){
					ajaxUtil.hidePanel();
					if(json.redirect){
						setTimeout(function (){window.location.href=json.redirect;}, 1000);	
					}					
				}				
			}, function(){});
			
		}		
	}
	
	function openFacebook(){
		var url = window.location.href;
		var arr = url.split("/");
		var returnPage = arr[0] + "/" + arr[2] + "/fb-signinreturn.htm";
						
		var url = "https://www.facebook.com/dialog/oauth/?scope=email"; 
		url += "&client_id="+facebook_id;
		url += "&display=popup";
		url += "&redirect_uri="+returnPage;
		url += "&response_type=token";										
		window.open(url,"facebookwindow","menubar=0,resizable=1,width=650,height=250");
	}
		
	function emptyOption(){
		$("#opt1").empty();
		$("#opt2").empty();
		$("#opt3").empty();
		$("#opt4").empty();
	}
	
	function resetOption(){
		$(".options dd").attr("class", "");
		$("#customCloneAddForm").find("#optid").val("");
	}
	
	function putProduct(json){
		prodAttrJson = json.sub;
		var main = json.main;
		var sub = json.sub;
		var optName;
		emptyOption();	
		
		for(var i=1; i<=4; i++){
			optName = eval("main.opt"+i+"_name");
			if(optName!=""){
				$("#thOpt"+i).html(optName);
			}else{
				$("#thOpt"+i).parent("tr").remove();
			}
		}
	
		processProductDetail(sub);
	}
	
	function processProductDetail(sub, ignoreOption){
		var opt1Array = new Array();
		var opt2Array = new Array();
		var opt3Array = new Array();
		var opt4Array = new Array();
		var ddObj;
		
		for(var i=0; i<sub.length; i++){	
			if(!opt1Array[sub[i].opt1]) opt1Array[sub[i].opt1]=0;
			if(!opt2Array[sub[i].opt2]) opt2Array[sub[i].opt2]=0;
			if(!opt3Array[sub[i].opt3]) opt3Array[sub[i].opt3]=0;
			if(!opt4Array[sub[i].opt4]) opt4Array[sub[i].opt4]=0;
						
			opt1Array[sub[i].opt1] += parseInt(sub[i].qty);
			opt2Array[sub[i].opt2] += parseInt(sub[i].qty);
			opt3Array[sub[i].opt3] += parseInt(sub[i].qty);
			opt4Array[sub[i].opt4] += parseInt(sub[i].qty);
			//alert(opt1Array[sub[i].opt1]);
			//alert("Opt1: "+sub[i].opt1+", Opt2: "+sub[i].opt2+" Price:"+sub[i].price+", Qty: "+ sub[i].qty);
		}	
		
		// Reset all option dd to outofstock except selected	
		$(".options:not('#opt"+ignoreOption+"') dd").each(function(i){
			if(!$(this).attr("class")){
				$(this).attr("class", "outofstock");
			}
		})
		
		var optArray;
		for(var i=1; i<=4; i++){
			if(ignoreOption!=i){
				optArray = eval("opt"+i+"Array");
				for(var p in optArray){
					if(p){
						ddObj = $("#opt"+i+" #"+p);				
						if(ddObj.size()==0){
							$("<dd id='"+p+"'"+((optArray[p]>0)?"":" class='outofstock' ")+">"+p+"</dd>").appendTo("#opt"+i);
						}else{
							if(optArray[p]>0 && ddObj.attr("class")!="selected"){
								ddObj.attr("class", "");
							}
						}
					}
				}
			}
		}
	}
	
	function processSelection(dlObj, ddObj){
		var sub = prodAttrJson;
		var whichOption = dlObj.attr("id").substring("opt".length);
		var selectedOption = ddObj.attr("id");
		var optArray = [];
		var opt;
		var validCount = 0;
		var valid = false;
		
		var opt1Selected = $("#opt1 dd.selected").html();
		var opt2Selected = $("#opt2 dd.selected").html();
		var opt3Selected = $("#opt3 dd.selected").html();
		var opt4Selected = $("#opt4 dd.selected").html();
			
		for(var i=0; i<sub.length; i++){
			opt = eval("sub[i].opt"+whichOption);
			valid = true;
			if(opt1Selected && opt1Selected!=sub[i].opt1){			
				valid = false;
				continue;
			}
			if(opt2Selected && opt2Selected==sub[i].opt2){			
				valid = false;
				continue;
			}
			if(opt3Selected && opt3Selected==sub[i].opt3){			
				valid = false;
				continue;
			}
			if(opt4Selected && opt4Selected==sub[i].opt4){						
				continue;
			}
			
			if(valid){
				optArray[validCount] = sub[i];
				validCount++;
			}				
		}
		
		processProductDetail(optArray, whichOption);
		isAllSelected();
		setSelected();
	}
	
	// Check If All Selected
	function isAllSelected(){
		var sub = prodAttrJson;
		var allSelected = true;
		var mainForm;
		var attrSelected = "";
		$(".options").each(function(i){
			if($(this).children("dd").size()>0 && $(this).children("dd.selected").size()==0){
				allSelected = false;
			}
		});
		
		if(allSelected){
			$(".options dd.selected").each(function(i){
				attrSelected += $(this).attr("id");
			});
			
			for(var i=0; i<sub.length; i++){
				//alert("attrSelected:"+attrSelected +", aaa:"+sub[i].opt1+sub[i].opt2+sub[i].opt3+sub[i].opt4);
				if(attrSelected==sub[i].opt1+sub[i].opt2+sub[i].opt3+sub[i].opt4){
					mainForm = $("#customCloneAddForm");				
					mainForm.find("#optid").val(sub[i].optid);
					//alert("sub[i].price = "+sub[i].price);
					$("#unitprice").html(ajaxUtil.roundNumber(sub[i].sellprice,2));
				}			
			}
		}
		
		return allSelected;
	}
	
	// Assign Selected
	function setSelected(){
		var sub = prodAttrJson;
		var allSelected = true;
		var mainForm;
		var attrSelected = "";
		
		var selectedOptionId = $("#customCloneAddForm").find("#optid").val();
		if(selectedOptionId!=""){
			for(var i=0; i<sub.length; i++){
				if(selectedOptionId==sub[i].optid){
					// Reset all option dd to outofstock except selected
					//$(".options dd").attr("class", "outofstock");
		
					//$(".options dd#"+sub[i].opt1).attr("class", "selected");
					//$(".options dd#"+sub[i].opt2).attr("class", "selected");
					//$(".options dd#"+sub[i].opt3).attr("class", "selected");
					//$(".options dd#"+sub[i].opt4).attr("class", "selected");
				}
			}
		}
		
	}
	
	function isInt(n) {
	   return n % 1 === 0;
	}
	
	function addToCart(){
		var sub = prodAttrJson;
		var form = $("#customCloneAddForm");		
		var selectedOptionId = form.find("#optid").val();
		var qty = form.find("input[name=qty]").val();	
		var gotError = false;	
		if(!isAllSelected()){
			alert("Please select options.");
			return;
		}
		
		if(!isInt(qty)){
			alert("Please input quantity.");
			return;
		}
		
		for(var i=0; i<sub.length; i++){				
			if(sub[i].optid==selectedOptionId){
				if(parseInt(sub[i].qty)<parseInt(qty)){
					alert("Maximum order quantity for selected option is "+sub[i].qty);
					gotError = true;
				}
				break;
			}
		}		
		
					
		if(!gotError){						
			var dialog_buttons = {};
			dialog_buttons["Continue Shopping"] = function(){						
				ajaxUtil.hidePanel();
				return;
			}; 
			dialog_buttons["View Cart"] = function(){ 							
				window.location="cart.htm";
				ajaxUtil.hidePanel();
				return;
			};	
														
			ajaxUtil.showPanel($("#info-msg"), "Adding To Cart...");		
			var query = "return=json&action1=add" + "&" + ajaxUtil.formData2QueryString(form, true);	
			ajaxUtil.postJsonQuery("cart.htm", query, function(json){
				if(json.rc=='0000'){				
					ajaxUtil.updatePanelMessage("Item Added To Cart", dialog_buttons);
				}				
			}, function(){ alert("Error"); });
		}		
	}

// Cart.htm
	function doCartDelete(key){
		ajaxUtil.showPanel($("#info-msg"), "Updating...");			
		//window.location.href="?action1=delete&row-status=D&cartdetailid="+key;	
		
		ajaxUtil.postJsonQuery(null,"action1=delete&return=json&row-status=D&cartdetailid="+key, (function(json){
			if(json.rc='0000'){					
				$("tr#"+key).remove();
				calPaymentTotal();
				ajaxUtil.hidePanel();
			}
			
		}), null);
		
	}

	function doCartUpdate(key, qty){
		ajaxUtil.showPanel($("#info-msg"), "Updating...");					
		var query = "action1=update&return=json&row-status=U&cartdetailid="+key+"&qty="+qty;		
		ajaxUtil.postJsonQuery(null,query, (function(json){
			if(json.rc='0000'){										
				$("tr#"+key).find("#sellsubtotal").html(ajaxUtil.roundNumber(json.data.sellsubtotal,2));
				$("tr#"+key).find("input[name=sellsubtotal]").val(ajaxUtil.roundNumber(json.data.sellsubtotal,2));
				
				shopUtil.updateSingleObjCurrency($("tr#"+key).find("#sellsubtotal"));
						
				calPaymentTotal();		
				ajaxUtil.hidePanel();
			}
			
		}), null);	
	}
	

	
	var isStateACInit = false;
	var stateJson;
	
	function initStateAutoComplete(){
		ajaxUtil.postJsonQuery("cart.htm", "return=json&action1=action.auto.suggest", function(json){				
			stateJson = json;						
			$("input[name=state]").autocomplete({
				source: stateJson,
				minLength: 2
			});
		}, function(){});
		isStateACInit = true;		
	}

	function getShippingEstimate(){		
		var weight = $("input[name=totalweight]").val();
		var state = $("input[name=state]").val();
		var country = $("select[name=country]").val();
		var query = "return=json&weight="+weight+"&state="+state+"&country="+country;
		if(country==shopcountry){
			if(state=="") return;
			query += "&action1=getlocal";
		}else{
			query += "&action1=getinternational";
		}
		alert("query = "+query);
		ajaxUtil.showPanel($("#info-msg"), "Estimating Shipping Charge...");					
		ajaxUtil.postJsonQuery("cart.htm", query, function(json){				
			if(json.rc=='0000'){			
				ajaxUtil.updatePanelMessage("<b>Shipping Charge: </b>"+currencysymbol+json.rate);
				$("input[name=shippingcharge]").val(ajaxUtil.roundNumber(json.rate,2));
				$("span#shippingcharge").html(ajaxUtil.roundNumber(json.rate,2));									
			}else{
				$("input[name=shippingcharge]").val("0.00");
				$("span#shippingcharge").html("0.00");
				ajaxUtil.updatePanelMessage("Unable to estimate. Please contact us for shipping charge.");
			}				
			calPaymentTotal();
		}, function(){});
	}

	function calPaymentTotal(){
		var total = 0;
		var shippingcharge = parseFloat($("input[name=shippingcharge]").val());
		
		shippingcharge = isNaN(shippingcharge)?0:shippingcharge;
		$("input[name=sellsubtotal]").each(function(i){
			total += parseFloat($(this).val());
		});
		//alert(total+"+"+shippingcharge+"="+(total+shippingcharge));
		$("span#shippingcharge").html(ajaxUtil.roundNumber(shippingcharge,2));
		
		$("span#subtotal").html(ajaxUtil.roundNumber(total,2));
		$("span#totalsales").html(ajaxUtil.roundNumber(total+shippingcharge,2));
		
		$("input#subtotal").val(ajaxUtil.roundNumber(total,2));
		$("input#totalsales").val(ajaxUtil.roundNumber(total+shippingcharge,2));			
	}
	    
	function checkOutPayPal(json){						
		var form = $("<form>");
		var splitParam;
		var itemParam;		
	
		var itemname, itemqty, itemunitprice, shipping;		
		var shippingObject = $("input[name=shippingcharge]");		
		var parameters = eval("({"+$("#mainForm").find("input[name=paymentmethod]:checked").parent().find("input[name=parameters]").val()+"})");				
		
		var url = window.location.href;
		var arr = url.split("/");
		var returnPage = arr[0] + "//" + arr[2] + "/return.htm";
		
		form.attr("id", "DynamicPaymentForm");
		//form.attr("action", "https://mobile.paypal.com/cgi-bin/webscr");
		form.attr("action", "https://www.paypal.com/cgi-bin/webscr");
		form.attr("method", "get");
		
		$("<input type='hidden' name='cmd' value='_cart'>").appendTo(form);
		$("<input type='hidden' name='upload' value='1'>").appendTo(form);
		$("<input type='hidden' name='no_shipping' value='1'>").appendTo(form);
		$("<input type='hidden' name='return_page' value='"+returnPage+"'>").appendTo(form);
		$("<input type='hidden' name='currency_code' value='"+paymentcurrency+"'>").appendTo(form);
		$("<input type='hidden' name='business' value='"+parameters.business+"'>").appendTo(form);		
		
		itemname='item_name_*';
		itemqty='quantity_*';
		itemunitprice='amount_*';
		
		var newName;			
		var nameFieldCount = 1;	
		var qtyFieldCount = 1;
		var subtotalFieldCount = 1;
		
		$("#mainForm").find('input[type=hidden]').each(
			function(i){
				newName = null;
				if($(this).attr("id")=='itemname' && itemname){
					newName = itemname.replace('*', nameFieldCount);
					nameFieldCount++;
				}else if($(this).attr("id")=='quantity' && itemqty){
					newName = itemqty.replace('*', qtyFieldCount);
					qtyFieldCount++;
				}else if($(this).attr("id")=='unitprice' && itemunitprice){
					newName = itemunitprice.replace('*', subtotalFieldCount);
					subtotalFieldCount++;
				}							
				if(newName!=null){									
					$("<input type='hidden' name='"+newName+"' value='"+$(this).val()+"'>").appendTo(form);														
				}
			}
		);
		
		// Add Shipment and Handling					
		$("<input type='hidden' name='"+itemname.replace('*', nameFieldCount)+"' value='Shipping Charge'>").appendTo(form);	
		$("<input type='hidden' name='"+itemqty.replace('*', qtyFieldCount)+"' value='1'>").appendTo(form);
		$("<input type='hidden' name='"+itemunitprice.replace('*', subtotalFieldCount)+"' value='"+shippingObject.val()+"'>").appendTo(form);
		$("<input type='hidden' name='charset' value='utf-8'>").appendTo(form);
		
		form.appendTo("body");					
		form.submit();			
	}
	
	function sendInvoice(json){
		var form = $("#mainForm");				
		var paymentMethod;
		
		form.find("input[name=paymentmethod]:checked").each(function(i){				
			if($(this).attr("checked")){
				paymentMethod = $(this).attr("id");
				paymentMethod = paymentMethod.substr(13);
			}
		});	
		alert("paymentMethod = "+paymentMethod);
		var query = "return=json&action1=sendinv&transid="+json.transid;			
		
		ajaxUtil.postJsonQuery("invoice.htm", query, function(json){				
			if(paymentMethod=='CC'){
				ajaxUtil.updatePanelMessage("Redirect to PayPal...");
				checkOutPayPal(json);
			}else if(paymentMethod=='OT' || paymentMethod=='COD'){
				form = $("<form>");	
				form.attr("id", "DynamicPaymentForm");				
				form.attr("method", "get");			
				form.attr("action","return.htm");
				form.appendTo("body");					
				form.submit();			
			}	
			
		}, null);	
	}