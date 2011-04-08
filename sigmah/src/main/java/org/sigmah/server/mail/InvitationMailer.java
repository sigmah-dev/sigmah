/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.server.util.logging.LogException;
import org.sigmah.server.util.logging.Trace;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

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
	
		ResourceBundle mailMessages = getResourceBundle(locale);
		SimpleEmail mail = new SimpleEmail();
		mail.addTo(model.getNewUser().getEmail(), model.getNewUser().getName());
		mail.setSubject(mailMessages.getString("Sigmah.newUserSubject"));

		mail.setMsg(composeMessage(model, locale,sendBySigmah));

		sender.send(mail);
	}

	private ResourceBundle getResourceBundle(Locale locale) {
		return ResourceBundle.getBundle("org.sigmah.server.mail.MailMessages",
				locale);
	}

	private String composeMessage(Invitation model, Locale locale)
			throws IOException, TemplateException {

		StringWriter writer = new StringWriter();
		Template template = templateCfg.getTemplate(TEXT_TEMPLATE, locale);
		template.process(model, writer);
		return writer.toString();
	}

   
	
	/**
	 * Return a message text of a email which will be sent when a new user is created
	 * on Sigmah system.
	 * @author HUZHE
	 * @param model 
	 * An invitation object has the inviting user object and the invited user
	 * @param locale
	 * A locale  object of the invited user
	 * @param sendBySigmah
	 * Indicate if the email is send by Sigmah system,not ActivityInfo
	 * @return String
	 * Return the texts of the email
	 * @throws IOException
	 * If an error occurred
	 * @throws TemplateException
	 * If an error occurred
	 */
	private String composeMessage(Invitation model, Locale locale,boolean sendBySigmah)
			throws IOException, TemplateException {

		if(sendBySigmah==true)
		{
			StringWriter writer = new StringWriter();
			Template template = templateCfg.getTemplate(TEXT_TEMPLATE_SIGMAH, locale);
			template.process(model, writer);
			return writer.toString();
		}
		else
		{
			return composeMessage(model,locale);
		}
	}
	

}
