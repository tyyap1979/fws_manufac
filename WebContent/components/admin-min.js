var AdminConfig={overlayOpacity:0.8,overlayMinTime:500,sortableField:"sortableField",actionUpload:"action.upload",actionSave:"action.save",actionAddRecord:"action.addrecord",actionShowMoreSearch:"action.show.more.search",checkAllAction:"action.check.all",checkNoneAction:"action.check.none",sortName:"sortName",sortDirectionName:"sortDirection",actionName:"action1",actionCreate:"action.create",actionUpdate:"action.update",actionRetrieveEdit:"action.retrieve.edit",actionRetrieveCopy:"action.retrieve.copy",actionDelete:"action.delete",actionSearch:"action.search",entryForm:"entryForm",searchForm:"searchForm",listingForm:"listingForm",sortableColumnAscClass:"ui-state-active",sortableColumnDescClass:"ui-state-active"};
Admin.prototype={startTime:undefined,sortBy:new Array(),sortByDirection:new Array(),activeModuleId:"",currentElement:undefined,activeEditTR:undefined,initialize:function(){var a=this;
a.checkSession();
a.processListingEvent();
a.commonEventListener();
this.buildPanel();
$(".validateForm").validationEngine();
this.buildSorting()
},checkSession:function(){var a="return=json&action1=checksession";
ajaxUtil.postJsonQuery(null,a,function(b){if(b.redirect){window.location=b.redirect;
return
}},null)
},processSearchEvent:function(){$("div[class=searchDiv]").each(function(a){$(this).bind("click",(function(e){var c=e.target;
if(c.id==AdminConfig.actionShowMoreSearch){var d;
var f=$(this).find("#searchMoreOption");
if(f.is(":visible")){f.hide()
}else{f.show()
}d=c.innerHTML;
c.innerHTML=c.rel;
c.rel=d;
e.preventDefault();
return
}if(c.id==AdminConfig.actionSearch){var g=$(this).attr("id");
adminUtil.activeModuleId=g+"Module";
adminUtil.searchRecord(c);
e.preventDefault();
return
}}));
if(typeof(autoSearch)!="undefined"&&autoSearch==false){}else{var b=$(this).attr("id");
adminUtil.activeModuleId=b+"Module"
}})
},commonEventListener:function(){$("#btnSubmit").bind("click",(function(b){var a=b.target;
adminUtil.commonJsonSubmit(ajaxUtil.getForm($(a)));
b.preventDefault();
return
}));
$("#btnSave").click(function(b){var c=$("#"+adminUtil.activeModuleId+"Entry");
var a=$.validationEngine.submitValidation(c.find(".validateForm"));
if(!a){adminUtil.saveRecord()
}b.preventDefault();
return
});
$("#btnCancel").click(function(a){adminUtil.hideEntryEvent();
a.preventDefault();
return
});
$("#btnPrint").click(function(b){var c;
$("textarea[class=editor]").each(function(d){c=nicEditors.findEditor($(this).attr("id"));
if(c){$(this).val(c.getContent())
}});
var a=window.open("print.htm","printWindow","height=600,width=800,menubar=yes,scrollbars=yes");
b.preventDefault();
return
})
},processListingEvent:function(){$("[class$=listingTable]").each(function(a){$(this).bind("click",(function(b){var f=b.target;
currentElement=f;
if(f.id==AdminConfig.actionDelete){var c=$(this).attr("id");
adminUtil.activeModuleId=c+"Module";
var k={};
k.Yes=function(){adminUtil.getModuleElement(AdminConfig.listingForm).find("input[name=action1]").val(AdminConfig.actionDelete);
adminUtil.deleteRecord();
return
};
k.No=function(){ajaxUtil.hidePanel();
return
};
ajaxUtil.showPanel("","Confirm Delete?",k);
b.preventDefault();
return
}if(f.id==AdminConfig.actionAddRecord){if(typeof(beforeAdd)=="function"){beforeAdd()
}var c=$(this).attr("id");
adminUtil.activeModuleId=c+"Module";
$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionCreate);
adminUtil.formReset();
adminUtil.resetClone(false);
adminUtil.showEntryEvent();
b.preventDefault();
return
}if(f.id=="action.clone.add"){adminUtil.rowClone($(f),true);
b.preventDefault();
return
}if(f.id=="action.clone.add.new"){adminUtil.rowClone($(f),false);
b.preventDefault();
return
}if(f.id=="action.clone.delete"){var j=$(f).parent();
while(j.get(0).tagName!="TABLE"){j=j.parent()
}var c=j.attr("id");
var e=c.substring(0,c.indexOf("EntryTable"));
var m=j.find("#"+e+"EntryTableBody");
var d=j.find("thead");
var i=d.find("[id="+e+"Clone]");
var h=i.get(0).tagName;
var g;
m.children(h).each(function(p){g=$(this).find("input[type=checkbox]");
$.validationEngine.closePrompt($(this));
if(g.attr("checked")){var o=$(this).find("input[name$=row-status]").val();
if(o=="N"){$(this).remove()
}else{$(this).find("input[name$=row-status]").val("D");
$(this).hide()
}}});
if(typeof(afterCloneDelete)=="function"){afterCloneDelete()
}adminUtil.updateSorting(m);
b.preventDefault();
return
}if(f.id==AdminConfig.actionRetrieveEdit){var n=$(f).closest("tr");
adminUtil.activeEditTR=n;
adminUtil.editPopUp(f);
b.preventDefault();
return
}if(f.id==AdminConfig.actionRetrieveCopy){adminUtil.activeModuleId=$(this).attr("id")+"Module";
var l="#"+adminUtil.activeModuleId+" #"+AdminConfig.listingForm;
var g=$(l).find("input[type=checkbox]:checked");
if(g.size()>1){alert("Please select only ONE record for copying.")
}else{g.each(function(p){if($(this).attr("checked")){var o=$(f).closest("tr");
adminUtil.activeEditTR=o;
adminUtil.copyPopUp($(this))
}})
}b.preventDefault();
return
}if(typeof(processListingEventCustom)=="function"){adminUtil.activeModuleId=$(this).attr("id")+"Module";
processListingEventCustom(f.id)
}}))
})
},updateSorting:function(a){a.children("tr").each(function(b){$(this).find("input[name$=position]").val(b+1)
})
},buildSorting:function(){var b=function(d,c){c.children().each(function(){$(this).width($(this).width())
});
return c
};
var a=function(f,d){var c=f.target;
$("#"+c.id).children("tr").each(function(e){$(this).find("input[name$=position]").val(e+1)
});
return d
};
$(".sortable tbody").sortable({helper:b,stop:a})
},addCloneID:function(c){var b=new Date().getTime();
var a="";
c.find("input, select, textarea").each(function(d){a=$(this).attr("name");
$(this).attr("id",new Date().getTime()+"-"+a)
})
},rowClone:function(f,g){var j=f.parents("table");
var a=j.attr("id");
var d=a.substring(0,a.indexOf("EntryTable"));
var m=j.find("#"+d+"EntryTableBody");
var c=j.find("thead");
var i=c.find("[id="+d+"Clone]");
var n;
var b=m.find("input[name=chkDetail]:checked");
if(b.size()>0){i=b.parents("tr")
}else{if(g&&m.children("tr").size()>0){i=m.children("tr:last");
if(i.find("[name=chkDetailGroup]").size()>0){i=c.find("[id="+d+"Clone]")
}}}n=i.clone(true);
var k=i.find("select");
var l=n.find("select");
var h=[];
var e=[];
k.each(function(o){h.push($(this).val());
e.push($(this).attr("name"))
});
l.each(function(o){$(this).val(h.shift());
$(this).attr("name",e.shift())
});
n.attr("id","");
n.find("input[type=checkbox]").attr("checked",false);
n.find("input[type=text]").val("");
n.find("input[type=hidden]").val("");
n.find("input[name$=row-status]").val("N");
n.find("img").attr("src","");
adminUtil.addCloneID(n);
n.show();
if(b.size()>0){n.insertAfter(i);
i.find("input[name=chkDetail]:checked").attr("checked",false)
}else{n.appendTo(m)
}ajaxUtil.resetGlobalComponentEvent(m);
adminUtil.updateSorting(m);
return n
},getModuleElement:function(a){return $("#"+this.activeModuleId).find("#"+a)
},buildPanel:function(){$(".dialogView").each(function(a){try{var b=$(this).width();
if($(this).css("vertical-align")=="top"){$(this).dialog({autoOpen:false,position:"top",modal:true,width:b,minHeight:500,close:function(d,e){$(".formError").remove()
}})
}else{$(this).dialog({autoOpen:false,modal:true,width:b,minHeight:500,close:function(d,e){$(".formError").remove()
}})
}}catch(c){alert(c)
}})
},showPanelSuccess:function(a){ajaxUtil.showPanel(null,"<div id='overlayMsg_progBod_text'>"+a+"</div>",null)
},hideOverlay:function(){var b=new Date();
var a=AdminConfig.overlayMinTime-(b.getTime()-startTime);
if(a>0){setTimeout(function(){ajaxUtil.hidePanel()
},a)
}else{ajaxUtil.hidePanel()
}},checkAll:function(a){var b;
a.find("input[type='checkbox']").each(function(c){b=$(this).parents("tr").attr("id");
if(!adminUtil.endsWith(b,"Clone")){$(this).attr("checked",true)
}})
},endsWith:function(b,a){return b.length>=a.length&&b.substr(b.length-a.length)==a
},checkNone:function(a){a.find("input[type='checkbox']").each(function(b){$(this).attr("checked",false)
})
},commonJsonSubmit:function(a){var b="return=json&"+ajaxUtil.formData2QueryString(a,false);
ajaxUtil.showPanel("",$("#js-processing-request").html(),null);
ajaxUtil.postJsonQuery(window.location.pathname,b,adminUtil.handleCallback,adminUtil.handleFallback)
},editPopUp:function(b){adminUtil.formReset();
adminUtil.resetClone(false);
var a;
if(b){a="return=json&"+$(b).attr("rel")+"&"+AdminConfig.actionName+"="+AdminConfig.actionRetrieveEdit
}else{a="return=json&"+AdminConfig.actionName+"="+AdminConfig.actionRetrieveEdit
}ajaxUtil.showPanel("",$("#js-processing-request").html(),null);
ajaxUtil.postJsonQuery(window.location.pathname,a,adminUtil.handleCallback,adminUtil.handleFallback)
},copyPopUp:function(b){adminUtil.formReset();
adminUtil.resetClone(false);
var a="return=json&"+$(b).attr("name")+"="+$(b).val()+"&"+AdminConfig.actionName+"="+AdminConfig.actionRetrieveCopy;
ajaxUtil.showPanel("",$("#js-processing-request").html(),null);
ajaxUtil.postJsonQuery(window.location.pathname,a,adminUtil.handleCallback,adminUtil.handleFallback)
},showEntryEvent:function(a,b){$("#searchDiv").hide();
$("#entryDiv").show();
$("#entryButtonBar").show();
$(".entryfocus").focus()
},hideEntryEvent:function(a){$("#searchDiv").show();
$("#entryDiv").hide();
$("#entryButtonBar").hide()
},deleteRecord:function(){var a="#"+adminUtil.activeModuleId+" #"+AdminConfig.listingForm;
ajaxUtil.showPanel("",$("#js-processing-request").html(),"");
var b="return=json&"+ajaxUtil.formData2QueryString(a);
ajaxUtil.postJsonQuery(window.location.pathname,b,this.handleCallback,this.handleFallback)
},refreshSearch:function(){var a=$("table.sorting").dataTable();
a.fnDraw()
},searchRecord:function(b){var a;
a=$("#"+adminUtil.activeModuleId).find("#"+AdminConfig.searchForm+"");
a.find("input[name='action1']").val(AdminConfig.actionSearch);
var e=this.sortBy[adminUtil.activeModuleId];
var c=adminUtil.sortByDirection[adminUtil.activeModuleId];
e=(e)?e:"";
c=(c)?c:"";
var d="return=json&"+ajaxUtil.formData2QueryString(a);
d=d+AdminConfig.sortName+"="+e;
d=d+"&"+AdminConfig.sortDirectionName+"="+c;
ajaxUtil.postJsonQuery(a.attr("action"),d,adminUtil.handleCallback,adminUtil.handleFallback)
},saveRecord:function(){if(typeof(customSave)=="function"){customSave()
}else{if(typeof(beforeSave)=="function"){beforeSave()
}var b;
$("textarea[class=editor]").each(function(e){b=nicEditors.findEditor($(this).attr("id"));
if(b){$(this).val(b.getContent())
}});
var c=$("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.entryForm+"");
var d=$("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.searchForm+"");
ajaxUtil.showPanel("",$("#js-processing-request").html(),null);
var a="return=json&"+ajaxUtil.formData2QueryString(c,true);
if(d.size()>0){a+=ajaxUtil.formData2QueryString(d,true)
}ajaxUtil.postJsonQuery(window.location.pathname,a,adminUtil.handleCallback,adminUtil.handleFallback)
}},handleCallback:function(json){var rc=json.rc;
var rd=json.rd;
if(json.redirect){window.location=json.redirect;
return
}if(rc=="9001"){adminUtil.showPanelSuccess(rd);
adminUtil.getModuleElement("paginationData").html("");
adminUtil.getModuleElement("viewRecordNumber").html("");
adminUtil.hideOverlay()
}else{if(rc=="9002"){adminUtil.showPanelSuccess(rd);
adminUtil.showEntryEvent()
}else{if(rc=="0"){$(unescape(json.record)).prependTo(adminUtil.getModuleElement("paginationData"));
adminUtil.hideEntryEvent();
adminUtil.showPanelSuccess(rd);
adminUtil.hideOverlay()
}else{if(rc=="1"){adminUtil.showPanelSuccess(rd);
adminUtil.hideEntryEvent();
if(json.autoreload=="Y"){adminUtil.refreshSearch()
}adminUtil.hideOverlay();
if(adminUtil.activeEditTR){adminUtil.activeEditTR.attr("class","editedrecord");
if(adminUtil.activeModuleId!="uploadModule"&&json.record){var oTable=$("table.sorting").dataTable();
var iPos=adminUtil.activeEditTR.prevAll().length;
oTable.fnUpdate(json.record,iPos,0,false)
}if(json.image!=""){adminUtil.activeEditTR.find("#productImageTD").html(json.image)
}}}else{if(rc=="2"){adminUtil.hideOverlay();
adminUtil.refreshSearch()
}else{if(rc=="3"){var moduleName=(json.md)?(json.md+"Module"):adminUtil.activeModuleId;
var name,value,control,field;
var record="";
var form=$("#"+moduleName+"Entry").find("#entryForm");
var formName="";
var formType="";
var formValue="";
if(json.R){record=json.R[0]
}if(record){for(var i=0;
i<record.length;
i++){fieldname=record[i].n;
fieldvalue=record[i].v;
control=record[i].c;
field=form.find('[name="'+fieldname+'"]');
field.each(function(x){formType=$(this).attr("type");
if(formType=="checkbox"){if(fieldvalue=="Y"){$(this).attr("checked",true)
}else{$(this).attr("checked",false)
}}else{if(formType==undefined){$(this).html(fieldvalue)
}else{if(formType=="textarea"){$(this).val(fieldvalue);
if(field.attr("class").indexOf("editor")!=-1&&nicEditors.findEditor(field.attr("id"))){nicEditors.findEditor(field.attr("id")).setContent(fieldvalue)
}}else{if(formType!="radio"){if(fieldvalue){$(this).val(fieldvalue)
}else{$(this).val("")
}if(control=="L"){if(formType=="select-one"){$(this).attr("disable",true)
}else{$(this).attr("readOnly",true)
}}if(control=="R"){}}}}}})
}}if(json.subclasslength){for(var i=0;
i<json.subclasslength;
i++){adminUtil.processEditSubClass(eval("json.SUBR"+i),true)
}}if(typeof(processEdit)=="function"){processEdit(json)
}adminUtil.activeModuleId=moduleName;
adminUtil.showEntryEvent();
if(json.isCopy==true){$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionCreate)
}else{$("#"+adminUtil.activeModuleId+"Entry").find("input[name='action1']").val(AdminConfig.actionUpdate)
}ajaxUtil.hidePanel()
}else{if(rc=="4"){var resultHTML=json.resultHTML;
var moduleName=(json.md)?(json.md+"Module"):adminUtil.activeModuleId;
if(json.pagination){$("#"+moduleName).find("#paginationDiv").html(json.pagination);
$("#"+moduleName).find("a[id^=viewPageNumber]").address()
}if(json.resultHTMLAppend){var appendObj=$(json.resultHTMLAppend);
appendObj.each(function(i){$(this).attr("class","newrecord");
$(this).find("input[name$='row-status']").val("N")
});
var paginationData=$("#"+moduleName).find("#paginationData");
if(paginationData.find("tr").size()>1){appendObj.insertBefore(paginationData.find("tr:first"))
}else{appendObj.appendTo(paginationData)
}}else{$("#"+moduleName).find("#paginationData").html(unescape(resultHTML))
}adminUtil.sortBy[moduleName]=json[AdminConfig.sortName];
adminUtil.hideOverlay()
}else{if(rc=="5"){adminUtil.hideOverlay();
adminUtil.showPanelSuccess(rd)
}else{if(rc=="9"){adminUtil.showPanelSuccess(rd);
adminUtil.showEntryEvent()
}else{if(rc=="10"){adminUtil.showPanelSuccess(rd)
}else{adminUtil.showPanelSuccess("Unknown Error, Please Contact Administrator. Error Code : "+rd)
}}}}}}}}}}if(typeof(afterJson)=="function"){afterJson(json)
}},processEditSubClass:function(s,f){var u;
var r;
var d;
var n;
var l=new Array();
var k;
var j;
var b=s.md;
var t=s.data;
var e=$("#"+b+"EntryTable").children("thead");
var h=$("#"+b+"EntryTableBody");
var v=$("[id="+b+"Clone]");
var m;
var a;
var c;
var q=["rowEven","rowOdd"];
if(v.html()){var p=v.parent().get(0).tagName;
if(p=="UL"){h=v.parent()
}if(f){h.empty()
}for(var o=0;
o<t.length;
o++){d=t[o];
for(var g=0;
g<d.length;
g++){j="";
if(v){m=v.clone(true);
m.attr("id","");
m.attr("class",q[(o%2)]);
m.appendTo(h);
for(u in d[g]){a=m.find("[name="+u+"]");
this.assignValue(a,d[g][u],d[g][u+"_value"]);
j+=(u+":"+d[g][u])+"\n"
}m.show();
m.find("textarea[class=editor]").each(function(i){$(this).attr("id",new Date().getTime());
$(this).width("500px");
$(this).height("150px");
new nicEditor({fullPanel:true}).panelInstance($(this).attr("id"))
});
adminUtil.addCloneID(m);
ajaxUtil.resetGlobalComponentEvent(m)
}}}}$("tbody[id$=EntryTableBody]").find("select[class=replaceText]").each(function(w){if($(this).val()==""){$(this).parent().html("")
}else{$(this).parent().html($(this).find("option:selected").text())
}})
},assignValue:function(f,a,b){var e,d,c;
f.each(function(g){e=$(this);
d=e.attr("type");
c=e.attr("name");
if($(this).get(0).tagName=="IMG"){d="img"
}else{if($(this).get(0).tagName=="SPAN"){d="span"
}}if(d=="checkbox"){if(a=="Y"){e.attr("checked",true)
}else{e.attr("checked",false)
}}else{if(d=="textarea"){e.val(a)
}else{if(d=="radio"){}else{if(d=="img"){var k=a.lastIndexOf("/")+1;
var j=a.substring(0,k)+"tn_"+a.substring(k);
e.attr("src",j)
}else{if(d=="span"){e.html(a)
}else{if(d=="hidden"){e.val(a);
var h=e.parents("td").find("span[name="+c+"]");
if(h){h.html((b!="")?b:a)
}}else{if(a){e.val(a)
}else{e.val("")
}}}}}}}})
},processEditDetail:function(d){var f=d.SUBR.rc;
if(f==4){var h=$("#"+adminUtil.activeModuleId+"Entry").find("input[name="+d.SUBR.parentkey+"]").val();
var a=d.SUBR.pagination;
var g=d.SUBR.resultHTML;
var b=d.SUBR.md;
var e=$("#"+b+"Module");
var c=e.find("#searchForm");
e.find("#paginationDiv").html(a);
e.find("#paginationData").html(g);
e.find("input[name="+d.SUBR.parentkey+"]").val(h);
if(c.find("input[name='subaction']").size()==0){$("<input type='hidden' name='subaction' value='attach'>").appendTo(c)
}}},handleFallback:function(a){strResponse=a.responseText;
switch(a.status){case 404:alert("Error: Not Found. The requested URL could not be found.");
break;
case 500:break;
default:if(strResponse.indexOf("Error:")>-1||strResponse.indexOf("Debug:")>-1){alert(strResponse)
}else{alert(strResponse)
}break
}},formReset:function(){var a=$("#"+adminUtil.activeModuleId+"Entry #"+AdminConfig.entryForm+"");
a.find("input, select, textarea").each(function(b){formName=$(this).attr("name");
formType=$(this).attr("type");
formValue=$(this).val();
switch(formType){case"text":case"password":case"textarea":$(this).val("");
break;
case"checkbox":$(this).attr("checked",false);
break
}});
$("textarea[class=editor]").each(function(b){myTextarea=nicEditors.findEditor($(this).attr("id"));
if(myTextarea){myTextarea.setContent("")
}});
$("#"+adminUtil.activeModuleId+"Entry").find("#paginationData").html("");
$("#"+adminUtil.activeModuleId+"Entry").find("#paginationDiv").html("")
},resetClone:function(b){var a;
var c="";
$("[class$=detail-listingTable]").each(function(e){var h=$(this).children("thead");
var f=$(this).children("tbody");
f.empty();
a=h.children("[id$=Clone]");
if(b){newClone=a.clone(true);
newClone.attr("id","");
newClone.find("input[type=checkbox]").attr("checked",false);
newClone.find("input[name$=row-status]").val("N");
var d=newClone.find("[id$=Clone]");
var g=newClone.find("tbody[id$=EntryTableBody]");
if(d.size()>0){d.appendTo(g);
d.show()
}adminUtil.addCloneID(newClone);
newClone.attr("style","");
newClone.appendTo(f);
newClone.show();
newClone.find("textarea[class=editor]").each(function(i){$(this).attr("id",new Date().getTime());
$(this).width("500px");
$(this).height("150px");
new nicEditor({fullPanel:true}).panelInstance($(this).attr("id"))
});
ajaxUtil.resetGlobalComponentEvent(newClone)
}})
}};
function Admin(){}var adminUtil;
$(window).ready(function(){adminUtil=new Admin();
adminUtil.initialize()
});