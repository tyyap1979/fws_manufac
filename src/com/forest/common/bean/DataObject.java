package com.forest.common.bean;

import com.forest.common.constants.SearchConst;

public class DataObject implements Cloneable
{
//	private Logger logger = Logger.getLogger (this.getClass ());
	public String name = "";
	public int type = 0;	
	public boolean nullable = false;
	public int length = 0;	
	public int precision = 0;
	public String stringValue = null;	
	public boolean autoIncrement = false;
	public int searchOption = SearchConst.EQUAL;
	public String prefix = null;	
	public String joinType = "And";
	public boolean key = false;	
	public String validation = null;
//	public String displayObjectType = null; // text, checkbox, radio, select
	public String selectModCode = null;
	
	public DataObject(){
		
	}
	
	public DataObject(String name, String stringValue){
		this.name = name;
		this.stringValue = stringValue;
	}
	
	public DataObject(String name, int type, boolean nullable, int length, int precision){
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.length = length;
		this.precision = precision;
	}	
	
	public boolean equals(Object pObj){		
		return (String.valueOf (pObj).equals (stringValue));
	}
	
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
	}
	
	public String toString(){
		return name;
	}
}
