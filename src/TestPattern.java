
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TestPattern {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestPattern a = new TestPattern();
	}
	
	public void testPattern(){
		StringBuffer param = new StringBuffer("019 276 8885 adadadae");
		// Get Quotation Number
		Pattern pat = Pattern.compile("\\d+");
		Matcher match = pat.matcher(param);		

		while(match.find()){
			match.appendReplacement(param, "X");
    	}
		System.out.println("param="+param);
	}
	
	public void test2(){
		Pattern p = Pattern.compile("\\d+");
	    Matcher m = p.matcher("019 276 8885 adadadae");
	    StringBuffer s = new StringBuffer();
	    while (m.find())
	        m.appendReplacement(s, "X");
	    System.out.println(s.toString());
	}
	
	public String getPhoneNumber(String phone){
		Pattern pat = Pattern.compile("\\d+");
		Matcher match = null;
		match = pat.matcher(phone);
		StringBuffer rtnBuffer = new StringBuffer();
		
		while(match.find()){
			rtnBuffer.append(match.group());
    	}
		return rtnBuffer.toString();
	}

}
