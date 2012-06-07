/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc;

import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.client.page.login.PasswordManagementService;
import org.sigmah.server.auth.impl.BCrypt;
import org.sigmah.server.dao.Transactional;
import org.sigmah.server.mail.MailSender;
import org.sigmah.shared.domain.User;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Servlet handles password management tasks
 * like a sending email with password reset link or update the password
 * @author RaphaÃ«l Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PasswordManagementServlet extends RemoteServiceServlet implements
		PasswordManagementService {

	private final static Log log = LogFactory.getLog(PasswordManagementServlet.class);
	
	@Inject
	private Injector injector;

	private final Map<String, Locale> localeMap;

	public PasswordManagementServlet() {
		final HashMap<String, Locale> map = new HashMap<String, Locale>();
		map.put("en", Locale.ROOT);
		map.put("fr", Locale.FRENCH);

		this.localeMap = map;
	}
	
	/*
	 * Send email with a secure link to reset password after following a link
	 */
	@Override
	@Transactional
	public void forgotPassword(String email, String language, String hostUrl) throws Exception {
		
		final EntityManager entityManager = injector.getInstance(EntityManager.class);

		// find user
		final Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email");
		query.setParameter("email", email);
		final User user = (User) query.getSingleResult();
		
		// unique key is stored and sent by email
		String uniqueKey = UUID.randomUUID().toString();
		uniqueKey = uniqueKey.replaceAll("-", ""); //squeeze
		
		// store key and date 
		user.setChangePasswordKey(uniqueKey);
		user.setDateChangePasswordKeyIssued(new Date());
		entityManager.merge(user);
		
		// build a link to pass by email, so the user can reset the password following the link
		final StringBuilder linkBuilder = new StringBuilder(hostUrl);
		linkBuilder.append("?token=" + URLEncoder.encode(uniqueKey, "UTF-8"));
		linkBuilder.append( "&locale=" + language);
		linkBuilder.append("#passwordReset");
				
		// send email
		final SimpleEmail mail = new SimpleEmail();

		final Locale locale = getLocale(language);
		final ResourceBundle bundle = ResourceBundle.getBundle("org.sigmah.server.mail.MailMessages", locale);

		mail.setSubject(bundle.getString("resetPassword.subject"));
		mail.setMsg(MessageFormat.format(bundle.getString("resetPassword.content"), linkBuilder.toString()));

		mail.addTo(email, User.getUserCompleteName(user));

		final MailSender mailSender = injector.getInstance(MailSender.class);
		mailSender.send(mail);
		log.info(String.format("Password reset email has been sent to '%s' with a link [%s]", email,linkBuilder.toString())); 
	}

	/*
	 * Validate the password reset link token and return corresponding user email
	 */
	@Override
	public String validateAndGetUserEmailByToken(String token) throws Exception {
		final EntityManager entityManager = injector.getInstance(EntityManager.class);

		// find user
		final Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.changePasswordKey = :token");
		query.setParameter("token", token);
		final User user = (User) query.getSingleResult();
		
		// check for expiration
		long timeSpentInSecs = (new Date().getTime() - user.getDateChangePasswordKeyIssued().getTime())/1000;
		long limit=24*3600;//24 hours
		
		// after 24h link will be expired
		if(timeSpentInSecs>limit){
			user.setChangePasswordKey(null);
			throw new Exception();
		}
				
		return user.getEmail();
	}

	/*
	 * Updates user's password using email and a new password 
	 */
	@Override
	@Transactional
	public void updatePassword(String email, String password) throws Exception {

		final EntityManager entityManager = injector.getInstance(EntityManager.class);

		final Query query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email");
		query.setParameter("email", email);

		final User user = (User) query.getSingleResult();

		final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		user.setHashedPassword(hashedPassword);
		
		// disactivate password change key
		user.setChangePasswordKey(null);
		
		entityManager.merge(user);
		log.info(String.format("User's(%s) password has been updated using password reset link",email));
	}
	
	private Locale getLocale(final String key) {
		Locale locale = localeMap.get(key);

		if (locale == null)
			locale = Locale.ROOT;

		return locale;
	}

}
