import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TestMail {
    private static final String SMTP_HOST_NAME = "127.0.0.1";  
	public static void main(String[] args) throws Exception{
		String from = "support@forestwebsolution.com";
		String to = "tyyap1979@hotmail.com";

		// Get system properties
		try{
			Properties props = System.getProperties();
	
			// Setup mail server
			props.put("mail.smtp.host", SMTP_HOST_NAME);
	
			// Get session
			Session session = Session.getDefaultInstance(props, null);
	
			// Define message
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("Hello JavaMail");
			message.setText("Welcome to JavaMail");
	
			// Send message			
			Transport.send(message);
			System.out.println("Sent....");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}		
}
