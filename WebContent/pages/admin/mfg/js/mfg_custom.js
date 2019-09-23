	var RowData = {
		prodid: '',
		minOrder: 0,		
		width: 0,
		height: 0,
		price: 0,
		cost: 0,
		qty: 0,
		discount: 0,
		unit: 0,
		costsubtotal: 0,
		sellsubtotal: 0,
		sellunittype: '',
		customWidth: 0,
		customHeight: new Array(),
		customFormula: '',
		customizable: '',
		customData: '',
		curentTR: null
	}

	function MFGUtilClass(){
		this.init();
	}
	
	MFGUtilClass.prototype = {
		tableHeight: 400,
		ratio: 0,
		mmDecimalToInch: [],
		_m:'',
		
		init: function(){			
			$(document).bind("click", function(event){
			    var element = event.target;
				
				if(element.id=='companygrplink'){	
					event.preventDefault();
					var path = window.location.pathname;
					var href = $(element).attr("href");
					var index = path.lastIndexOf("/");
					var filename = path.substr(index+1);					
					window.location = href + filename;					
				}
				
				if(element.id=='action.clone.addgroup'){		
					mfgUtil.addGroup();
					event.preventDefault(); return;
				}								
	        });	
	        
	        
			
	        this.bindAllSelect();	
	        
	        for(var i=1;i<=16;i++){
	        	this.mmDecimalToInch[i/16] = i + "/16";
	        }
	        
		},	
		
		bindAllSelect: function(){				
			$('#customCloneAddForm').find("input").bind("change", (function(event){
				var element = event.target;																		
				if((element.id=="mfgtdmeasurement" || element.id=="measurement") && element.value!=''){					
					var form = $('#customCloneAddForm');					
					var measurement = mfgUtil.getMeasurement($(element).val());					
					form.find("span[id$=width]").html(measurement[0]);
					form.find("span[id$=height]").html(measurement[1]);					
					form.find("[name$=width]").val(measurement[0]);
					form.find("[name$=height]").val(measurement[1]);					
				}		
				mfgUtil.calculate($("#customCloneAddForm")); // custom.js						
			}));
						
			$('#customCloneAddForm').find('select').bind("change", (function(event){							
				mfgUtil.calculate($("#customCloneAddForm")); // custom.js					
			}));
			
			$('select[name=mfgtdprodid]').bind("change", (function(event){
				mfgUtil.loadProductOption($(this).val());	
				if($("#mfgtdmeasurement").val()!=""){
					var form = $('#customCloneAddForm');	
					var measurement = mfgUtil.getMeasurement(form.find("#mfgtdmeasurement").val());					
					form.find("span[id$=width]").html(measurement[0]);
					form.find("span[id$=height]").html(measurement[1]);					
					form.find("[name$=width]").val(measurement[0]);
					form.find("[name$=height]").val(measurement[1]);
				}					
				mfgUtil.calculate($("#customCloneAddForm"));
				event.preventDefault(); return;				
			}));
		},
				
		custom_after_clone: function(){					
			mfgUtil.bindAllSelect();			
		},
		
		loadProductOption: function(prodid){
			var parentNode = $("#customCloneAddForm");														
			var replaceNode;										
			if(prodid!=''){																	
		   		var prodoptid;
		   		var prodJson = eval('productMainjson.data.prod'+prodid);
		   		var sellunittype = prodJson.sellunittype;
		   		var customizable = prodJson.customise;			   		
		   		var optionjson = eval('productOptionjson.data.prod'+prodid);
		   					   				
		   		var hiddenField;			   	
		   		var name;
		   		
		   		// Reset Fields			   		  				   
		   		parentNode.find('input:not([name$=transdetailid],[name$=status],[name$=measurement],[name$=qty])').val('');
		   		//parentNode.find('#mfgtdwidth').html('');
		   		//parentNode.find('#mfgtdheight').html('');
		   		$("#chargeProduct").html("");
		   		
				parentNode.find("td[id*=opt]").each(function(i){
					name = $(this).find("input,select").attr("name");						
					hiddenField = $("<input type='hidden'>");
					hiddenField.attr("name", name);
					$(this).empty();
					hiddenField.appendTo($(this));
				});
		   		if(optionjson){			   			
		   			prodoptid = optionjson.split(',');
		   			for(var i=0;i<prodoptid.length;i++){
			   			var clone;
			   			replaceNode = parentNode.find("td[id$=opt"+(i+1)+"]");
			   			clone = $('#preload').find('li[id=opt'+prodoptid[i]+']').find("select").clone();					   				
			   			clone.attr("name",	replaceNode.attr("id"));					   			
			   			replaceNode.empty();			   								   		
			   			clone.appendTo(replaceNode);
			   			$("<div id='chargeOpt'></div>").appendTo(replaceNode);				   					
			   			clone.bind("change", (function(event){
							var element = event.target;									
							mfgUtil.calculate($("#customCloneAddForm")); // custom.js								
						}));	   							   								   								   									   		
			   		}				   		
		   		}
		   		
		   		if(customizable=='Y'){
					$("#customizeLink").show();   			
					parentNode.find("select[name$=opt1]").val("");		   			
		   			parentNode.find("select[name$=opt1]").hide();
		   		}else{
		   			$("#customizeLink").hide();
		   		}
		   		mfgUtil.fieldDefault($("#customCloneAddForm"), false);							
			}
		},						
		
		calculate: function(obj){
			var width = obj.find("[name$=width]").val();
			var height = obj.find("[name$=height]").val();
			var prodid = obj.find("[name$=prodid]").val();				
			if(prodid==""){
				return;
			}else if (prodid=='0'){
				mfgUtil.doCalculate($("#customCloneAddForm"));
			}else{
				var prodJson = eval('productMainjson.data.prod'+prodid);
				var sellunittype = prodJson.sellunittype;					
				// Check if all required entry for calculation is fullfil
				var canCheckPrice = false;
				if(sellunittype=='sf' && width>0 && height>0){
					canCheckPrice = true;
				}else if((sellunittype=='ft' || sellunittype=='in' || sellunittype=='mm' || sellunittype=='cm' || sellunittype=='m') && width>0){
					canCheckPrice = true;
				}else if(sellunittype=='u'){
					canCheckPrice = true;
				} 
				//alert("sellunittype= "+sellunittype+", canCheckPrice = "+canCheckPrice);
				if(canCheckPrice){
					checkPrice();
				}else{
					return;
				}
			}
		},
	
	doCalculate: function(obj){		
		if(obj.attr("id")=='mfgtdEntryTableBody'){
			obj.find("tr").each(function(i){				
				mfgUtil.calculateNow($(this));
			});
		}else{
			mfgUtil.calculateNow(obj);
		}
	},
		
	calculateNow: function(obj){
		var customerType = $("#entryForm").find("select[name=custtype]").val();				
		var measurementType = "mm";						
		var sellunittype;			
		
		var productUnit = [];
		var productOptionPrice = [];
		var opt, optprice, optsellunit, optcost;
				
		var prodJson;
		var measurement;
		var measurementEle;
		var json;
		var optName;
		var priceObj;
		var costObj;
		var inputWidth, inputHeight;
		var roundUpPrefix;
		// Reset RowData value
			RowData.prodid= '';
			RowData.minOrder= 0;		
			RowData.width= 0;
			RowData.height= 0;
			RowData.price= 0;
			RowData.cost= 0;
			RowData.qty= 0;
			RowData.discount= 0;
			RowData.unit= 0;
			RowData.costsubtotal= 0;
			RowData.sellsubtotal= 0;
			RowData.sellunittype= '';
			RowData.customWidth= 0;
			RowData.customHeight= new Array();
			RowData.customFormula= '';
			RowData.customData= '';
			RowData.curentTR= null;		
		// End Reset Value
		
		// Calculate Unit and get all product total unit							
		RowData.curentTR = obj;
		RowData.prodid = obj.find("[id$=prodid]").val();							
		if(RowData.prodid==undefined || RowData.prodid==''){
			return;
		}			
		prodJson = eval('productMainjson.data.prod'+RowData.prodid);
		roundUpPrefix = prodJson.roundup_prefix;		
		RowData.sellunittype = prodJson.sellunittype;
		RowData.minOrder = prodJson.minorder;
		RowData.customizable = prodJson.customise;	
		costObj 			= obj.find("[id$=cost]");
		priceObj 			= obj.find("[id$=price]");
		measurementEle 		= obj.find("input[id$=measurement]");		
		measurement 		= mfgUtil.getMeasurement(measurementEle.val());	
		RowData.width		= parseFloat(measurement[0]);
		RowData.height		= parseFloat(measurement[1]);
		RowData.qty			= obj.find("[id$=qty]").val();		
		RowData.customData	= obj.find("[id$=customisedata]").val();		
		// prodid 0 is [Custom Product]
		if(RowData.prodid!='0'){
			priceObj.val("0.00");
			costObj.val("0.00");
		}					
		RowData.qty=(RowData.qty=='')?1:RowData.qty;
		
		if(!customerType){
			customerType = custtype;
		}

		// Calculate Unit								
		if(RowData.customData){						
			var customFormula = prodJson.customformula;				
			eval(customFormula+"_cal()");	
			var customWidth = obj.find("[id$=width]").val();
			var customHeight = obj.find("[id$=height]").val();		
			
			json = eval(RowData.customData);		
			var spanWidth=customWidth / json.colw.length;
			var totalUnit=0;
			for(var x=0; x<json.colw.length; x++){
				RowData.unit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, spanWidth, customHeight);	
				if(typeof(roundUpAtFinal)=='undefined'){
					totalUnit += parseFloat(roundUpSqft(RowData.unit, roundUpPrefix));
				}else{
					totalUnit += parseFloat(RowData.unit);
				}										
			}			
			//alert("RowData.unit = "+RowData.unit+"\ntotalUnit = "+roundUpSqft(totalUnit));
			RowData.unit = roundUpSqft(totalUnit, roundUpPrefix);				
		}else{				
			if(RowData.prodid==0){ // Custom Product
				if(RowData.width && RowData.height){					
					RowData.sellunittype = "sf";
					//RowData.unit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, RowData.width, RowData.height);						
				}else if(!RowData.width && !RowData.height){
					RowData.sellunittype = "u";
					//RowData.unit = "1";
				}else{
					if(measurementEle.val().indexOf("in")!=-1){
						RowData.sellunittype = "in";
						//RowData.unit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, RowData.width, RowData.height);
					}else if(measurementEle.val().indexOf("ft")!=-1){
						RowData.sellunittype = "ft";
						//RowData.unit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, RowData.width, RowData.height);
					}else{
						RowData.sellunittype = "mm";
						//RowData.unit = RowData.width;
					}
				}								
			}else{
				RowData.unit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, RowData.width, RowData.height);			
				//unit = roundUpSqft(unit);					
			}				
		}
		//alert("RowData.unit = "+RowData.unit+", RowData.sellunittype="+RowData.sellunittype+",measurementType="+measurementType);
		if(RowData.prodid!='0' && RowData.unit<=0 && RowData.sellunittype=='sf'){
			obj.find("[id$=unit]").val("");
			obj.find("[id$=price]").val("");
			obj.find("[id$=sellsubtotal]").val("");
			return;
		}					
		
		var origUnit = 0;		
		if(RowData.prodid!='0'){		
			//alert("RowData.unit="+RowData.unit+", RowData.minOrder = "+RowData.minOrder);
			if(typeof(roundUpAtFinal)=='undefined'){
				origUnit = RowData.unit;						
			}else{
				origUnit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, RowData.width, RowData.height);					
			} 						
			if(parseFloat(RowData.minOrder) > parseFloat(RowData.unit)){
				RowData.unit = RowData.minOrder;				
			}					
			//alert("RowData.unit = "+RowData.unit);					
			obj.find("[id$=unit]").val(roundUpSqft(RowData.unit, roundUpPrefix));
		}				
		// Calculate Option Price And Set to price & cost textfield
		var optFormula;
		var optUnit;
		var optPrice;		
		var optValue;
		obj.find("[id*=opt]").each(function(a){			
			//alert($(this).attr("type") + ", $(this).attr('checked') = " + $(this).attr("checked"));	
			if(($(this).attr("type")=='select-one' || $(this).attr("type")=='hidden') && $(this).val()!=''){
				optValue = $(this).val();
				//optName = $(this).find("option:selected").text();
			}else if(($(this).attr("type")=='radio' && $(this).attr("checked"))){
				optValue = $(this).attr("id").substring(3);
			}else{
				optValue = "";
			} 					
			
			if(optValue!=''){					
				//alert("$(this).attr('id')="+$(this).attr("id")+"\noptValue = "+optValue);
				opt = eval('productOptionPriceJson.optdtl'+$(this).val());						
				if(opt){										
					optFormula = opt.formula;						
					if(customerType=='D') optprice = opt.dealerprice;
					if(customerType=='C') optprice = opt.clientprice;
					if(customerType=='P') optprice = opt.publicprice;						
					if(customerType==null){							
						optprice = opt.publicprice;
					}
					optcost = opt.cost;
					optsellunit = opt.sellunittype;						
					optcost=(optcost==null)?"0":optcost;
					
					$(this).parents("td").find("#chargeOpt").html(" +"+optprice + "/" + optsellunit);					
					if("in"==optsellunit && optFormula!=null && optFormula!=''){							
						optUnit = mfgUtil.convertToUnit(measurementType, optsellunit, ("w"==optFormula)?RowData.width:RowData.height, 0);
						if(typeof(roundUpOption)=='function'){
							optUnit = roundUpOption(optUnit);
						}													
						optprice = parseFloat(optprice) * parseFloat(optUnit) / obj.find("[id$=unit]").val();							
					}					
													
					optprice = ajaxUtil.roundNumber(optprice,2);
					//alert("ID="+$(this).val()+"\n optprice = "+optprice+"\n RowData.price = "+RowData.price);
					RowData.cost += parseFloat(optcost);
					RowData.price += parseFloat(optprice);																						
				}
			}
		});
		
		// Calculate Custom Option Price And Set to price & cost textfield
		var optionTotalPrice = 0;
		if(RowData.customData){				
			var data = eval(RowData.customData);
			var span = data.span;
			var colw = data.colw;
			var rowh = data.rowh;
			var totalSpan = span.length;
			var averagePrice = 0;
			var averageCost = 0;
			var spanSqft = 0;
			var spanWidth = 0;
			var spanHeight = 0;
			var spanUnit = 0;
			var totalSpanHeight=0;					
				
			// Change to divide by actual total sf.								
			for(var a=0; a<span.length; a++){					
				totalSpan = span[a].length;
				spanWidth = colw[a];

				//alert("unit = "+unit);
				for(var b=0; b<totalSpan; b++){		
					spanHeight = rowh[a][b];		
					spanUnit = mfgUtil.convertToUnit(measurementType, RowData.sellunittype, spanWidth, spanHeight);
					
					if(typeof(roundUpAtFinal)=='undefined'){
						spanUnit = roundUpSqft(spanUnit, roundUpPrefix);
					}
					//alert("spanWidth="+spanWidth+", \nspanHeight="+spanHeight+", \nspanUnit = "+spanUnit +", origUnit="+origUnit);
					//alert("span["+a+"]["+b+"] = "+span[a][b]);
					opt = eval('productOptionPriceJson.optdtl'+span[a][b]);	
								
					if(opt){
						//alert("spanUnit = "+spanUnit+ ", unit = "+unit+", customerType = "+customerType);
						if(customerType=='D') {optprice = opt.dealerprice};
						if(customerType=='C') {optprice = opt.clientprice};
						if(customerType=='P') {optprice = opt.publicprice};							
						
						optionTotalPrice += optprice * spanUnit;
						optprice = optprice * (spanUnit / origUnit);
						
						optcost = opt.cost * (spanUnit / origUnit);							
						optsellunit = opt.sellunittype;
						
						//alert("optdtlid = "+span[a][b]+", optprice = "+optprice +", spanUnit = "+spanUnit+", unit = "+unit);
						averagePrice += parseFloat(optprice);	
						averageCost += parseFloat(optcost);	
					}						
				}
			}		
			//alert("customData averagePrice = "+averagePrice + ", priceObj.val()="+priceObj.val());				
			$("td[id=mfgtdopt1]").find("#chargeOpt").html(" +"+ajaxUtil.roundNumber(averagePrice,2) + "/" + RowData.sellunittype);							
			averagePrice = parseFloat(averagePrice) + parseFloat(priceObj.val());	
			averageCost =  parseFloat(averageCost) + parseFloat(costObj.val());
			RowData.cost = averageCost;	
			RowData.price= averagePrice;
		}			
			
		// Set Unit Price & sellsubtotal	
		var unitCost;
		RowData.discount= obj.find("[id$=discount]").val();
		RowData.qty 	= obj.find("[id$=qty]").val() * obj.find("[id$=unit]").val();							
		RowData.unit 	= parseFloat(obj.find("[id$=unit]").val());
		
		// Get Unit Price For Product Based on from to quantity
		var unitPrice=0; 
		var unitCost=0;				
		if(RowData.prodid!='0'){
			priceJson = productPricejsonArray[RowData.prodid];									
			if(priceJson){					
				for (p in priceJson) {    							
					if(	priceJson[p].orderto >= RowData.unit){							
						unitPrice = priceJson[p].price;
						unitCost = priceJson[p].cost;
						break;
					}
				}	
				//alert("priceJson[p].orderto = "+priceJson[p].orderto+", unit = "+unit +", unitPrice = "+unitPrice);																						
			}	
		}else{
			// Custom Product
			unitPrice = priceObj.val();
			unitPrice = (unitPrice=='')?'0':unitPrice;
			RowData.price = 0;
		}
		
		$("#chargeProduct").html(ajaxUtil.roundNumber(unitPrice,2) + "/" + RowData.sellunittype);
		// End Get Unit Price For Product
		
		//alert("unitPrice = "+parseFloat(unitPrice)+", RowData.price = "+parseFloat(RowData.price));		
		
		unitCost = parseFloat(unitCost) + parseFloat(RowData.cost);
		RowData.cost = 	ajaxUtil.roundNumber(unitCost,2);
		RowData.costsubtotal = mfgUtil.round5Cents(ajaxUtil.roundNumber(unitCost*RowData.qty,2));
		
		
		// For Orion, Homade Only
		if(typeof(roundUpAtFinal)!='undefined' && (RowData.customData)){			
			RowData.sellsubtotal = mfgUtil.round5Cents(ajaxUtil.roundNumber(((unitPrice*RowData.qty)+optionTotalPrice-RowData.discount),2));
			unitPrice = parseFloat(unitPrice) + parseFloat(RowData.price);			
			RowData.price = 	ajaxUtil.roundNumber(unitPrice,2);			
		}else{
			unitPrice = parseFloat(unitPrice) + parseFloat(RowData.price);			
			RowData.price = 	ajaxUtil.roundNumber(unitPrice,2);			
			RowData.sellsubtotal = mfgUtil.round5Cents(ajaxUtil.roundNumber(((RowData.price*RowData.qty)-RowData.discount),2));
		}
		
		
		//alert("RowData.price = "+RowData.price+", RowData.qty ="+RowData.qty+", RowData.sellsubtotal = "+RowData.sellsubtotal);	
		if(priceObj.attr("type")=='hidden'){
			obj.find("span[name$=cost]").html(RowData.cost);
			obj.find("span[name$=price]").html(ajaxUtil.roundNumber(RowData.price,2));
			obj.find("span[name$=costsubtotal]").html(RowData.costsubtotal);
			obj.find("span[name$=sellsubtotal]").html(ajaxUtil.roundNumber(RowData.sellsubtotal,2));
			obj.find("span[name$=discount]").html(RowData.discount);			
						
		}
		
		if(isNaN(RowData.cost)==false) costObj.val(ajaxUtil.roundNumber(RowData.cost,2));
		if(isNaN(RowData.price)==false) priceObj.val(ajaxUtil.roundNumber(RowData.price,2));																
		if(isNaN(RowData.costsubtotal)==false) obj.find("[id$=costsubtotal]").val(RowData.costsubtotal);
		if(isNaN(RowData.sellsubtotal)==false) obj.find("[id$=sellsubtotal]").val(RowData.sellsubtotal);
		if(isNaN(RowData.width)==false) obj.find("span[id$=width]").html(roundMeasurement(RowData.width));
		if(isNaN(RowData.height)==false) obj.find("span[id$=height]").html(roundMeasurement(RowData.height));
		if(isNaN(RowData.width)==false) obj.find("[id$=width]").val(roundMeasurement(RowData.width));
		if(isNaN(RowData.height)==false) obj.find("[id$=height]").val(roundMeasurement(RowData.height));	
		
		if(typeof(afterCalculation)=='function'){	
			afterCalculation();
		}			
	},
	
		getMeasurement: function(value){
			var m = [];		
			var splitTo = value.split("x");
			var data;
			
			for(var i=0; i<splitTo.length; i++){
				data = splitTo[i];
				data = data.replace(/(^\s*)|(\s*$)/g, "");
				
				if(data.indexOf("in")!=-1){
					m[i]=mfgUtil.convert("in","mm",data);					
				}else if(data.indexOf("mm")!=-1){
					m[i]=data.substring(0,data.indexOf("mm"));
				}else{
					m[i]=data;
				}				
			}
			return m;
		},
		
		convertMmToInch: function(value){
			//alert("b4 value = "+value);
			value = (parseFloat(value) / 25.4) + "";
			value = ajaxUtil.roundNumber(value, 5)+"";
			//alert("after value = "+value);
			
			var dec = 0;
			var val = 0;
			var rtn = "";
			if(value.indexOf(".")!=-1){
				dec = "0."+value.substring(value.indexOf(".")+1);
				val = value.substring(0, value.indexOf("."));
				rtn = val + " " + mfgUtil.mmDecimalToInch[dec];
			}else{
				rtn = value;
			}
			 
			//alert("value = "+value+", val="+val+ ", dec="+dec + ", rtn = "+rtn);		
			return rtn;	
		},
		
		round5Cents: function(val){
			return (Math.round(val*20)/20);  
	 	},
		
		fieldDefault: function(obj, multiple){
			var optId, opt1, opt2, opt3;
			var replaceNode;	
			var prodJson;
			var prodid;
			var sellunittype;
			var customizable;
			var prodNode;		
			var customisedata;	
			var clone;
			var preload = $('#preload').find("select");		
			var optionjson;		
			obj.each(function(i){							
				prodid = $(this).find("input[name$=prodid]").val();	
				if(prodid==undefined){
					prodid = $(this).find("select[name$=prodid]").val();	
				}			
				opt1 = $(this).find("input[name$=opt1]").val();
				opt2 = $(this).find("input[name$=opt2]").val();
				opt3 = $(this).find("input[name$=opt3]").val();							
				if(prodid==undefined){
					return;
				}					
				prodNode = $(this).find("[name$=prodid]").parent();												
				prodJson = eval('productMainjson.data.prod'+prodid);
				if(prodJson==undefined) return;
				optionjson = eval('productOptionjson.data.prod'+prodid);							
				
				$(this).find("[name$=measurement]").attr({readonly: ''});		
				
				if(prodid==0 || prodid==''){									
					$(this).find("span[name$=prodid]").html($(this).find("[name$=prodname]").val() + " " + prodJson.name);
				}else{					
					$(this).find("span[name$=prodid]").html(prodJson.name);
					if(multiple){
						if(opt1!='') $(this).find("span[name$=opt1]").html(eval('productOptionPriceJson.optdtl'+opt1).name);	
						if(opt2!='') $(this).find("span[name$=opt2]").html(eval('productOptionPriceJson.optdtl'+opt2).name);
						if(opt3!='') $(this).find("span[name$=opt3]").html(eval('productOptionPriceJson.optdtl'+opt3).name);
					}
				}
				
				sellunittype = prodJson.sellunittype;
				customizable = prodJson.customise;
				//alert("sellunittype= "+sellunittype+", customizable = "+customizable);
				
				customisedata = $(this).find("[name$=customisedata]").val();
		
				if(sellunittype=='u'){								
					$("#customCloneAddForm").find("[name$=measurement]").val("");
					$("#customCloneAddForm").find("[name$=measurement]").attr("readonly", "readonly");							
				}														   		
			});		
		},

		convertToUnit: function(measurement, sellUnit, width, height){
			var unit;
			if(sellUnit=='u'){
				unit = "0";					
			}else if(sellUnit=='in' || sellUnit=='ft' || sellUnit=='mm' || sellUnit=='cm' || sellUnit=='m'){
				unit = mfgUtil.convert(measurement, sellUnit, width);
			}else if(sellUnit=='sf'){
				unit = mfgUtil.convert(measurement, sellUnit, width*height);
			}			
			//alert("sellUnit = "+sellUnit+", unit = "+unit+", width = "+width+", height= "+height);			
			return unit;
		},
		
		// Unit = SqFt

		convert: function(fromUnit, toUnit, value){					
			var divideValue = 1;
			var calValue = 0;
			// 2in = ?mm			
			if(isNaN(value)){				
				value = value.replace(/(^\s*)|(\s*$)/g, "");
				if(value.indexOf("in")!=-1){
					value = value.substring(0, value.indexOf("in"));
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
			}else if(toUnit=='cm'){				
				if(fromUnit=='mm'){					
					calValue = (value / 10);
				}else if(fromUnit=='in'){					
					calValue = (value * 2.54);
				}		
			}else if(toUnit=='m'){				
				if(fromUnit=='mm'){					
					calValue = (value / 1000);
				}else if(fromUnit=='in'){					
					calValue = (value * 0.0254);
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
			}else if(toUnit=='sf'){
				if(fromUnit=='mm'){
					calValue = (value * 0.0000107639104);					
				}else if(fromUnit=='in'){
					calValue = (value / 144);
				}
			}		
			//calValue = Math.round((calValue*100)/100);
			calValue = ajaxUtil.roundNumber(calValue,5);
			//alert("fromUnit="+fromUnit+", "+"toUnit="+toUnit + ",  value="+value + ",  calValue=" + calValue);
			return calValue;
		},
		
		addGroup: function(){
			var trLast = $('tbody[id=mfgtdEntryTableBody]').find("tr:last");
			var grpCount = $('tbody[id=mfgtdEntryTableBody]').find("tr").size();
			var trGroup = mfgUtil.getGroupElement();
			
			grpCount = $('tbody[id=mfgtdEntryTableBody]').find("[name=chkDetailGroup]").size();
			
			if(grpCount==0){
				$(trGroup).insertBefore($('tbody[id=mfgtdEntryTableBody]').find("tr:first"));
			}else{
				$(trGroup).appendTo($('tbody[id=mfgtdEntryTableBody]'));
			}			
		},
		
		getGroupElement: function(){
			var rowTd = $('#mfgtdClone').find("td");
			var trGroup = "";
			trGroup = "<tr><td><input name='chkDetailGroup' type='checkbox'>" +
			"<input type='hidden' name='mfgtdgrprow-status' value='N' />" +
			"<input type='hidden' name='mfgtdgrpgroupid' value='' />" +
			"<input type='hidden' name='mfgtdgrpgrpcode' value='' />" +
			"</td>"+
			"<td colspan='"+(rowTd.size()-1)+"'><input type='text' size='80' name='mfgtdgrpgrpdesc' /></td></tr>";
			
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
			
			var trGroup = mfgUtil.getGroupElement();
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
				$('tbody[id=mfgtdEntryTableBody]').find("tr").each(function(i){
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
			$('tbody[id=mfgtdEntryTableBody]').find("tr").each(function(i){
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
		},				
		
		buildTableEntry: function(ele){			
			var customDialog = $("#CustomiseDialog");
			var dialog_buttons = {};
			var trEle = $("#customCloneAddForm");
			var customisedata = trEle.find("input[name$=customisedata]").val();
			var measurementEle = trEle.find("input[name$=measurement]");
			var widthEle = trEle.find("input[name$=width]");
			var heightEle = trEle.find("input[name$=height]");
			var option1 = trEle.find("select[name$=opt1]");
			var prodCode = trEle.find("select[name$=opt1]");
			var prodid = trEle.find("[name$=prodid]").val();
			
			var prodJson = eval('productMainjson.data.prod'+prodid);			
			var customiseContent = customDialog.find("#customiseContent");
			var measurement = mfgUtil.getMeasurement(measurementEle.val());	
			
			RowData.customData = customisedata; 
			RowData.customFormula = prodJson.customformula;
			
			if(measurementEle){
				RowData.width=parseFloat(measurement[0]);
				RowData.height=parseFloat(measurement[1]);
			}else{
				RowData.width=parseFloat(widthEle.val());
				RowData.height=parseFloat(heightEle.val());
			}
			if(widthEle.attr("readonly")=='' && widthEle.val()==''){
				alert("Please enter Width");
				return;
			}
			if(heightEle.attr("readonly")=='' && heightEle.val()==''){
				alert("Please enter Height");
				return;
			}
			var data;

			if(customisedata==''){
				customisedata = eval(RowData.customFormula+"_customise()");											
			}
			
			data = eval(customisedata);
			customDialog.find("input[name=col]").val(data.colw.length);
			customDialog.find("input[name=row]").val(data.rowh.length-1);	
			customiseContent.empty();
			$(mfgUtil.buildDiv(trEle, parseFloat(widthEle.val()), parseFloat(heightEle.val()), data)).appendTo(customiseContent);			
			
			var totalSpan = 0;
			var counter = 0;
			var spanArray = new Array();
			for(var a=0; a<data.span.length; a++){					
				totalSpan = data.span[a].length;
				for(var b=0; b<totalSpan; b++){	
					spanArray[counter] = data.span[a][b];
					counter++;			
				}
			}
			$("div[id=optSpan]").each(function(i){
				var cloneOpt = option1.clone();				
				//cloneOpt.hide();								
				cloneOpt.appendTo($(this));							

				if(spanArray[i]!=''){						
					cloneOpt.val(spanArray[i]);								
					var text = cloneOpt.find("option:selected").text();
					$(this).append($("<span>"+text+"</span>"));
				}else{
					$(this).append($("<span>Select Option</span>"));
				}
			});
			mfgUtil.resizeTable();
			mfgUtil.attachTdEvent();
			customDialog.unbind();
			customDialog.bind("change", function(event){
				var element = event.target;
				var val = 0;
				if(element.name=='colw' || element.name=='rowh'){
					mfgUtil.resizeTable();				
				}
				if(element.name=='col'){ // Span
					var col = parseInt(element.value);				
					var colDiv = $("div[id=colDiv]");					
					var dif = col - colDiv.size();					
					if(dif>0){				
						ajaxUtil.cloneObject(colDiv.last()).appendTo("#containerDiv");								
					}else{
						// Remove
						colDiv.each(function(i){							
							if(col<=i){								
								$(this).remove();
							}
						});						
					}					
					mfgUtil.resizeTable();
					mfgUtil.attachTdEvent();
				}
				if(element.name=='row'){ // Divider
					var row = parseInt(element.value) + 1;
					var colDiv = $(element).parents("div[id=colDiv]");					
					var spanDiv = colDiv.find("div[id=spanDiv]");										
					var dif = row - spanDiv.size();
					
					if(dif>0){
						// Add						
						for(var i=0;i<dif;i++){		
							ajaxUtil.cloneObject(spanDiv.last()).appendTo(colDiv);
						}						
					}else{
						// Remove
						spanDiv.each(function(i){							
							if(row<=i){								
								$(this).remove();
							}
						});
					}						
					mfgUtil.resizeTable();
					mfgUtil.attachTdEvent();
				}
				
				// attach event to eqbalHeight
				if(element.id=='eqbalHeight'){					
					if(element.value=='EQ'){
						$(element).parent().find("input[name=rowh]").attr("readonly","readonly");
					}else{
						$(element).parent().find("input[name=rowh]").attr("readonly","");
					}
					
					mfgUtil.resizeTable();
				}
			
				// attach event to eqbalWidth
				if(element.id=='eqbalWidth'){					
					if(element.value=='EQ'){
						$(element).parent().find("input[name=colw]").attr("readonly","readonly");
					}else{
						$(element).parent().find("input[name=colw]").attr("readonly","");
					}
					mfgUtil.resizeTable();
				}
				
				event.preventDefault();
			});			
			dialog_buttons['Done'] = function(){		
				customisedata = mfgUtil.buildData();		
				var span = (eval(customisedata)).span;	
				var totalSpan = 0;
				for(var a=0; a<span.length; a++){					
					totalSpan = span[a].length;
					for(var b=0; b<totalSpan; b++){																
						if(span[a][b]==''){
							alert("Please select option");
							return;
						}				
					}
				}			
				
				trEle.find("input[name$=customisedata]").val(customisedata);
				mfgUtil.calculate($("#customCloneAddForm"));
				customDialog.dialog("close");
				
			};
			customDialog.dialog('option', 'buttons', dialog_buttons);
			customDialog.dialog('open');					
		},
		
		resizeTable: function(){			
			var customiseContent = $("#customiseContent");
			var widthArray = [];
			var val;
			var eqbalHeight = $("select[id=eqbalHeight]");
			var eqbalWidth = $("select[id=eqbalWidth]");
			
			// To Calculate Back Actual Width Height			
			RowData.customData = mfgUtil.buildData();									
			eval(RowData.customFormula+"_customise()");
			// Calculate EQHeight
			var totalNotEqualHeight = 0;
			var eqOptionCount=0;
			
			$("div[id=colDiv]").each(function(i){	
				eqOptionCount = 0;
				totalNotEqualHeight = 0;
				eqbalHeight = $(this).find("select[id=eqbalHeight]");	
				//alert("eqbalHeight.size() = "+eqbalHeight.size());	
				eqbalHeight.each(function(x){
					if($(this).val()=='EQ'){
						eqOptionCount++;
					}else{
						val = $(this).parent().find("input[name=rowh]").val();
						totalNotEqualHeight += parseFloat(val);					
					}
				});	
				
				if(eqOptionCount>0){
					//alert("RowData.customHeight["+i+"] = "+RowData.customHeight[i]);
					var leftHeight = RowData.customHeight[i] - totalNotEqualHeight;
					var eqHeight = leftHeight / eqOptionCount;
					eqbalHeight.each(function(i){				
						if($(this).val()=='EQ'){						
							$(this).parent().find("input[name=rowh]").val(ajaxUtil.roundNumber(eqHeight, 2));
						}
					});				
				}
			});
						
			// Calculate EQWidth
			var totalNotEqualWidth = 0;
			eqOptionCount=0;
			eqbalWidth.each(function(i){				
				if($(this).val()=='EQ'){
					eqOptionCount++;
				}else{
					val = $(this).parent().find("input[name=colw]").val();
					totalNotEqualWidth += parseFloat(val);
				}
			});
			
			if(eqOptionCount>0){
				var leftWidth = RowData.customWidth - totalNotEqualWidth;
				var eqWidth = leftWidth / eqOptionCount;
				eqbalWidth.each(function(i){				
					if($(this).val()=='EQ'){						
						$(this).parent().find("input[name=colw]").val(ajaxUtil.roundNumber(eqWidth, 2));
					}
				});				
			}
			
			// Resize Column
			$("div[id=colDiv]").each(function(i){						
				val = $(this).find("input[name=colw]").val();	
				$(this).width(val * mfgUtil.ratio + 5);
				$(this).find("div[id=spanDiv]").width(val * mfgUtil.ratio);
				//$(this).find("div[id=spanDiv]").attr("width",);
			});

			// Resize Row
			$("div[id=colDiv]").each(function(i){
				var spanDiv = $(this).find("div[id=spanDiv]");
				var rowCount = spanDiv.size();
				spanDiv.each(function(x){
					val = $(this).find("input[name=rowh]").val();
					$(this).height(val * mfgUtil.ratio - (rowCount*0.6));
				});						
				//$(this).find("div[id=spanDiv]").attr("width",);
			});	
		}, 
		
		attachTdEvent: function(){
			$("div[id=optSpan]").unbind();
			$("div[id=optSpan]").each(function(i){
				if(i==0){		
					$(this).find('select').unbind();
					$(this).find('select').bind("change", (function(event){
						var text = $(this).find("option:selected").text();						
						$("div[id=optSpan] select").each(function(x){							
							if($(this).val()==''){
								$(this).parent().find("span").remove();
								$(this).val(event.target.value);																
								$(this).parent().append($("<span>"+text+"</span>"));
							}
						});						
					}));
				}				
				$(this).hover(
					function () {
						$(this).find("span").remove();
				    	$(this).find("select").show();
				  	}, 
				  	function () {
				  		var select = $(this).find("select");
				    	select.hide();
				    	$(this).find("span").remove();
				    	if(select.val()!=''){
					    	var text = select.find("option:selected").text();					    	
							$(this).append($("<span>"+text+"</span>"));
						}else{
							$(this).append($("<span>Select Option</span>"));
						}
				  	}
				);
				
			});
		},
		
		buildData: function(){
			var colw="";
			var rowh="";
			var span="";
			var data = "({";
			
			colw = "'colw':[";
			$("input[name=colw]").each(function(i){
				colw+=$(this).val()+",";				
			});				
			if(colw!='') colw = colw.substring(0,colw.length-1);
			colw += "],";
			
			var eqbalWidth = "'eqbalWidth':[";
			$("select[id=eqbalWidth]").each(function(i){
				eqbalWidth+="'"+$(this).val()+"',";
			});				
			if(eqbalWidth!='') eqbalWidth = eqbalWidth.substring(0,eqbalWidth.length-1);
			eqbalWidth += "],";
			
			var colDiv = $("div[id=colDiv]");
			var rowDiv;			
			var eqbalHeight;
			
			eqbalHeight = "'eqbalHeight':[";			
			rowh 		= "'rowh':[";
			span 		= "'span':[";
			colDiv.each(function(i){
				rowh+="[";	
				rowDiv = $(this).find("div[id=spanDiv]");
				
				rowDiv.find("input[name=rowh]").each(function(i){
					rowh+=$(this).val()+",";
				});	
				if(rowh!='') rowh = rowh.substring(0,rowh.length-1);
				rowh += "],";
				
				eqbalHeight+="[";	
				rowDiv.find("select[id=eqbalHeight]").each(function(i){
					eqbalHeight+="'"+$(this).val()+"',";
				});	
				if(eqbalHeight!='') eqbalHeight = eqbalHeight.substring(0,eqbalHeight.length-1);
				eqbalHeight += "],";
				
				span+="[";	
				rowDiv.find("div[id=optSpan] select").each(function(i){
					span+="'"+$(this).val()+"',";					
				});	
				
				if(span!='') span = span.substring(0,span.length-1);
				span += "],";
			});
			if(rowh!='') rowh = rowh.substring(0,rowh.length-1);
			rowh += "],";	
			
			if(eqbalHeight!='') eqbalHeight = eqbalHeight.substring(0,eqbalHeight.length-1);
			eqbalHeight += "]";			
			
			if(span!='') span = span.substring(0,span.length-1);
			span += "],";	
					
			data += colw + rowh + span + eqbalWidth + eqbalHeight + "})";	
			//alert("buildData data = "+data);		
			return data;
		},
		
		buildDiv: function(trEle, width, height, data){
			var divStr = '';
			var td = '';
			var tr = '';
			var eqbalWidth;
			var eqbalHeight;
			var colCount, rowCount;
			
			colCount = data.colw.length;
			mfgUtil.ratio = mfgUtil.tableHeight / height;
						
			divStr = "<div id='containerDiv'>";	
			
			var spanCounter = 0;			
			var colStr = "";
			var colw, rowh;
			for(var i=0; i<colCount; i++){	
				rowCount = data.rowh[i].length;
				colw = ajaxUtil.roundNumber(data.colw[i]*mfgUtil.ratio,0);					
				colStr = "<div id='colDiv' style='width:"+(colw+5)+"px; float:left;'>"; // Column Div				
					colStr += "<div class='headerSpan' style='width:100%; height:60px;'>";
					colStr += "<b>Divider:</b> <input type='textbox' size='5' name='row' value='"+(rowCount-1)+"'><br>";
					colStr += "<b>Type:</b> <select id='eqbalWidth'><option value=''></option><option value='EQ'" +(data.eqbalWidth[i]=='EQ'?'selected':'')+">EQ</option></select><br>";
					colStr += "<b>Width:</b> <input type='textbox' "+((data.eqbalWidth[i]=='EQ'?"readonly='readonly'":""))+"' name='colw' size='10' value='"+data.colw[i]+"'>";
					colStr+="</div>";
				
				for(var h=0; h<rowCount; h++){
					rowh = ajaxUtil.roundNumber(data.rowh[i][h]*mfgUtil.ratio,0);					
					colStr += "<div id='spanDiv' class='customSpan' style='width:"+(colw)+"px; height:"+(rowh)+"px;'>"; // Span Div
					colStr += "<b>Type:</b> <select id='eqbalHeight'><option value=''></option><option value='EQ'" +(data.eqbalHeight[i][h]=='EQ'?'selected':'')+">EQ</option></select>";
					colStr += "<br><b>Height:</b> <input type='textbox' "+((data.eqbalHeight[i][h]=='EQ'?"readonly='readonly'":""))+"' name='rowh' size='10' value='";
					colStr += data.rowh[i][h] + "'>";
					colStr += "<br><b>Option:</b> <div id='optSpan'></div>";
					colStr += "</div>";
				}
				colStr += "</div>";							
				divStr+=colStr;					
			}	
			divStr+="</div>";							
			return divStr;						
		},
		
		buildTable: function(trEle, width, height, data){
			var table = '';
			var td = '';
			var tr = '';
			var eqbalWidth;
			var eqbalHeight;
			var colCount, rowCount;
			
			if(data.colw){
				colCount = data.colw.length + 1;				
			}
			
			if(data.rowh){
				rowCount = data.rowh.length + 1;
				mfgUtil.ratio = mfgUtil.tableHeight / height;
			}			
			table = "<table>";	
			
			var spanCounter = 0;			
			_m = trEle.find("input[name$=measurement]").val();			
				
			for(var i=0; i<rowCount; i++){				
				td = '';
				
				for(var x=0; x<colCount; x++){					
					if(i==0){
						if(x==0){
							td += "<th width=100></th>";
						}else{	
							eqbalWidth = "<select id='eqbalWidth'><option value=''></option><option value='EQ'" +(data.eqbalWidth[x-1]=='EQ'?'selected':'')+">EQ</option></select>";					
							td += "<th width="+data.colw[x-1] * mfgUtil.ratio+" align='left'>"+eqbalWidth+"<br><br><input type='textbox' "+((data.eqbalWidth[x-1]=='EQ'?"readonly='readonly'":""))+"' style='text-align: right;' name='colw' size='10' value='";
							//if(_m.indexOf('in')!=-1){	
							//	td += mfgUtil.convertMmToInch(data.colw[x-1]) + 'in';
							//}else{
								td += data.colw[x-1];
							//}
							td += "'></th>";
						}
					}else{
						if(x==0){
							eqbalHeight = "<select id='eqbalHeight'><option value=''></option><option value='EQ'" +(data.eqbalHeight[i-1]=='EQ'?'selected':'')+">EQ</option></select>";
							td += "<td width=100>"+eqbalHeight+"<br><br><input type='textbox' "+((data.eqbalHeight[i-1]=='EQ'?"readonly='readonly'":""))+"' style='text-align: right;' name='rowh' size='10' value='";
							//if(_m.indexOf('in')!=-1){	
							//	td += mfgUtil.convertMmToInch(data.rowh[i-1]) + 'in';
							//}else{
								td += data.rowh[i-1];
							//}							
							td += "'></td>";
						}else{														
							td += "<td class='customPanel' width="+data.colw[x-1] * mfgUtil.ratio+" id='optSpan'>";
							td += "</td>";
							spanCounter++;
						}
					}					
				}
				if(i==0){
					tr += "<thead><tr height='50'>"+td+"</tr></thead>";
				}else{
					if(i==1){
						tr += "<tbody>";
					}
					tr += "<tr height='"+data.rowh[i-1] * mfgUtil.ratio+"'>"+td+"</tr>";					
				}
				// Last Row
				if((i+1)==rowCount){
					tr += "</tbody>";
				}
				
			}
			
			
			table+=tr+"</table>";								
			return table;						
		},
		
		buildDefaultData: function(col, row){
			//Format = ({'colw':[1407.5,1407.5],'eqbalWidth':['EQ','EQ'],'rowh':[[993.5,993.5],[993.5,993.5]],'eqbalHeight':[['EQ','EQ'],['EQ','EQ']],'span':[]})			
			var tdWidth = 0;
			var trHeight = 0;
			var widthStr = "";
			var widthEq = "";
			var heightStr = "";
			var heightEq = "";
			
			
			tdWidth = ajaxUtil.roundNumber(tdWidth, 2);
			trHeight = ajaxUtil.roundNumber(trHeight, 2);
			
			customisedata = "({";
			//customisedata += "'col':"+col+",";		
					
			for(var i=0; i<col; i++){
				widthStr += tdWidth + ",";
				widthEq += "'EQ',";
			}
			
			widthStr = widthStr.substring(0, widthStr.length-1);
			widthEq = widthEq.substring(0, widthEq.length-1);
			customisedata += "'colw':["+widthStr+"],";
			customisedata += "'eqbalWidth':["+widthEq+"],";
			
			
			for(var i=0; i<col; i++){		
				heightStr += "[";	heightEq += "[";
				for(var h=0; h<row; h++){	
					heightStr += trHeight;			
					heightEq += "'EQ'";
					if(h+1<row){
						heightStr += ",";
						heightEq += ",";
					}
				}
				heightStr += "]";
				heightEq += "]";
				if(i+1<col){
					heightStr += ",";
					heightEq += ",";
				}						
			}
			heightStr = "'rowh':["+heightStr+"],";
			heightEq  = "'eqbalHeight':["+heightEq+"],";
			
			customisedata += heightStr;
			customisedata += heightEq;
					
			customisedata += "'span':[]})";	
			//alert("buildDefaultData = "+customisedata);
			return customisedata;
		}
		
		
	}


var mfgUtil;
$(document).ready(function(){mfgUtil = new MFGUtilClass();});