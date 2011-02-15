/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.mail;

import com.google.inject.Inject;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.sigmah.server.util.logging.LogException;

public class MailSenderImpl implements MailSender {
    final public static String HOST_NAME_KEY = "mail.hostname";
    final public static String FROM_ADDRESS_KEY = "mail.from.address";
    final public static String FROM_NAME_KEY = "mail.from.name";

    final private static String DEFAULT_HOST_NAME = "localhost";
    final private static String DEFAULT_ADDRESS = "mailer@sigmah.org";
    final private static String DEFAULT_NAME = "Sigmah";

    @Inject
    private Properties properties;

    @LogException
    @Override
    public void send(Email email) throws EmailException {
        for(final Map.Entry<Object, Object> entry : properties.entrySet())
            System.out.println("PROPS "+entry.getKey().toString()+" = "+entry.getValue().toString());

        email.setHostName(properties.getProperty(HOST_NAME_KEY, DEFAULT_HOST_NAME));
        email.setFrom(properties.getProperty(FROM_ADDRESS_KEY, DEFAULT_ADDRESS), properties.getProperty(FROM_ADDRESS_KEY, DEFAULT_NAME));
        email.send();
    }

}
