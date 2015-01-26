/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.server.util.logging.LogException;
import org.sigmah.server.util.logging.Trace;
import org.sigmah.shared.domain.User;

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
     * 
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
    public void send(Invitation model, Locale locale) throws EmailException, TemplateException, IOException {

        ResourceBundle mailMessages = getResourceBundle(locale);
        SimpleEmail mail = new SimpleEmail();
        mail.addTo(model.getNewUser().getEmail(), model.getNewUser().getName());
        mail.addBcc("akbertram@gmail.com"); // for testing purposes
        mail.setSubject(mailMessages.getString("newUserSubject"));

        mail.setMsg(composeMessage(model, locale));

        sender.send(mail);
    }

    // Overload
    /*
     * (non-Javadoc)
     * 
     * @see org.sigmah.server.mail.Mailer#send(java.lang.Object,
     * java.util.Locale, boolean
     */
    @Trace
    @LogException
    public void send(Invitation model, Locale locale, boolean sendBySigmah) throws EmailException, TemplateException,
            IOException {

        final ResourceBundle mailMessages = getResourceBundle(locale);

        SimpleEmail mail = new SimpleEmail();
        mail.addTo(model.getNewUser().getEmail(), model.getNewUser().getName());

        MessageFormat formatter = new MessageFormat(mailMessages.getString("newUserInvitationMailSubject"), locale);
        mail.setSubject(formatter.format(null));

        // Create the string of message subject
        final Object[] messageArguments = { 
                User.getUserCompleteName(model.getNewUser()),
                User.getUserCompleteName(model.getInvitingUser()), 
                model.getInvitingUser().getEmail(),
                model.getHostUrl(),
                model.getNewUser().getEmail(),
                model.getNewUserPassword() };

        formatter = new MessageFormat(mailMessages.getString("newUserInvitationMailMessage"), locale);
        String messageSubject = formatter.format(messageArguments);

        mail.setMsg(messageSubject);

        sender.send(mail);
    }

    private ResourceBundle getResourceBundle(Locale locale) {

        final String bundleName = "org.sigmah.server.mail.MailMessages";

        ResourceBundle mailMessages;
        try {
            mailMessages = ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException e) {
            // A locale for which we're sure that there is a resource bundle
            // available.
            locale = Locale.ENGLISH;
            mailMessages = ResourceBundle.getBundle(bundleName, locale);
        }

        return mailMessages;

    }

    private String composeMessage(Invitation model, Locale locale) throws IOException, TemplateException {

        StringWriter writer = new StringWriter();
        Template template = templateCfg.getTemplate(TEXT_TEMPLATE, locale);
        template.process(model, writer);
        return writer.toString();
    }

}
