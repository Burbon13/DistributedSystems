package ds.gae;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class Worker extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(Worker.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	String email = req.getParameter("email");
    	String state = req.getParameter("state");
    	logger.info("Sending email to " + email + " with status " + state);
    	
    	Session session = Session.getDefaultInstance(new Properties(), null); 
    		Message msg = new MimeMessage(session);
    		try {
				msg.setFrom(new InternetAddress("admin@todoapp.appspotemail.com", "Todo App"));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email, "")); 
				msg.setSubject("Car reservation"); 
				if (state.equals("success")) {
					msg.setText("Your reservations were successfully created! :D"); 
					logger.info("Sending SUCCESS EMAIL!");
				} else {
					msg.setText("Unable to confirm all of your reservations ... :(  Please try again."); 
					logger.info("Sending FAILURE EMAIL!");
				}
				Transport.send(msg);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} 
    }
}
