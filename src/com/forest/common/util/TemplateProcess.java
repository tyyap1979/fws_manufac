package com.forest.common.util;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.forest.common.bean.ClientBean;

/*
 * 1. initModuleContent to identify module content and initLoopElementContent to identify loopCon
 * 2. 
 */
public class TemplateProcess{
	private Logger logger = Logger.getLogger (TemplateProcess.class);
	
	public StringBuffer initTemplateProcess(StringBuffer pageHTML, String templatePath){		
		int start = 0;
		int end = 0;
		
		int templateStart = 0;
		int templateEnd = 0;
		
		String componentContent = null;
		String componentTag = null;		
		StringBuffer useTemplateHTML = null;
		try{
//			logger.debug("pageHTML = "+pageHTML);
			start = pageHTML.indexOf ("<p:useTemplate>")+"<p:useTemplate>".length ();
			end = pageHTML.indexOf ("</p:useTemplate>", start);
			
			if(end!=-1){
				String useTemplate = pageHTML.substring (start, end);			
				logger.debug("templatePath + useTemplate = "+templatePath + useTemplate);
				useTemplateHTML = new StringBuffer(FileHandler.readFile (null, templatePath + useTemplate).toString());
							
				while((templateStart = useTemplateHTML.indexOf ("<p:component "))!=-1){	
					templateEnd = useTemplateHTML.indexOf ("</p:component>", templateStart) + "</p:component>".length ();				
					componentTag = useTemplateHTML.substring (templateStart, useTemplateHTML.indexOf (">", templateStart) + 1);					
					componentContent = "";
					// Get Actual Content
					if((start = pageHTML.indexOf (componentTag))!=-1){
						end = pageHTML.indexOf ("</p:component>", start);
						componentContent = pageHTML.substring (start + componentTag.length (), end);
					}				
					useTemplateHTML.replace (templateStart, templateEnd, componentContent);								
				}
			}else{
				useTemplateHTML = new StringBuffer(pageHTML.toString());
			}
		}catch(Exception e){
			logger.error (e, e);
			logger.debug("pageHTML = "+pageHTML);
		}
		
		return useTemplateHTML;
	}	
	
	public Document initTemplateProcessDoc(ClientBean clientBean, StringBuffer pageHTML, String templatePath){		
		StringBuffer useTemplateHTML = null;
		Document docTemplate = null;		
		String moduleId = null;		
		String moduleContent = null;
		Element ele = null;
		Elements media = null;
		int count = 0;
		Document doc = null;
		String meta = null;
		try{			
			doc = Jsoup.parse(pageHTML.toString());
//			logger.debug("doc = "+doc.html());
			String useTemplate = doc.select("p|usetemplate").text();			
			if(!CommonUtil.isEmpty(clientBean.getLanguage())){
				useTemplate = clientBean.getLanguage() + "/" + useTemplate;
			}
			
			if(!CommonUtil.isEmpty(useTemplate)){										
				useTemplateHTML = new StringBuffer(FileHandler.readFile (null, templatePath + useTemplate).toString());

				docTemplate = Jsoup.parse(useTemplateHTML.toString());
//				logger.debug("docTemplate = "+docTemplate.html());
				meta = doc.select("p|component[value=meta]").html();	
//				logger.debug("meta = "+meta);
				docTemplate.select("head").prepend(meta);
				
				media = docTemplate.select("p|component");
				count = media.size();				
				for (int i=0; i<count;i++) {
		        	ele = media.get(i);
		        	moduleId = ele.attr("value");		        	
		        	moduleContent = doc.select("p|component").select("[value="+moduleId+"]").html();			        	    
		        	ele.before(moduleContent);
		        	ele.remove();
				}
				
				// Put in all include page
				media = docTemplate.select("p|includepage");
				count = media.size();				
				for (int i=0; i<count;i++) {
		        	ele = media.get(i);
		        	moduleId = ele.attr("id");		        	
		        	if(!CommonUtil.isEmpty(clientBean.getLanguage())){
		        		moduleId = clientBean.getLanguage() + "/" + moduleId;
					}
		        	moduleContent = FileHandler.readFile (null, templatePath + moduleId).toString();		        			        	
		        	ele.before(moduleContent);
		        	ele.remove();
				}				
			}else{
				docTemplate = doc;
			}
		}catch(Exception e){
			logger.error (e, e);			
		}				
		
		
		return docTemplate;
	}	
}
