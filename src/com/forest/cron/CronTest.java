package com.forest.cron;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.forest.common.util.CommonUtil;
import com.forest.common.util.DBUtil;
import com.forest.common.util.FileUtil;

public class CronTest
{
	private Connection conn = null;
	private Logger logger = Logger.getLogger (CronTest.class);
	
	public static void main(String[] args) throws Exception{		
		CronTest t = new CronTest();	
		t.conn = new DBUtil().getDirectConnection ();
		t.readMain();	
	}			
	
	private void processMain()throws Exception{
		StringBuffer mBuffer = new StringBuffer();
		FileInputStream fstream = null;
	    DataInputStream in = null;
	    BufferedReader br = null;
	    String strLine = null;
	    String[] csvArray = null;
		try{
			PreparedStatement pstmt = conn.prepareStatement("Insert Into agentprofile(companyid,name,mobileno,status) values('jhomedesign',?,?,'A')");
			fstream = new FileInputStream("D:/Adam/AgentList/propertyguru.csv");
	    	in = new DataInputStream(fstream);
	    	br = new BufferedReader(new InputStreamReader(in));
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		    	csvArray = strLine.split(",");
		    	pstmt.setString(1, csvArray[2]);
		    	pstmt.setString(2, csvArray[1]);
		    	pstmt.execute();
		    	logger.debug("strLine = "+strLine);
		    	//processDetail(strLine);
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
	}
	
	private void save(){
		
	}
	
	private void processDetail(String url)throws Exception{				
		try{
			Document doc = Jsoup.connect(url).get();     
	        Elements media = doc.select(".summary2").select(".orangebold");
	        
	        Element src = null;
	        String link = null;
	        int count = 0;

	        
	        count = media.size();
	        if(count>0){	        	
		        for (int i=0; i<count;i++) {
		        	src = media.get(i);
		        	link = src.text();
		        	System.out.println(url + "," + link);
		        	
		        }		        
	        }	        
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			DBUtil.free(conn, null, null);
		}
	}
	
	private void readMain()throws Exception{
		String url = "http://resultpru13.spr.gov.my/module/keputusan/paparan/5_KeputusanDR.php";
		for(int i=109; i<110; i++){
			readDetail(url+"?kod="+i+"00");
		}
	}
	
	private String getNumber(String num){
		return num.replaceAll(",", "").replaceAll("%", "");
	}
	private void readDetail(String url)throws Exception{				
		PreparedStatement pstmtUpdate = null;
		StringBuffer queryUpdate = new StringBuffer();
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");	 
	    
	    PreparedStatement pstmt = conn.prepareStatement("Insert Into undi(state,parliment,bn,pas,pkr,dap,totalvote,inboxvote,rejectvote,keluar,notreturn,percentage,majority,winner) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		try{
//			queryUpdate.append("Update currency Set ");
//			queryUpdate.append("rate=?, updatedate=now()");
//			queryUpdate.append(" Where currencycode=?");
//			pstmtUpdate = conn.prepareStatement(queryUpdate.toString());
			
			logger.debug("Processing "+url);
			Document doc = Jsoup.connect(url).get();     
	        Elements media = doc.select("table td");
	        
	        Elements tr = null;
	        Elements td = null;
   
	        int count = 0;
	        int trCount = 0;
	        int tdCount = 0;
	        String text = "";
	        
	        String negeri = null;
	        String parliment = null;

	        String[] info = new String[7];
	        String[] infoKey = new String[7];
	        ArrayList calonArray = new ArrayList();
	        String[] calon = new String[4];
	        String winner = "";
	        
	        count = media.size();
	        int start = 0;
	        int end = 0;
	        infoKey[0] = "JUMLAH PEMILIH BERDAFTAR : ";
	        infoKey[1] = "JUMLAH KERTAS UNDI DITOLAK : ";
	        infoKey[2] = "JUMLAH KERTAS UNDI DALAM PETI UNDI : ";
	        infoKey[3] = "JUMLAH KERTAS UNDI DIKELUARKAN : ";
	        infoKey[4] = "JUMLAH KERTAS UNDI TIDAK DIKEMBALIKAN : ";
	        infoKey[5] = "PERATUS PENGUNDIAN : ";
	        infoKey[6] = "MAJORITI : ";	        
	        
	        
	        
	        if(count>0){	        	
		        for (int i=0; i<count;i++) {
		        	text = media.get(i).text();
		        	if(negeri==null && text.indexOf("NEGERI : ")!=-1){
		        		negeri = text.substring("NEGERI : ".length(), text.indexOf("BAHAGIAN PILIHAN RAYA : "));
		        		parliment = text.substring(text.indexOf("P."));		        		
		        	}
		        	if(text.indexOf("NAMA PENUH PARTI BILANGAN UNDI KEPUTUSAN ")!=-1){
		        		tr = media.get(i).select("tr[valign=center]");
		        		trCount = tr.size();
		        		for(int a=0; a<trCount; a++){
		        			td = tr.get(a).select("td");
		        			tdCount = td.size();
		        			calon = new String[4];
		        			for(int d=0; d<tdCount; d++){
		        				calon[d] = td.get(d).text();			        				
		        			}
		        			if("MENANG".equals(calon[3])){
		        				winner = calon[1];
		        			}
		        			calonArray.add(calon);
		        		}
		        		
		        		text = text + " ";
//		        		System.out.println("TD:"+text);
		        		for(int a=0; a<infoKey.length; a++){
		        			start = text.indexOf(infoKey[a])+infoKey[a].length();
			        		end = text.indexOf(" ",start);
			        		//System.out.println("start:"+start+", end:"+end);
			        		info[a] = text.substring(start, end);
//			        		System.out.println(infoKey[a]+info[a]);
		        		}
		        		
		        		break;
		        	}
		        }	
		        
		        pstmt.setString(1, negeri);
		        pstmt.setString(2, parliment);
		        
		        System.out.println("Negeri:"+negeri);
        		System.out.println("Parliment:"+parliment);
        		
        		pstmt.setString(3, "0");
        		pstmt.setString(4, "0");
        		pstmt.setString(5, "0");
        		pstmt.setString(6, "0");
		        for(int i=0;i<calonArray.size(); i++){
		        	calon = (String[])calonArray.get(i);
	        		System.out.println("Name = "+calon[0].substring(3));
	        		System.out.println("Parti = "+calon[1]);
	        		System.out.println("Votes = "+getNumber(calon[2]));
	        		System.out.println("Result = "+calon[3]);
	        		if(calon[1].equalsIgnoreCase("BN")){
	        			pstmt.setString(3, getNumber(calon[2]));
	        		}
	        		if(calon[1].equalsIgnoreCase("PAS")){
	        			pstmt.setString(4, getNumber(calon[2]));
	        		}
	        		if(calon[1].equalsIgnoreCase("PKR")){
	        			pstmt.setString(5, getNumber(calon[2]));
	        		}
	        		if(calon[1].equalsIgnoreCase("DAP")){
	        			pstmt.setString(6, getNumber(calon[2]));
	        		}
	        	}
		        for(int i=0;i<infoKey.length; i++){		        	
	        		System.out.println(infoKey[i]+getNumber(info[i]));
	        		pstmt.setString(7+i, getNumber(info[i]));
	        	}
		        
		        pstmt.setString(14, winner);	
//		        pstmt.execute();
	        }	        
		}catch(Exception e){
			logger.error(e, e);
		}finally{
			DBUtil.free(null, pstmt, null);
		}
	}	
}
