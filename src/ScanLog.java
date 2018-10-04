import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.forest.common.util.DBUtil;
import com.forest.common.util.FileUtil;
import com.forest.common.util.OwnException;


public class ScanLog {
	public Connection _dbConnection = null;
	public static void main(String[] args) {
		ScanLog a = new ScanLog();
		try{
			a.process();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void process() throws Exception{
		StringBuffer mBuffer = new StringBuffer();
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
	    File f = null;
	    int notExistCount = 0;
	    int totalFileCount = 0;
		try{
			fstream = new FileInputStream("D:/Adam/Developement/Log/fws.log");
	    	in = new DataInputStream(fstream);
	    	br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
	    	while (true) {
	    		strLine = br.readLine();
	    	    if (strLine == null) {
	    	        //wait until there is more of the file for us to read
	    	        Thread.sleep(5000);
	    	    }
	    	    else {
	    	        System.out.println("Adam - "+strLine);
	    	    }
	    	}
		}catch(Exception e){
			e.printStackTrace();
//			throw new OwnException(e);
		}finally{
			
		}
	}
	
	public void process2() throws Exception{
		FileUtil f = new FileUtil();
		ArrayList a = f.readFolder("D:/Finishing/");
		StringBuffer b = f.readFile("D:/Finishing/query.txt");
		String[] fileAttribute = null;
		int fileCount = 0;
		File noUseFile = null;
		for(int i=0; i<a.size(); i++){
			fileAttribute = (String[])a.get(i);
			if(b.indexOf(fileAttribute[1].substring(0, fileAttribute[1].length()-4))==-1){
				System.out.println(fileAttribute[1]+" No use");
				noUseFile = new File("D:/Finishing/"+fileAttribute[1]);
				noUseFile.delete();
				noUseFile = null;
				
				fileCount++;
			}
		}
		System.out.println("Total "+fileCount+" files not using.");
	}
	
	
	public void rename() throws Exception{
		StringBuffer mBuffer = new StringBuffer();
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
	    File f = null;
	    FileUtil fu = new FileUtil();
	    int notExistCount = 0;
	    int totalFileCount = 0;
	    String wrongName = null;
		try{
			fstream = new FileInputStream("D:/Finishing/rename.txt");
	    	in = new DataInputStream(fstream);
	    	br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		    	wrongName = strLine;
		    	wrongName = wrongName.substring(0, 2) + wrongName.substring(3);
		    	
		    	f = new File("D:/Finishing/From Mark/"+wrongName);
		    	if(f.exists()){
		    		fu.copyDirectory(f, new File("D:/Finishing/"+strLine));
		    		System.out.println(strLine);		    		
		    	}
		    }		    
		}catch(Exception e){
			e.printStackTrace();
//			throw new OwnException(e);
		}finally{
			
		}
	}
}
