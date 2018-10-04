package com.forest.common.builder;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.DataObject;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.beandef.AdminUserBeanDef;
import com.forest.common.beandef.BaseDef;
import com.forest.common.beandef.BeanDefUtil;
import com.forest.common.constants.GeneralConst;
import com.forest.common.services.GenericServices;
import com.forest.common.util.BuilderUtil;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.OwnException;
import com.forest.common.util.ResourceUtil;
import com.forest.common.util.SelectBuilder;
import com.forest.mfg.adminbuilder.MFG_SelectBuilder;
import com.forest.mfg.beandef.MFG_CustProductDef;
import com.forest.mfg.beandef.MFG_Grouping;
import com.forest.mfg.beandef.MFG_ProductOptionDef;
import com.forest.mfg.beandef.MFG_RawDef;
import com.forest.mfg.beandef.MFG_SupplierProductDef;
import com.forest.share.adminbuilder.BuildAdminHTML;

public class GenericAdminBuilder extends AdminBuilder {
	private Logger logger = Logger.getLogger (GenericAdminBuilder.class);
	protected HashMap[] _attrReqDataMap = null;
	protected HashMap[][] _attrReqDataDetailMap = null;
	
	protected GenericServices _gs = null;
	protected Class _defClass = null;
	protected String[] _deleteArrCode = null;
	protected String _reqSortFieldName 	= null;
	protected String _reqSortDirection 	= null;		
	protected String _reqRecordStartAt = null;
	protected String _reqTotalRecordPerPage = null;
	
	protected String _reqAction = null;
	protected HttpServletRequest _req = null;
	protected HttpServletResponse _resp = null;
	
	protected String _accessByCompany = null; // COMPANY_NONE, COMPANY_SELECTED, COMPANY_MASTER
	protected String _accessByUserRight = null; // USER_RIGHT_NO, USER_RIGHT_YES
	
	protected ArrayList arrayListSearch = null;
	protected ArrayList searchArray = null;
	protected ArrayList addArray = null;
	protected ArrayList listingArray = null;
	protected Class[] subClass = null;	
	protected StringBuffer _searchCriteria = new StringBuffer();
	
	public GenericAdminBuilder(ShopInfoBean mShopInfoBean, ClientBean mClientBean, Connection conn, 
			HttpServletRequest req, HttpServletResponse resp, Class defClass, String accessByShop, String resources) throws Exception{
		super(mShopInfoBean, mClientBean, conn, resources);	
				
		_gs = new GenericServices(_dbConnection, _shopInfoBean, _clientBean);
		_defClass = defClass;
		_req = req;
		_resp = resp;
		_reqTotalRecordPerPage = req.getParameter (TOTAL_RECORD_PER_PAGE);
		_reqRecordStartAt = req.getParameter (RECORD_START_AT);
		_reqSortDirection = req.getParameter (SORT_DIRECTION);
		_reqSortFieldName = req.getParameter (SORT_NAME);
		
		_reqAction = req.getParameter(ACTION_NAME);
		if(CommonUtil.isEmpty(accessByShop) || ModuleConfig.COMPANY_NONE.equals(accessByShop)){
			_accessByCompany = null;
		}else if(ModuleConfig.COMPANY_SELECTED.equals(accessByShop)){
			_accessByCompany = _shopInfoBean.getShopName();
		}else if(ModuleConfig.COMPANY_MASTER.equals(accessByShop)){
			if(CommonUtil.isEmpty(_shopInfoBean.getCompanyGroup())){
				_accessByCompany = _shopInfoBean.getShopName();
			}else{
				_accessByCompany = _shopInfoBean.getCompanyGroup();
			}
		}
		
		HashMap moduleMap = (HashMap) ModuleConfig.filenameToClass.get(_clientBean.getRequestFilename());
		if(moduleMap!=null){			
			_accessByUserRight = (String)moduleMap.get(ModuleConfig.BY_USER_RIGHT);
			logger.debug("_accessByUserRight = "+_accessByUserRight);
		}
		_reqTotalRecordPerPage=(_reqTotalRecordPerPage==null)?"10":_reqTotalRecordPerPage;
		
	} 	
	
	public StringBuffer requestHandler()throws Exception{	
		return null;
	}
	
	public JSONObject requestJsonHandler()throws Exception{		
		
		JSONArray searchResult = null;
		
		JSONObject json = new JSONObject();
		subClass = BeanDefUtil.getSubClass(_defClass);

		try{
			if(GeneralConst.CREATE.equals (_reqAction) || GeneralConst.UPDATE.equals (_reqAction)){
				addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
				
				_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, addArray, _accessByCompany);			
				_attrReqDataMap[0].put(BaseDef.UPDATEBY, _clientBean.getLoginUserId());
				StringBuffer validationBuffer = validateBean (addArray, _attrReqDataMap, (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME), _clientBean.getLocale (), _reqAction);
				logger.debug("validationBuffer = "+validationBuffer);
				if(validationBuffer.length ()>0){
					json.put ("rc", RETURN_VALIDATION);						
					json.put ("rd", validationBuffer);									
					return json;
				}
			}
			// Functional 
			if(GeneralConst.CREATE.equals (_reqAction)){		
				json = requestCreate();
			}else if(GeneralConst.UPDATE.equals (_reqAction)){				
				json = requestUpdate();
			}else if(GeneralConst.DELETE.equals (_reqAction)){				
				json = requestDelete();
			}else if(GeneralConst.EDIT.equals (_reqAction)){		
				json = requestEdit(null, null);
			}else if(GeneralConst.COPY.equals (_reqAction)){		
				json = requestEdit(null, null, true);
			}else if(GeneralConst.SEARCH.equals (_reqAction) || GeneralConst.RETRIEVE.equals (_reqAction)){
				searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
				listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);

				if(CommonUtil.isEmpty(_reqSortFieldName)){
					_reqSortFieldName = (String) BeanDefUtil.getField(_defClass, BeanDefUtil.DEFAULT_SORT) + " " +(String) BeanDefUtil.getField(_defClass, BeanDefUtil.DEFAULT_SORT_DIRECTION);
				}else{
					String prefix = BeanDefUtil.getDataObject(_defClass, _reqSortFieldName).prefix;
					_reqSortFieldName = (CommonUtil.isEmpty(prefix)?"":prefix+".")+_reqSortFieldName + " " + _reqSortDirection; 
				}				
								
				if(GeneralConst.RETRIEVE.equals (_reqAction)){					
					DataObject keyField = BeanDefUtil.getKeyObject(_defClass);
					if(!CommonUtil.isEmpty(keyField.prefix)){
						_searchCriteria.append (keyField.prefix).append (".");
					}
					_searchCriteria.append (keyField.name).append (" In (");
					_searchCriteria.append (DBUtil.arrayToString(_req.getParameter("id").split(","), BeanDefUtil.getKeyObject(_defClass))).append (")");
					
					_reqTotalRecordPerPage = "*";
					
					_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
				}else{
					_attrReqDataMap = new HashMap[1];	
					_attrReqDataMap[0] = new HashMap();
					_attrReqDataMap[0].put(BaseDef.COMPANYID, _accessByCompany);					
					_searchCriteria = BuilderUtil.requestSearchValueToDataObject(_req, searchArray, _accessByCompany);
										
					if(ModuleConfig.USER_RIGHT_YES.equals(_accessByUserRight)){
						if(_searchCriteria.length()>0){
							_searchCriteria.append(" And (");
							_searchCriteria.append(" a.updateby = ").append(_clientBean.getLoginUserId());														
							_searchCriteria.append(" Or (");
							_searchCriteria.append(" u.").append(AdminUserBeanDef.usergroup.name).append(">=").append(_clientBean.getUserGroup());
							_searchCriteria.append(" Or a.updateby is null or a.updateby = ''");
							_searchCriteria.append("))");
						}						
					}
					logger.debug("Adam _searchCriteria:" +_searchCriteria);
				}
				
				if(!CommonUtil.isEmpty(_reqRecordStartAt)){
					_reqTotalRecordPerPage = _reqRecordStartAt + ", " + _reqTotalRecordPerPage;
				}
				arrayListSearch = _gs.search(_defClass, _searchCriteria, 
						searchArray, listingArray, _attrReqDataMap,	_reqSortFieldName, _reqTotalRecordPerPage, _reqAction);

				
				if(arrayListSearch.size ()>1){				
					json.put ("md", (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME));
					json.put ("rc", RETURN_SEARCH);			
					
					if(GeneralConst.SEARCH.equals (_reqAction)){								
//						json.put ("pagination", getClientPagination((ArrayList) arrayListSearch.get(0), 
//								Integer.parseInt(_reqTotalRecordPerPage), null, "1", "viewPageNoAddress"));
					}
						
					json = buildSearchResult1(_defClass,
							(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
							listingArray, arrayListSearch, true, false, null);
					json.put("sEcho", _req.getParameter("sEcho"));
				}else{
					json.put ("rc", "9001"); // No record Founnd
					json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", "9001", _clientBean.getLocale ()));
				}										
			}
		}catch(OwnException e){			
			throw e;
		}catch(Exception e){
			logger.error(e, e);
			throw new OwnException(e);
		}		
		return json;
	}
	
	protected JSONObject requestEdit(StringBuffer searchCriteria, StringBuffer detailSearchCriteria)throws Exception{
		return requestEdit(searchCriteria, detailSearchCriteria, false);
	}
	
	protected JSONObject requestEdit(StringBuffer searchCriteria, StringBuffer detailSearchCriteria, boolean isCopy)throws Exception{		
		subClass = BeanDefUtil.getSubClass(_defClass);
		
		JSONObject json = new JSONObject();
		addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
		searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
		_attrReqDataMap = BuilderUtil.requestValueToDataObject(_req, searchArray, _accessByCompany);
		
		arrayListSearch = _gs.search(_defClass, searchCriteria, 
				searchArray, 
				addArray, _attrReqDataMap, null, "1", _reqAction);				

		json.put ("md", (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME));
		json.put ("rc", RETURN_EDIT);
		json.put ("isCopy", isCopy);
		json.put ("R", buildEdit((ArrayList) arrayListSearch.get(1), _defClass, null, isCopy));
		
		if(subClass!=null){				
			json.put("subclasslength", subClass.length);
			logger.debug("detailSearchCriteria = "+detailSearchCriteria);
			logger.debug("----------------- Detail _attrReqDataMap = "+_attrReqDataMap[0]);
			for(int i=0; i<subClass.length; i++){				
				addArray = BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
				searchArray = BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);				
				arrayListSearch = _gs.search(subClass[i], detailSearchCriteria, 
						searchArray, addArray, _attrReqDataMap, null, "*", _reqAction);
				
				json.put ("SUBR"+i, buildEditDetail((ArrayList) arrayListSearch.get(1), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.KEY), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.TABLE_PREFIX), null, isCopy));
			}
		}
		return json;
	}	
	
	protected JSONObject requestCreate()throws Exception{
		JSONObject json = new JSONObject();
		int key = 0;
		key = _gs.create(_defClass, addArray, _attrReqDataMap[0]);
		logger.debug("key = "+key);
		if(key>0){
			_attrReqDataMap[0].put((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key));	
		}else{
			key = Integer.parseInt((String)_attrReqDataMap[0].get((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY)));
		}
						
		json.put ("rc", RETURN_CREATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_CREATE, _clientBean.getLocale ()));
		json.put ("record", buildAddRecordReturn (_defClass,
				(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		if(subClass!=null){			
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), String.valueOf(key), _shopInfoBean.getShopName());
				
				if(_attrReqDataDetailMap[i]!=null){							
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));
				}
			}
		}
//		if(_defClass==MFG_CustProductDef.class || _defClass==MFG_SupplierProductDef.class || _defClass==MFG_ProductOptionDef.class 
//				|| _defClass==MFG_RawDef.class || _defClass==AdminUserBeanDef.class || _defClass==MFG_Grouping.class){	
				
			MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
			new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
//		}
		
		
		return json;
	}
	
	protected JSONObject requestUpdate()throws Exception{
		JSONObject json = new JSONObject();
		_gs.update(_defClass, addArray,	_attrReqDataMap[0]);
		json.put ("rc", RETURN_UPDATE);	
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RETURN_UPDATE, _clientBean.getLocale ()));
		json.put ("record", buildAddEditRecord (_defClass,(String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY), 
				listingArray, _attrReqDataMap[0]));
		
		
		if(subClass!=null){								
			_attrReqDataDetailMap = new HashMap[subClass.length][];
			for(int i=0; i<subClass.length; i++){						
				// Create Detail
				_attrReqDataDetailMap[i] = BuilderUtil.requestValuesToDataObject(subClass[i], _req, 
						BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean), 
						(String) BeanDefUtil.getField(subClass[i], BeanDefUtil.PARENT_KEY), null, _shopInfoBean.getShopName());	
				
				if(_attrReqDataDetailMap[i]!=null)						
					_gs.updateDetail(subClass[i], _attrReqDataDetailMap[i], BeanDefUtil.getKeyObject(subClass[i]), BeanDefUtil.getArrayList(subClass[i], _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean));							
			}
		}							
		
		MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
		return json;
	}
	
	protected JSONObject requestDelete()throws Exception{
		JSONObject json = new JSONObject();
		_deleteArrCode = _req.getParameterValues ((String) BeanDefUtil.getField(_defClass, BeanDefUtil.KEY));				
		_gs.delete (_defClass, BeanDefUtil.getKeyObject(_defClass), _deleteArrCode);
		
		json.put ("rc", RETURN_DELETE);					
		json.put ("rd", ResourceUtil.getAdminResourceValue(null,"", RECORD_DELETED, _clientBean.getLocale ()));				
		
		if(subClass!=null){
			for(int i=0; i<subClass.length; i++){	
				_gs.delete (subClass[i], BeanDefUtil.getParentKeyObject(subClass[i]), _deleteArrCode);
			}
		}
				
		MFG_SelectBuilder.removeCache(_shopInfoBean.getShopName(), _defClass);
		new BuildAdminHTML(_shopInfoBean, _clientBean, _dbConnection, _req, _resp).buildHTML(_defClass);
		return json;
	}
	public JSONArray requestJsonArrayHandler()throws Exception{	
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonRow = new JSONObject();		
		
		if(GeneralConst.DYNAMIC_SELECT.equals (_reqAction) || GeneralConst.AUTO_SUGGEST.equals (_reqAction)){
			StringBuffer searchCriteria = new StringBuffer();
			
			searchArray = BeanDefUtil.getArrayList(_defClass, BeanDefUtil.AS_SEARCH_LIST);
			listingArray = BeanDefUtil.getArrayList(_defClass, BeanDefUtil.AS_SEARCH_DISPLAY_LIST);
			
//			_attrReqDataMap = BuilderUtil.requestAutoSuggestValueToDataObject(_req, searchArray, _accessByCompany);
			_attrReqDataMap = new HashMap[1];	
			_attrReqDataMap[0] = new HashMap();
			_attrReqDataMap[0].put(BaseDef.COMPANYID, _accessByCompany);
			
			_searchCriteria = BuilderUtil.requestSearchValueToDataObject(_req, searchArray, _accessByCompany);
			
			if(ModuleConfig.USER_RIGHT_YES.equals(_accessByUserRight)){
				if(_searchCriteria.length()>0){
					_searchCriteria.append(" And (");
					_searchCriteria.append(" a.updateby = ").append(_clientBean.getLoginUserId());														
					_searchCriteria.append(" Or (");
					_searchCriteria.append(" u.").append(AdminUserBeanDef.usergroup.name).append(">=").append(_clientBean.getUserGroup());
					_searchCriteria.append(" Or a.updateby is null or a.updateby = ''");
					_searchCriteria.append("))");
				}						
			}
			
			ArrayList arrayListAutoSuggest = (ArrayList) _gs.search(_defClass, _searchCriteria, 
					searchArray, listingArray, _attrReqDataMap,
					_reqSortFieldName, _reqTotalRecordPerPage, _reqAction, "getAutoSuggestSearchScript").get(1);
			
//			logger.debug("arrayListAutoSuggest = "+arrayListAutoSuggest);
			HashMap dataRow = null;
			HashMap fieldRow = null;
			String asType = null;
			DataObject obj = null;
			String[] fields = new String[listingArray.size()];

			for(int i=0; i<arrayListAutoSuggest.size(); i++){
				jsonRow = new JSONObject();
				dataRow = (HashMap)arrayListAutoSuggest.get(i);				
				for(int x=0; x<fields.length; x++){
					fieldRow = (HashMap)listingArray.get(x);					
					obj = (DataObject)fieldRow.get("object");
					asType = (String) fieldRow.get("astype");		
					if(CommonUtil.isEmpty(asType)){
						asType = obj.name;
					}
					jsonRow.put (asType, (String) dataRow.get(obj.name));
				}
				
				logger.debug("jsonRow = "+jsonRow);
				jsonArray.put(jsonRow);
			}				
		}
		
		return jsonArray;
	}
	
	public void displayHandler(StringBuffer buffer)throws Exception{
		
	}
	
	public StringBuffer displayHandler()throws Exception{		
		ArrayList addArray = null;
		ArrayList listingArray = null;
		int columns = 1;		
		int tabsCount = 0;
		String[] tabs = null; 
		Object columnObj = null;
		Object tabsObj = null;
		StringBuffer buffer = new StringBuffer();
		StringBuffer subClsBuffer = new StringBuffer();
		
		if(_defClass!=null){
			String MODULE_NAME = (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME);
			Class[] subClass = BeanDefUtil.getSubClass(_defClass);
			
			searchArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_SEARCH, _shopInfoBean, _clientBean);
			listingArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_LISTING, _shopInfoBean, _clientBean);
			addArray = BeanDefUtil.getArrayList(_defClass, _dbConnection, BeanDefUtil.DISPLAY_TYPE_ADD, _shopInfoBean, _clientBean);
			columnObj = BeanDefUtil.getField(_defClass, "columns");
			tabsObj = BeanDefUtil.getField(_defClass, "tabs");	
			if(columnObj!=null){
				columns = Integer.parseInt(columnObj.toString());
			}
			if(tabsObj!=null){
				tabs = (String[])tabsObj;
				tabsCount = tabs.length;
			}
			
			buffer.append("<p:useTemplate>simplplan-template.htm</p:useTemplate>").append("\n");
			
			buffer.append("<p:component value=\"titlebar\">");
			buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
			buffer.append(_shopInfoBean.getShopNameDesc());
			buffer.append("</p:component>").append("\n");
			
			buffer.append("<p:component value=\"title_right\">");
			buffer.append("<ul id=\"entryButtonBar\" class=\"sorting\" style=\"display: none;\">");
			buffer.append("<li><a href=\"#\" id=\"btnCancel\">Cancel</a></li>");
			buffer.append("<li><a href=\"#\" id=\"btnSave\">Save</a></li>");
			buffer.append("<li><a href=\"#\" id=\"btnPrint\">Print</a></li>");
			buffer.append("</ul>");
			buffer.append("</p:component>").append("\n");
			
			buffer.append("<p:component value=\"body\">").append("\n");	
	
			buffer.append("<div id=\"").append(MODULE_NAME).append("Module\" class='box_content'>").append("\n");	
				buffer.append("<div id='searchDiv'>");
					buffer.append ("<form id='").append (GeneralConst.LISTING_FORM).append ("' method='POST'>");
						buffer.append(buildListing(listingArray, MODULE_NAME, true)).append("\n");	
					buffer.append ("</form>");			
				buffer.append("</div>");
				// Add Form
				if(subClass!=null){
					for(int i=0; i<subClass.length; i++){
						subClsBuffer.append("<div style='width: auto; overflow:auto;'>");
						subClsBuffer.append("<fieldset>"); 
						subClsBuffer.append("<legend><b>").append(ResourceUtil.getAdminResourceValue(_resources, (String) BeanDefUtil.getField(subClass[i], BeanDefUtil.MODULE_NAME), "title", _clientBean.getLocale())).append("</b></legend>");						
						subClsBuffer.append (buildDetailEntryForm (subClass[i]));						
						subClsBuffer.append("</fieldset>");
						subClsBuffer.append("</div><br>");
					}
				}
				
				buffer.append("<div id='entryDiv' style='display: none;overflow:scroll;'>");
					buffer.append("<div id=\"").append(MODULE_NAME).append("ModuleEntry\"");
					buffer.append(" title='%i18n.system.").append(MODULE_NAME).append(".title% - ");
					buffer.append(_shopInfoBean.getShopNameDesc()).append("'");
					buffer.append(" class=\"entryScreen\">").append("\n");
					buffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST' autocomplete='off'>");
						if(tabsCount>0){
							buffer.append("<div class='tabs'><ul>");
							for(int i=0; i< tabsCount; i++){
								buffer.append("<li><a href='#tabs-").append(i+1).append("'>").append(tabs[i]).append("</a></li>");	
							}
							buffer.append("</ul>");
							
							for(int i=0; i< tabsCount; i++){								
								buffer.append("<div id='tabs-").append(i+1).append("'>\n");
									buffer.append(buildEntryForm(MODULE_NAME, addArray, columns, (i+1))).append("\n<br>");
									if(i==0 && subClass!=null){
										buffer.append(subClsBuffer);
									}
								buffer.append("</div>\n\n");
							}
							
							buffer.append("</div>");
						}else{
							buffer.append(buildEntryForm(MODULE_NAME, addArray, columns)).append("\n<br>");
							buffer.append(subClsBuffer);
						}
						
						
					buffer.append ("</form>");	   
					buffer.append("</div>").append("\n");
				buffer.append("</div>");
			buffer.append("</div>").append("\n");
			buffer.append("</p:component>").append("\n");			
		}
		
		return buffer;
	}	
	
	public StringBuffer displayHandlerEntry(ArrayList addArray)throws Exception{						
		int columns = 1;
		Object columnObj = null;
		StringBuffer buffer = new StringBuffer();
		
		if(_defClass!=null){
			String MODULE_NAME = (String) BeanDefUtil.getField(_defClass, BeanDefUtil.MODULE_NAME);
			Class[] subClass = BeanDefUtil.getSubClass(_defClass);							
			columnObj = BeanDefUtil.getField(_defClass, "columns");	
			if(columnObj!=null){
				columns = Integer.parseInt(columnObj.toString());
			}
			buffer.append("<p:useTemplate>simplplan-template.htm</p:useTemplate>").append("\n");
			buffer.append("<p:component value=\"titlebar\">");
			buffer.append("%i18n.system.").append(MODULE_NAME).append(".title% - ");
			buffer.append(_shopInfoBean.getShopNameDesc());
			buffer.append("</p:component>").append("\n");
			buffer.append("<p:component value=\"body\">").append("\n");	
	
			buffer.append("<div id=\"").append(MODULE_NAME).append("Module\">").append("\n");		
				// Add Form
				buffer.append("<div id=\"").append(MODULE_NAME).append("ModuleEntry\"");
				buffer.append(" title='%i18n.system.").append(MODULE_NAME).append(".title% - ");
				buffer.append(_shopInfoBean.getShopNameDesc()).append("'");
				buffer.append(" class=\"entryScreenSingle\" style=\"width: 100%;\">").append("\n");
				buffer.append ("<form id='").append (GeneralConst.ENTRY_FORM).append ("' method='POST' autocomplete='off'>");	
					buffer.append(buildEntryForm(MODULE_NAME, addArray, columns)).append("\n<br>");	
					if(subClass!=null){
						for(int i=0; i<subClass.length; i++){
							MODULE_NAME = (String) BeanDefUtil.getField(subClass[i], BeanDefUtil.MODULE_NAME);		
							
							buffer.append("<fieldset>"); 
							buffer.append("<legend><b>").append(ResourceUtil.getAdminResourceValue(_resources, MODULE_NAME, "title", _clientBean.getLocale())).append("</b></legend>");
							buffer.append (buildDetailEntryForm (subClass[i])).append("<br>");
							buffer.append("</fieldset><br>");
						}
					}
					buffer.append ("</form>");	   
				buffer.append("</div>").append("\n");				
			buffer.append("</div>").append("\n");
			buffer.append("</p:component>").append("\n");			
		}
		
		return buffer;
	}
	
	public JasperPrint buildReport()throws Exception{
		return null;
	}
	
	public StringBuffer buildWebReport()throws Exception{
		return null;
	}
	
	public String getHashStringValue(HashMap data, String key, String fieldname){		
		String rtn = null;
		try{
			if(key==null || data==null){
				rtn =  "";
			}else{
				if(CommonUtil.isEmpty(fieldname)) fieldname = key;
				rtn =  (String)((HashMap)data.get(key)).get(fieldname);
			}
		}catch(Exception e){
			logger.debug("key = "+key+", data = "+data);
			logger.error(e, e);
		}
		return rtn;
	}
}
