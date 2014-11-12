import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SMTPServerConnector {
	
	public SMTPServerConnector(){}
	
   public void send(String from, String to, String subject, String text) {
      final String username = "";//change accordingly
      final String password = "";//change accordingly

      /* Use locally installed Fake SMTP server */
      String host = "localhost";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "false");
      props.put("mail.smtp.starttls.enable", "false");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "26");

      /* Create session...
       * ... Is Authenticator really needed???
       */
      Session session = Session.getInstance(props,new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
	   }});

      try {
    	  
	   /* Create message-object */
	   Message message = new MimeMessage(session);
	
	   /* Set "from" */
	   message.setFrom(new InternetAddress(from));
	
	   /* Set "to" */
	   message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
	
	   /* Set subject */
	   message.setSubject(subject);
	
	   /* Set Text */
	   message.setText(text);

	   /* Send E-Mail */
	   Transport.send(message);
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}