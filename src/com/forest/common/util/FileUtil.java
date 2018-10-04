package com.forest.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
//import java.util.Scanner;

import org.apache.log4j.Logger;

public class FileUtil
{
	private Logger logger = Logger.getLogger (this.getClass ());
	public static void main(String [] args) {
		try{
			
		}catch(Exception e){
			
		}		
	}
	
	public void copyDirectory(File sourceLocation , File targetLocation)throws IOException {        
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }        
    }
	
	public void copyFile(File sourceLocation , File targetLocation)throws IOException {
        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);
        
        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();        
        
        sourceLocation.delete ();
    }
	
	public void moveFile(String filename, String toDirectory){
		// File (or directory) to be moved
	    File file = new File(filename);
	    
	    // Destination directory
	    File dir = new File(toDirectory);
	    if(!dir.exists ()){
	    	dir.mkdir ();
		}
	    // Move file to new directory
	    logger.debug("toFile = "+toDirectory + "" + file.getName());
	    File toFile = new File(toDirectory + "" + file.getName());
	    if(file.exists() && toFile.exists()) toFile.delete();
	    
	    boolean success = file.renameTo(new File(dir, file.getName()));
	    if (!success) {
	        logger.debug("faile move "+filename);
	    }
	}
	
	public StringBuffer readFile(String path) throws OwnException{
		StringBuffer mBuffer = new StringBuffer();
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
		try{
			fstream = new FileInputStream(path);
	    	in = new DataInputStream(fstream);
	    	br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		    	mBuffer.append (strLine);
		    	mBuffer.append(System.getProperty("line.separator"));
		    }
		}catch(Exception e){
			logger.error(e, e);
//			throw new OwnException(e);
		}finally{
			try{
				if(br!=null) br.close (); br = null;
	    		if(in!=null) in.close (); in = null;
		    	if(fstream!=null) fstream.close (); fstream = null;
			}catch(Exception e){
				logger.error (e,e);
			}
		}

	    return mBuffer;
	}
	
	public void deleteFolder(String folderName){
		logger.debug ("deleteFolder = "+folderName);
		File m_oFolder = new File(folderName);
		if(m_oFolder.isDirectory ()){
			deleteDirectory(m_oFolder);
		}else{
			m_oFolder.delete ();
		}
		
		
		m_oFolder = null;
	}
	
	public boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }

	public ArrayList listDirectory(String filePath) {
		File path = new File(filePath);
		ArrayList dataList = new ArrayList();
		HashMap dataMap = null;
	    if( path.exists() ) {
	    	File[] files = path.listFiles();
	    	for(int i=0; i<files.length; i++) {
	    		dataMap = new HashMap();
	    		dataMap.put("filename", files[i].getName());	    		
	    		dataList.add(dataMap);
	    	}
	    }
	    path = null;
	    return dataList;
	}
	
	public void createFolder(String folderName){
		File m_oFolder = new File(folderName);
		if(!m_oFolder.exists ()){
			m_oFolder.mkdir ();
		}
		m_oFolder = null;
	}
	
	public void writeFile(String content, String path) throws OwnException{
		BufferedWriter out = null;
        
        try{
        	logger.debug("Write File to "+path);
        	out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF8"));
        	out.write(content);
        }catch(Exception e){
        	throw new OwnException(e);
        }finally{
        	try{
	        	out.close();	        	
        	}catch(Exception e){
        		logger.error (e, e);
        	}
        }
	}
	
//	public StringBuffer readFile(String filename, String encoding) {	    
//		StringBuffer text = new StringBuffer();
//	    String NL = System.getProperty("line.separator");
//	    Scanner scanner = null;
//	    try {
//	    	scanner = new Scanner(new File(filename), encoding);
//	    	while (scanner.hasNextLine()){
//	    		text.append(scanner.nextLine() + NL);
//	    	}
//	    }catch(Exception e){
//	    	//logger.error(e);
//	    }
//	    finally{
//	    	if(scanner!=null) scanner.close();
//	    }	    
//	    return text;
//	  }

	/** Write fixed content to the given file. */
	public void writeFile(String filename, String content, String encoding) throws IOException  {	    
	    Writer out = new OutputStreamWriter(new FileOutputStream(filename), encoding);
	    try {
	      out.write(content);
	    }
	    finally {
	      out.close();
	    }
	  }

	
	public File getFile(String fileName){
		return new File(fileName);
	}
	
	public ArrayList readFolder(String folderName)throws OwnException{
		logger.debug ("readFolder = "+folderName);
		File folder = new File(folderName);	
		File file = null;
		String[] files = null;
		String[] fileAttribute = null;
		Date date = null;
		ArrayList mList = new ArrayList();
		try{			
			if(folder.exists ()){
				files = folder.list ();
				if(files!=null && files.length>0){
					for(int i=0; i<files.length; i++){
						fileAttribute = new String[4];
						file = new File(folder.getAbsolutePath ()+"/"+files[i]);	
						date = new Date(file.lastModified ());
												
						fileAttribute[0] = (file.isDirectory ())?"D":"F";
						fileAttribute[1] = files[i];
						fileAttribute[2] = String.valueOf (file.length ());
						fileAttribute[3] = String.valueOf (date);
						mList.add (fileAttribute);
//						logger.debug (""+files[i] + " : " + date);
						file = null;
					}
				}
			}
		}catch(Exception e){
			throw new OwnException(e);
		}finally{			
			folder = null;
		}
		
		return mList;
	}
	
	public String convertStreamToString(InputStream is)throws OwnException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (Exception e) {
        	throw new OwnException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error (e, e);
            }
        }
 
        return sb.toString();
    }
	
	public static boolean isExist(String file){
		File f = new File(file);
		boolean isE = false;
		if(f.exists()){
			isE = true;			
		}
		f = null;
		return isE;
	}
}
