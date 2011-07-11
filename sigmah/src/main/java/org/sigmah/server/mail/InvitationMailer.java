/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.server.util.LocaleHelper;
import org.sigmah.server.util.logging.LogException;
import org.sigmah.server.util.logging.Trace;

import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class InvitationMailer implements Mailer<Invitation> {
	
	private final Configuration templateCfg;
	private final MailSender sender;
	static final String TEXT_TEMPLATE = "mail/Invite.ftl";
	
	//
	/**
	 * When send a inviting email to a new user from Sigmah,use this template
	 * @author HUZHE
	 */
	static final String TEXT_TEMPLATE_SIGMAH = "mail/SigmahInvite.ftl";
	

	@Inject
	public InvitationMailer(Configuration templateCfg, MailSender sender) {
		this.templateCfg = templateCfg;
		this.sender = sender;
	}

	@Override
	@Trace
	@LogException
	public void send(Invitation model, Locale locale) throws EmailException,
			TemplateException, IOException {

		ResourceBundle mailMessages = getResourceBundle(locale);
		SimpleEmail mail = new SimpleEmail();
		mail.addTo(model.getNewUser().getEmail(), model.getNewUser().getName());
		mail.addBcc("akbertram@gmail.com"); // for testing purposes
		mail.setSubject(mailMessages.getString("newUserSubject"));

		mail.setMsg(composeMessage(model, locale));

		sender.send(mail);
	}

    //Overload
	/* (non-Javadoc)
	 * @see org.sigmah.server.mail.Mailer#send(java.lang.Object, java.util.Locale, boolean
	 */
	@Trace
	@LogException	
	public void send(Invitation model, Locale locale, boolean sendBySigmah)
			throws EmailException, TemplateException, IOException {
	
		ResourceBundle mailMessages =  ResourceBundle.getBundle("org.sigmah.client.i18n/UIMessages",locale);
		SimpleEmail mail = new SimpleEmail();
		mail.addTo(model.getNewUser().getEmail(), model.getNewUser().getName());
		mail.setSubject(mailMessages.getString("newUserInvitationMailSubject"));

		//Create the string of message subject
		Object[] messageArguments = {
			    model.getNewUser().getName(),
			    model.getInvitingUser().getName(),
			    model.getInvitingUser().getEmail(),
			    model.getHostUrl(),
			    model.getNewUser().getChangePasswordKey()
			};
		
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);		
		formatter.applyPattern(mailMessages.getString("newUserInvitationMailMessage"));
		String messageSubject = formatter.format(messageArguments);
		
		mail.setMsg(messageSubject);
		
		sender.send(mail);
	}

	private ResourceBundle getResourceBundle(Locale locale) {
		
		return ResourceBundle.getBundle("org.sigmah.server.mail.MailMessages",locale);
		
	}

	private String composeMessage(Invitation model, Locale locale)
			throws IOException, TemplateException {

		StringWriter writer = new StringWriter();
		Template template = templateCfg.getTemplate(TEXT_TEMPLATE, locale);
		template.process(model, writer);
		return writer.toString();
	}
	

}
