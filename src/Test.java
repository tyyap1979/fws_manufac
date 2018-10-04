import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
	public static void main(String[] args) {
		int[][] intArray = {
				{-3, -2, -1, 1},{-2, 2, 3, 4},{4,5,7,8}
		};
		try {
			Test t = new Test();
			t.countNegativeQuantity(intArray);
		} catch (Exception e) {	 
			e.printStackTrace();	 
		}	 
		
	}	 
	
	public void countNegativeQuantity(int[][] intArray) {
		
	}
}
