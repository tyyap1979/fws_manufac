package com.forest.common.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/*
 * 1. initModuleContent to identify module content and initLoopElementContent to identify loopCon
 * 2. 
 */
public class ModuleContent
{
//	private int loopCount = 0;
	private String[] loopHtml = null;
	Element moduleEle = null;
	Element[] loopEleArray = null;
	
//	private Logger logger = Logger.getLogger (ModuleContent.class);
	
	public void initModuleContent(Element moduleEle){	
		initModuleContent(moduleEle, null);
	}
	public void initModuleContent(Element moduleEle, String loopSelector){		
//		loopCount = 0;				
		loopHtml = null;
		this.moduleEle = moduleEle; 
		this.moduleEle.attr("pid", this.moduleEle.attr("id"));
		this.moduleEle.removeAttr("id");
		if(CommonUtil.isEmpty(loopSelector)){
			initLoopElementContent("loop");
		}else{
			initLoopElementContent(loopSelector);
		}
	}
	
	private void initLoopElementContent(String loopSelector){
//		if(CommonUtil.isEmpty(loopId)){
//			this.loopEle = this.moduleEle.select("[custom=loop]");
//		}else{
//			this.loopEle = this.moduleEle.select("[custom=loop-"+loopId+"]");
//		}
		Elements loopEle = null;
		Element ele = null;
		int loopEleSize = 0;		
		loopEle = this.moduleEle.select(loopSelector);
		
		loopEleSize = loopEle.size();
		System.out.println("["+loopSelector+"] loopEleSize: "+loopEleSize);
		if(loopEleSize>0){
			this.loopHtml = new String[loopEleSize];		
			this.loopEleArray = new Element[loopEleSize];		
			for(int i=0; i<loopEleSize; i++){
				ele = loopEle.get(i);
				this.loopHtml[i] = ele.html(); 	
				if(ele.attr("removeTag").equals("false")){
					this.loopEleArray[i] = ele;					
				}else{
					this.loopEleArray[i] = ele.parent();					
				}
				this.loopEleArray[i].empty();
			}
		}
//		if(!CommonUtil.isEmpty(this.loopHtml)){
////			System.out.println("this.moduleEle = "+this.moduleEle.attr("id") + ", this.loopHtml = "+this.loopHtml);	              
//	        this.loopEle.removeAttr("custom");
////	        this.loopEle.empty();
//		}
	}
	
//	public void removeLoopElement(){								        
//		this.loopEle.remove();
//	}
//	
	
	public void setLoopElementContent(HashMap map){	
		setLoopElementContent(map, 0);
	}
	/*
	 * Loop content set to variable is because it can then append content freely
	 */
	public void setLoopElementContent(HashMap map, int index){		
		String html = null;
		if(this.loopHtml!=null){
			html = this.loopHtml[index];
		}
		if(CommonUtil.isEmpty(html)) return;
		Iterator it = map.keySet ().iterator ();
		String key = null;		
		
		
		while(it.hasNext ()){
			key = (String) it.next ();			
			if(map.get (key)!=null){				
				html = html.replaceAll ("\\%data."+key.toLowerCase ()+"\\%", (String) map.get (key));
			}else{
				html = html.replaceAll ("\\%data."+key.toLowerCase ()+"\\%", "");
			}
		}							
		
		this.loopEleArray[index].append(html.toString());		
//		this.loopCount++;		
	}
	
	/*
	 * Loop content set to variable is because it can then append content freely
	 */
	public void setElementContent(HashMap map){		
		String html = new String(this.moduleEle.html());
		Iterator it = null;
		String key = null;		
		
		if(map!=null){
			it = map.keySet ().iterator ();
			while(it.hasNext ()){
				key = (String) it.next ();			
				if(map.get (key)!=null){					
					html = html.replaceAll ("\\%data."+key.toLowerCase ()+"\\%", (String) map.get (key));
				}else{
					html = html.replaceAll ("\\%data."+key.toLowerCase ()+"\\%", "");
				}
			}						
		}else{
			html = "";
		}
		this.moduleEle.before(html);
		this.moduleEle.remove();
	}		
	
	public void moduleContentFinish(){
		this.moduleEle.before(this.moduleEle.html());
		this.moduleEle.remove();
	}
}
