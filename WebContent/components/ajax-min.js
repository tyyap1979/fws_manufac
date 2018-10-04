var startTime;
function Util(){this.init()
}Util.prototype={init:function(){this.buildOverlayAndPanel();
this.buildValidate();
this.attachAutoSuggestEvent($(document));
this.attachDatePicker($(document));
$(".tabs").tabs()
},attachDatePicker:function(a){a.find(".datepicker").each(function(b){$(this).datepicker();
$(this).datepicker("option","dateFormat","yy-mm-dd")
})
},resetGlobalComponentEvent:function(a){ajaxUtil.attachAutoSuggestEvent(a)
},attachAutoSuggestEvent:function(a){a.find("[class*=autosuggest]").each(function(c){var g=$(this).parents("tr");
var d=$(this).attr("class");
var b="";
if(g){b=g.attr("id")
}if(b.indexOf("Clone")==-1&&(d.indexOf("ui-autocomplete-input")==-1)){var f=d.split(" ");
for(var c=0;
c<f.length;
c++){if(f[c].indexOf("autosuggest")!=-1){d=f[c]
}}var e=d.substring(0,d.indexOf("-"))+".htm?return=json&action1=action.auto.suggest";
var h=$(this).attr("name");
h=h.substring(0,h.lastIndexOf("_value"));
$(this).autocomplete({source:e,minLength:2,highlight:true,change:function(i,k){var j=$(this).val();
if(j==""){$(this).parents("tr").find("input[name="+h+"]").val("")
}},select:function(i,j){$(this).parents("tr").find("input[name="+h+"]").val(j.item.key);
if(typeof(processAfterAutoComplete)=="function"){processAfterAutoComplete(j.item)
}}})
}})
},dynamicSelectLoad:function(){var a="";
$(document).find("select[class*=dynamicselect]").each(function(c){var d=$(this).attr("class");
var g=$(this);
var f=d.split(" ");
for(var c=0;
c<f.length;
c++){if(f[c].indexOf("dynamicselect")!=-1){d=f[c];
break
}}mod=d.substring(0,d.lastIndexOf("-"));
if(a.indexOf(mod+",")==-1){a+=mod+",";
var b="select.htm";
var e="return=json&action1=action.dynamic.select&mod="+mod;
$.ajax({type:"POST",url:b,data:e,cache:false,dataType:"json",success:function(h){g=$(document).find("select[id="+g.attr("id")+"]");
g.empty();
g.append($(h.options))
}})
}})
},customTrim:function(a){if(a){a=a.replace(/(^\s*)|(\s*$)/g,"")
}return a
},bindLink:function(){$("a[href!='#']").bind("click",(function(a){if($(this).attr("href").indexOf("#")==-1&&$(this).attr("name")!="fb_share"){ajaxUtil.showPanel("",$("#js-processing-request").html(),null)
}}));
$(window).unload(function(){setTimeout(function(){ajaxUtil.hidePanel()
},3000)
})
},postJsonQuery:function(a,b,d,c){if(!a){a=window.location.pathname
}if(a.substr(a.length-5,5)==".html"){a=a.substring(0,a.length-1)
}$.ajax({type:"POST",url:a,data:b,cache:false,dataType:"json",success:d})
},updater:function(b,a){$.ajax({type:"POST",url:a,success:function(c){$("#"+b).html(c);
$("#"+b).dialog("open");
ajaxUtil.hidePanel()
}})
},getForm:function(a){var b=$(a);
while(b.get(0).tagName!="FORM"){b=b.parent()
}return b
},buildValidate:function(){var a="<span id='reqmark'>*</span>";
var b;
$(".validateForm").find("input[class^='validate'], select[class^='validate'], textarea[class^='validate']").each(function(c){if($(this).attr("type")!="radio"){b=$(this).parent().prev("th");
b.find("#reqmark").remove();
$(a).appendTo(b)
}})
},validateForm:function(c,f){var j;
if(f==null){j=ajaxUtil.getForm(c)
}else{j=f
}var h="";
var e="";
var k="";
var d="";
var b;
var a="";
var g="";
var i=true;
j.find("input, select, textarea").each(function(m){b=$(this).parent();
d=$(this).attr("class");
h=$(this).attr("name");
e=$(this).attr("type");
k=$(this).val();
if(d!=""&&e!="radio"){if(b.find("#display-msg").size()==0){$("<div id='display-msg'></div>").appendTo(b)
}else{b.find("#display-msg").html("")
}a=d.split(" ");
g="";
for(var m=0;
m<a.length;
m++){if(a[m]=="required"){if(k==""){g=$("#js-required").html();
i=false;
break
}}else{if(a[m].indexOf("match-with")!=-1){var l=a[m].substring("match-with".length+1);
if(k!=j.find("[name='"+l+"']").val()){g=$("#js-password-field-match").html();
i=false;
break
}}}}b.find("#display-msg").html(g)
}});
return i
},formData2QueryString:function(a,b){var f="";
var g;
var c="";
var e="";
var d="";
var g=$(a);
g.find("input, select, textarea").each(function(h){c=$(this).attr("name");
d=$(this).val();
if(this.tagName.toLowerCase()=="input"){e=$(this).attr("type")
}else{e=this.tagName.toLowerCase()
}switch(e){case"text":case"select":case"hidden":case"password":case"textarea":f+=c+"="+encodeURIComponent(ajaxUtil.customTrim(d))+"&";
break;
case"checkbox":if(b){if($(this).attr("checked")){f+=c+"=Y&"
}else{f+=c+"=N&"
}}else{if($(this).attr("checked")){f+=c+"="+ajaxUtil.customTrim(d)+"&"
}}break;
case"radio":if($(this).is(":checked")){d=$(this).attr("id").replace(c,"");
f+=c+"="+ajaxUtil.customTrim(d)+"&"
}break
}});
return f
},reverseConvert:function(e){var d="";
var a;
var b=0;
var c=0;
if(e.indexOf("&#x")!=-1){while((b=e.indexOf("&#x",b))!=-1){c=e.indexOf(";",b+2);
a=e.substring(b+3,c);
d=String.fromCharCode(parseInt(a,16));
e=e.substring(0,b)+d+e.substring(c+1,e.length)
}}else{while((b=e.indexOf("&#",b))!=-1){c=e.indexOf(";",b+2);
a=e.substring(b+2,c);
d=String.fromCharCode(a);
e=e.substring(0,b)+d+e.substring(c+1,e.length)
}}return e
},convertToEntities:function(d){var b=d;
var a="";
if(b!=null){for(var c=0;
c<b.length;
c++){if(b.charCodeAt(c)>127){a+="&#"+b.charCodeAt(c)+";"
}else{a+=b.charAt(c)
}}}return(a)
},commaFormatted:function(f){if(f==0){return 0
}var c=",";
var b=f.split(".",2);
var h=b[1];
var e=parseInt(b[0]);
if(isNaN(e)){return""
}var g="";
if(e<0){g="-"
}e=Math.abs(e);
var k=new String(e);
var b=[];
while(k.length>3){var j=k.substr(k.length-3);
b.unshift(j);
k=k.substr(0,k.length-3)
}if(k.length>0){b.unshift(k)
}k=b.join(c);
if(h.length<1){f=k
}else{f=k+"."+h
}f=g+f;
return f
},showPanel:function(b,a,c){var g=new Date();
startTime=g.getTime();
try{$("#dialog").html("<div class='panelMessage'>"+a+"</div>");
if(c){$("#dialog").dialog("option","buttons",c)
}$("#dialog").dialog("open")
}catch(f){alert(f)
}},updatePanelMessage:function(a,b){if(b){$("#dialog").dialog("option","buttons",b)
}$("#dialog").html("<div class='panelMessage'>"+a+"</div>")
},hidePanel:function(){var b=new Date();
var a=800-(b.getTime()-startTime);
if(a>0){setTimeout(function(){$("#dialog").dialog("close")
},a)
}else{$("#dialog").dialog("close")
}},buildOverlayAndPanel:function(){var b="";
var a='<div id="dialog" title="&nbsp;">'+b+"</div>";
$(a).appendTo("body");
$("#dialog").dialog({autoOpen:false,modal:true})
},loadUploader:function(e,f,b,d,a){if(!b){b="*.*"
}if(!d){d="All Files"
}if(!a){a="1 MB"
}f["return"]="json";
if(swfu){}else{var c={flash_url:"/components/swfupload/swfupload2.5b3.swf",upload_url:e,post_params:f,file_size_limit:a,file_types:b,file_types_description:d,file_upload_limit:50,file_queue_limit:0,custom_settings:{progressTarget:"fsUploadProgress",cancelButtonId:"btnCancel"},debug:false,button_placeholder_id:"spanButtonPlaceholder",button_text:"<span class='uploadButton'>Upload</span>",button_text_style:".uploadButton { font-weight: bold;text-decoration: underline; font-family: verdana; font-size: 12px; }",button_text_top_padding:0,button_text_left_padding:0,button_width:60,button_height:18,button_cursor:SWFUpload.CURSOR.HAND,swfupload_loaded_handler:swfUploadLoaded,file_queued_handler:fileQueued,file_queue_error_handler:fileQueueError,file_dialog_complete_handler:fileDialogComplete,upload_start_handler:uploadStart,upload_progress_handler:uploadProgress,upload_error_handler:uploadError,upload_success_handler:uploadSuccess,upload_complete_handler:customUploadComplete,queue_complete_handler:customQueueComplete};
swfu=new SWFUpload(c)
}},cloneObject:function(e){var c=e.clone(true);
var b=e.find("select");
var d=c.find("select");
var a=[];
b.each(function(f){a.push($(this).val())
});
d.each(function(f){$(this).val(a.shift())
});
return c
},roundNumber:function(a,c){var b=Math.round(a*Math.pow(10,c))/Math.pow(10,c);
b=b.toFixed(c);
return b
}};
var swfu;
var ajaxUtil;
$(document).ready(function(){ajaxUtil=new Util()
});