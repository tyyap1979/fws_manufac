package com.forest.common.util;
 
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class CookiesUtil
{
//	private Logger logger = Logger.getLogger (this.getClass ());
	private HttpServletRequest req = null;
	private HttpServletResponse resp = null;
	
	private final String CURRENCY = "user.currency";	
	private final String NAME = "user.name";
	private final String LANGUAGE = "user.language";
	private final String CART = "cart";
	
	public static final String TRANINFO = "trans.info"; 
	
	public CookiesUtil(HttpServletRequest request, HttpServletResponse response){
		this.req = request;
		this.resp = response;
	}
	
	public void setSessionCookies(String name, String value){
		Cookie cookies = new Cookie(name, value);		
		this.resp.addCookie(cookies);	
	}
	
	public void setCookies(String name, String value, int numOfDay){
		Cookie cookies = new Cookie(name, value);
		if(numOfDay>0){
			cookies.setMaxAge (numOfDay * 86400); //365 * 24 * 3600
		}
		cookies.setPath ("/");
		this.resp.addCookie(cookies);		
	}
	
	public void setDomainCookies(String name, String value, int numOfDay){
		String DOMAIN_NAME = ResourceUtil.getSettingValue ("domain.name");		
		Cookie cookies = new Cookie(name, value);
		if(numOfDay>0){
			cookies.setMaxAge (numOfDay * 86400); //365 * 24 * 3600
		}
		cookies.setDomain ("."+DOMAIN_NAME);
//		cookies.setPath ("/");
		this.resp.addCookie(cookies);		
	}
	
	public void removeCookies(String name){	
		Cookie cookie = new Cookie(name, "");
		cookie.setMaxAge(0);
		cookie.setPath ("/");
		resp.addCookie (cookie);	
	}
	
	
	
	public String readCookies(String name){
		String value = "";
		Cookie[] cookies = this.req.getCookies();

		if (cookies != null){
			for (int i =0; i< cookies.length; i++){				
				if(name.equals (cookies[i].getName())){					
					value = cookies[i].getValue();
					break;
				}
			}
		}
		
		return value;
	}
	
	public void setTransInfo(String text){
		setCookies (TRANINFO, text, 1);
	}
		
	public String getTransInfo(){
		return readCookies (TRANINFO);
	}
	
	public void setUserInfo(String str){
		setCookies ("user.info", str, 365);
	}
	public String getUserInfo(){
		return readCookies ("user.info");
	}
	
	public void setAdminUserInfo(String str){
		setCookies ("admin.user.info", str, 0);
	}
	
	public String getAdminUserInfo(){
		return readCookies ("admin.user.info");
	}
	
	public void setUser(String name){
		setCookies (NAME, name, 365);
	}
		
	public String getUser(){
		return readCookies (NAME);
	}
	
	public void setCurrency(String code){
		setCookies (CURRENCY, code, 365);
	}
		
	public String getCurrency(){
		return readCookies (CURRENCY);
	}	
	
	public void setLanguage(String code){
		setCookies (LANGUAGE, code, 365);
	}
		
	public String getLanguage(){
		return readCookies (LANGUAGE);
	}
	
	public void setCartID(String code){
		setCookies (CART, code, 365);
	}
		
	public String getCartID(){
		return readCookies (CART);
	}
}
