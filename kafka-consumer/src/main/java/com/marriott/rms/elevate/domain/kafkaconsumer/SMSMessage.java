package com.marriott.rms.elevate.domain.kafkaconsumer;

public class SMSMessage {

	private String to;
	private String subject;
	private String message;
	
	public SMSMessage(){}
	
	public SMSMessage(String to, String subject, String message){
		this.to = to;
		this.subject = subject;
		this.message = message;
	}
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
