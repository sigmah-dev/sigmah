/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.sigmah.server.util.logging.LogException;

import com.google.inject.Inject;

public class MailSenderImpl implements MailSender {

    final public static String HOST_NAME_KEY = "mail.hostname";   
    final public static String PORT_KEY = "mail.port";
    final public static String FROM_ADDRESS_KEY = "mail.from.address";
    final public static String FROM_NAME_KEY = "mail.from.name";
    final public static String FROM_AUTH_USERNAME_KEY = "mail.auth.username";
    final public static String FROM_AUTH_PASSWORD_KEY = "mail.auth.password";

    final private static String DEFAULT_HOST_NAME = "localhost";
    final private static String DEFAULT_PORT = "25";
    final private static String DEFAULT_ADDRESS = "mailer@sigmah.org";
    final private static String DEFAULT_NAME = "Sigmah";

    @Inject
    private Properties properties;

    @LogException
    @Override
    public void send(Email email) throws EmailException {

        email.setHostName(properties.getProperty(HOST_NAME_KEY, DEFAULT_HOST_NAME));
        email.setSmtpPort(Integer.parseInt(properties.getProperty(PORT_KEY, DEFAULT_PORT)));
        email.setFrom(properties.getProperty(FROM_ADDRESS_KEY, DEFAULT_ADDRESS),
                properties.getProperty(FROM_NAME_KEY, DEFAULT_NAME));

        // Authentication if specified.
        final String authUsername = properties.getProperty(FROM_AUTH_USERNAME_KEY, StringUtils.EMPTY);
        final String authPassword = properties.getProperty(FROM_AUTH_PASSWORD_KEY, StringUtils.EMPTY);
        if (!StringUtils.EMPTY.equals(authUsername) && !StringUtils.EMPTY.equals(authPassword)) {
            email.setAuthenticator(new DefaultAuthenticator(authUsername, authPassword));
        }

        email.send();
    }

}
