/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.client.page.login.RetrievePasswordService;
import org.sigmah.server.auth.impl.BCrypt;
import org.sigmah.server.dao.Transactional;
import org.sigmah.server.mail.MailSender;
import org.sigmah.shared.domain.User;

/**
 * Servlet for the service {@link RetrievePasswordServlet}.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class RetrievePasswordServlet extends RemoteServiceServlet implements RetrievePasswordService {

    @Inject
    private Injector injector;

    private final Map<String, Locale> localeMap;

    public RetrievePasswordServlet() {
        final HashMap<String, Locale> map = new HashMap<String, Locale>();
        map.put("en", Locale.ROOT);
        map.put("fr", Locale.FRENCH);

        this.localeMap = map;
    }

    private Locale getLocale(final String key) {
        Locale locale = localeMap.get(key);

        if(locale == null)
            locale = Locale.ROOT;

        return locale;
    }

    @Override
    @Transactional
    public void retrievePassword(String email, String language) throws EmailException {
        final EntityManager entityManager = injector.getInstance(EntityManager.class);

        final Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email");
        query.setParameter("email", email);
        final User thisUser = (User) query.getSingleResult();

        // If the user doesn't exists, the following lines won't be executed
        final String password = generatePassword();
        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        final SimpleEmail mail = new SimpleEmail();

        final Locale locale = getLocale(language);
        final ResourceBundle bundle = ResourceBundle.getBundle("org.sigmah.server.mail.MailMessages", locale);

        mail.setSubject(bundle.getString("retrievePassword.subject"));
        mail.setMsg(MessageFormat.format(bundle.getString("retrievePassword.content"), password));

        mail.addTo(email, User.getUserCompleteName(thisUser));

        thisUser.setHashedPassword(hashedPassword);
        entityManager.merge(thisUser);

        final MailSender mailSender = injector.getInstance(MailSender.class);
        mailSender.send(mail);
    }

    /**
     * Generates a new password.
     * @return A password of 8 characters with 2 caps, 1 number and 1 special character.
     */
    private String generatePassword() {
        final StringBuilder password = new StringBuilder();

        int[] remainings = new int[] {4, 2, 1, 1};
        int size = 8;

        while(size > 0) {
            int nextChar = -1;
            while(nextChar == -1) {
                int alphabet = (int) (Math.random() * remainings.length);
                if(remainings[alphabet] > 0) {
                    nextChar = alphabets[alphabet][(int) (Math.random() * alphabets[alphabet].length)];
                    remainings[alphabet]--;
                }
            }
            password.append((char)nextChar);

            size--;
        }

        return password.toString();
    }
    private static final char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','p','q','r','s','t','u','v','w','x','y','z'};
    private static final char[] caps    = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final char[] numbers = {'1','2','3','4','5','6','7','8','9'};
    private static final char[] symbols = {'$','+','-','=','_','!','%','@'};
    private static final char[][] alphabets = {letters, caps, numbers, symbols};
}
