package com.forest.common.servlet;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperExportManager;

import org.apache.log4j.Logger;

import com.forest.common.bean.ClientBean;
import com.forest.common.bean.ShopInfoBean;
import com.forest.common.builder.GenericAdminBuilder;
import com.forest.common.builder.ModuleConfig;
import com.forest.common.builder.ModuleConfigID;
import com.forest.common.builder.ShopAdminBuilder;
import com.forest.common.builder.AdminBuilder;
import com.forest.common.constants.BusinessConst;
import com.forest.common.constants.GeneralConst;
import com.forest.common.servlet.BaseServlet;
import com.forest.common.util.CommonUtil;
import com.forest.common.util.FileHandler;
import com.forest.common.util.OwnException;
import com.forest.common.util.TemplateProcess;
 
public class AdminHandler extends BaseServlet
{	
	private Logger logger = Logger.getLogger (this.getClass ());
	
	static final long serialVersionUID = 1L;			
	private Map hashPageMap = null; 
	
	public void doGet (HttpServletRequest req, HttpServletResponse resp, ShopInfoBean shopInfoBean, ClientBean clientBean)
			throws ServletException, IOException, Exception
	{
		TemplateProcess tp = new TemplateProcess();
		AdminBuilder wb = new AdminBuilder(shopInfoBean, clientBean, getDbConnection(), null); 
		try{			
//			_dbConnection = getDbConnection();
			_shopInfoBean 	= shopInfoBean;
			_clientBean 	= clientBean;
			_req = req;
			_resp = resp;
			
			_returnType 		= _req.getParameter ("return");
			_action 			= _req.getParameter (GeneralConst.ACTION_NAME);
			_requestFilename 	= _clientBean.getRequestFilename ();
			if(BusinessConst.INTERIOR_DESIGN.equals(_shopInfoBean.getBusiness())){
				hashPageMap = (HashMap) ModuleConfigID.filenameToClass.get(_requestFilename);
			}else if(BusinessConst.MANUFACTURING.equals(_shopInfoBean.getBusiness())){
				hashPageMap = (HashMap) ModuleConfig.filenameToClass.get(_requestFilename);	
			}else if(BusinessConst.RETAIL.equals(_shopInfoBean.getBusiness())){
				hashPageMap = (HashMap) ModuleConfig.filenameToClass.get(_requestFilename);	
			}
						
			if(checkLogin()){
//				verifyCompany();
				
				GenericAdminBuilder genAdminBuilder = null;								
				if(hashPageMap!=null){
		    		genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder((Class) hashPageMap.get(ModuleConfig.HANDLE_BUILDER), hashPageMap, _req, _resp);	    			    		
				}
				
				if(_requestFilename.equals("upload.htm")){
					_returnType = "json";
					_parseHtml = new StringBuffer(genAdminBuilder.requestHandler().toString());
				}else{
					if("json".equals (_returnType)){
				    	if(genAdminBuilder!=null){
				    		if(GeneralConst.AUTO_SUGGEST.equals (req.getParameter(GeneralConst.ACTION_NAME))){
				    			_parseHtml = new StringBuffer(genAdminBuilder.requestJsonArrayHandler().toString());
				    		}else{
				    			_parseHtml = new StringBuffer(genAdminBuilder.requestJsonHandler().toString());
				    		}
				    	}
					}else if("rpt".equals (_returnType)){				
						_resp.setContentType("application/pdf");
						JasperExportManager.exportReportToPdfStream(genAdminBuilder.buildReport (), _resp.getOutputStream());					
						return;					
					}else{				
						if("webrpt".equals (_returnType)){									
							_parseHtml = genAdminBuilder.buildWebReport();
						}else{
							
						    if(!CommonUtil.isEmpty(_action)){
						    	_parseHtml = genAdminBuilder.requestHandler ();		    				    			
						    }else{
						    	if(hashPageMap!=null && hashPageMap.get(ModuleConfig.HANDLE_BUILDER) != null){		    			
					    			_parseHtml = genAdminBuilder.displayHandler();			    			
					    		}else{		    		    
							    	_parseHtml = FileHandler.readAdminFile (_shopInfoBean, _clientBean);
					    		}
						    }
						}
		    			// Merge template with page
		    			_parseHtml = tp.initTemplateProcess (_parseHtml, _shopInfoBean.getTemplatePath ());
		    			
		    			Iterator it = ModuleConfig.pagePrefixWebBuilder.keySet().iterator();
		    			String key = null;
		    			while(it.hasNext()){
		    				key = (String) it.next();	    				
		    				
		    				if(_parseHtml.indexOf (key)!=-1){    					
			    				genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder((Class) ModuleConfig.pagePrefixWebBuilder.get(key), null, req, resp);	
			    				genAdminBuilder.displayHandler(_parseHtml);
		    				}
		    			}
	
					    if(_parseHtml.indexOf ("system.admin.company.bar")!=-1){
					    	genAdminBuilder = (GenericAdminBuilder)wb.getWebBuilder(ShopAdminBuilder.class, null, req, resp);
					    	genAdminBuilder.displayHandler (_parseHtml);
					    }
	
//					    replacePath();
					    insertCommonProperties();
				    }	    
				}
			    if(!GeneralConst.EDIT.equals (_action)){
			    	if(hashPageMap!=null){
			    		replaceI18n((String) hashPageMap.get(ModuleConfig.RESOURCES));
			    	}else{
			    		replaceI18n(null);
			    	}
			    	
			    }
			}			
			
			commit();
			logger.debug("------------- Commit ---------------");
		}catch(Exception e){				
			printException(e);
		}finally{			
			printResult();
		}
	}			
}
