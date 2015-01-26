package org.sigmah.server.mail;

/**
 * Email container used for the {@link MailSender}
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class Email {

	/** From Address. */
	private String fromAddress;

	/** From name. */
	private String fromName;

	/** To addresses. */
	private String[] toAddresses;

	/** Cc addresses. */
	private String[] ccAddresses;

	/** Subject. */
	private String subject;

	/** content. */
	private String content;

	/** Name of the mail server host. */
	private String hostName;

	/** SMTP server port. */
	private int smtpPort;

	/** Mail content type. */
	private String contentType;

	/** Mail charset. */
	private String encoding;

	/** Username for smtp autenthication or <code>null</code>. */

	private String authenticationUserName;

	/** Password for smtp autenthication or<code>null</code>. */
	private String authenticationPassword;

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String[] getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String[] toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String[] getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String[] ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getAuthenticationUserName() {
		return authenticationUserName;
	}

	public void setAuthenticationUserName(String authenticationUserName) {
		this.authenticationUserName = authenticationUserName;
	}

	public String getAuthenticationPassword() {
		return authenticationPassword;
	}

	public void setAuthenticationPassword(String authenticationPassword) {
		this.authenticationPassword = authenticationPassword;
	}

}
