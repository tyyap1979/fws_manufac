import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.forest.common.util.FileUtil;


public class TestJsoup {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			StringBuffer html = new FileUtil().readFile("D:/Adam/Developement/Workspace/fws_manufac/WebContent/themes/award/cart.htm");
			Document doc = Jsoup.parse(html.toString(),"",Parser.xmlParser());
			System.out.println("doc = "+doc.html());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
