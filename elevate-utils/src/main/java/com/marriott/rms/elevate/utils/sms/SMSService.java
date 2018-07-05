package com.marriott.rms.elevate.utils.sms;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(SMSService.class);

	private static final String SMTP_SERVER = "smtp.marriott.com";//"smtp.office365.com";
	private static final int SMTP_SERVER_PORT = 25;//587;

	//private static final String IMAP_SERVER = "outlook.office365.com";

	private final String from = "srmun072@marriott.com";
	private static final String USER = "srmun072@marriott.com";
	private static final String PASSWORD = "winter@16";

	public void sendEmail(List<String> recepientList, String subject,
			String messageContent) {

		Session session = Session.getInstance(this.getProperties());
					
		try {
			final Message message = new MimeMessage(session);

			String to = "8606806235@txt.att.net";
			Address[] recepients = new Address[] { new InternetAddress(to)
			// ,new InternetAddress(another_to)
			};

			message.setRecipients(Message.RecipientType.TO, recepients);
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);
			message.setText(messageContent);
			message.setSentDate(new Date());
			LOG.info("Ready to send the message..");
			Transport.send(message,USER, PASSWORD);
			LOG.info("Message Sent to " + recepientList);

		} catch (Exception ex) {
			LOG.info("Error Sending the message: " + ex.getMessage(), ex);
		}

	}

	public Properties getProperties() {
		final Properties config = new Properties();
		config.put("mail.smtp.auth", "true");
		config.put("mail.smtp.starttls.enable", "true");
		config.put("mail.smtp.host", SMTP_SERVER);
		config.put("mail.smtp.port", SMTP_SERVER_PORT);
		config.put("mail.debug", "true");		

		config.put("mail.smtp.socketFactory.port", SMTP_SERVER_PORT); 
		config.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
		return config;
	}
	




}
