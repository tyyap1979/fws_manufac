package com.forest.common.constants;

public class PathConst
{	
	private static PathConst _instance = null;
	
	public static PathConst getInstance() {
		if (_instance==null) {
			synchronized (PathConst.class) {
				if (_instance==null) {
					_instance = new PathConst();					
				}
			}
		}
		
		return _instance;
	}
}
