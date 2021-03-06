package com.challonge_elo_calculator.calculator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/*
 * Basic utility class to contain static methods to be used across the codebase.
 * 
 * TODO: When placing into production, remove all printStackTrace exception handling
 */
public class Utility {

	/*
	 * Send a basic email. 
	 * 
	 * First parameter, the destination, is the email address to send to.
	 * 
	 * The originator parameter contains firstly the email address to send from, and a second element contains the
	 * password needed to access that account.
	 * 
	 * Third string is the subject line of the email.
	 * 
	 * Final parameter is the body of the email to be sent, as a single string.
	 * 
	 */
	public static void sendEmail(String dest, String originator[], String subject, String mess) {    
		// Recipient's email ID needs to be mentioned.
		String to = dest;

		// Sender's email ID needs to be mentioned
		String from = originator[0];

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable","true"); 
		properties.put("mail.smtp.EnableSSL.enable","true");
		properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
  
		// Get the default Session object.
		SmtpAuthenticator authentication = new SmtpAuthenticator(originator[0], originator[1]);
		Session session = Session.getInstance(properties, authentication);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);
         
			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			message.setText(mess);

			// Send message
			Transport.send(message);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
   
	/*
	 * checks if a string is essentially null
	 */
	public static boolean isNull(String in) {
		boolean toReturn = false;
		
		if (in == null || in.isEmpty() || in.trim().isEmpty() || in.equals("null")) 
			toReturn = true;
		
		return toReturn;
	}
	
	/*
	 * Gets the body of an email to send.
	 */
	public static String getBody(String method, Exception e) {
		String mess = "";
		
		//get current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		mess += "Error occured in method: " + method + ", at " + dateFormat.format(date) + "\n";
		mess += e.getStackTrace() + "\n\n";
		
		return mess;
	}
	
	/*
	 * Gets the body of an email to send.
	 * 
	 * In this overloaded method version, an extra string is provided.
	 * 
	 * This string is included in the email, and can be some special information from the method itself,
	 * as a probable cause for failure, for example.
	 */
	public static String getBody(String method, Exception e, String otherInfo) {
		String mess = "";
		
		//get current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		mess += "Error occured in method: " + method + ", at " + dateFormat.format(date) + "\n";
		mess += "Additional information provided by program includes: " + otherInfo + "\n";
		mess += e.getStackTrace() + "\n\n";
		
		return mess;
	}
	
	/*
	 * Gets the body of an email to send.
	 * 
	 * In this overloaded method version, no exception is needed.
	 */
	public static String getBody(String method, String otherInfo) {
		String mess = "";
		
		//get current time
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		
		mess += "Error occured in method: " + method + ", at " + dateFormat.format(date) + "\n";
		mess += "Additional information provided by program includes: " + otherInfo + "\n";
		
		return mess;
	}
}