package org.sigmah.client.security;

import java.util.Date;

import org.sigmah.client.ui.presenter.LoginPresenter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.result.Authentication;

import com.google.gwt.user.client.Cookies;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Provides the {@link Authentication}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AuthenticationProvider implements Provider<Authentication> {

	/**
	 * <p>
	 * The cached authenticated user data.<br/>
	 * This data is updated at each page access ; see {@link org.sigmah.client.event.EventBus EventBus}.
	 * </p>
	 * <p>
	 * <em>Should never be {@code null}.</em>
	 * </p>
	 */
	private Authentication authentication;

	/**
	 * Returns the current authentication.<br/>
	 * If no user is currently authenticated, the method returns an empty {@link Authentication} instance (with
	 * {@code null} token).
	 * 
	 * @return The current authentication or empty authentication (never returns {@code null}).
	 */
	@Override
	public Authentication get() {
		
		if (isAnonymous()) {
			clearAuthentication();
		}

		return authentication;
	}
	
	/**
	 * Returns the authentication token.
	 * <p>
	 * When anonymous, <code>null</code> is returned.
	 * </p>
	 * 
	 * @return The authentication token or <code>null</code> if anonymous.
	 */
	public String getAuthenticationToken() {
		
		return Cookies.getCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE);
	}

	/**
	 * <p>
	 * Logins the given {@code authentication} by setting cookies and updating cached data.
	 * </p>
	 * <p>
	 * <em>Should be called <b>exclusively</b> by {@link LoginPresenter}.</em>
	 * </p>
	 * 
	 * @param authentication
	 *          The authentication. Its {@code authenticationToken} <b>must</b> be set.
	 */
	public void login(final Authentication authentication) {

		if (authentication == null) {
			clearAuthentication();
			return;
		}

		// Cookies properties.
		final String path = org.sigmah.shared.Cookies.COOKIE_PATH;
		final String domain = org.sigmah.shared.Cookies.COOKIE_DOMAIN;
		final boolean secure = org.sigmah.shared.Cookies.COOKIE_SECURED;

		// Sets the cookies.
		Cookies.setCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE, authentication.getAuthenticationToken(), oneDayLater(), domain, path, secure);
		Cookies.setCookie(org.sigmah.shared.Cookies.LANGUAGE_COOKIE, authentication.getLanguage().getLocale(), oneDayLater(), domain, path, secure);

		// Caches the authentication data.
		this.authentication = authentication;
	}

	/**
	 * <p>
	 * Updates the cached {@link Authentication} instance.
	 * </p>
	 * <p>
	 * <em>Should be called <b>exclusively</b> by {@link org.sigmah.client.event.EventBus}.</em>
	 * </p>
	 * 
	 * @param authentication
	 *          The authentication. Its {@code authenticationToken} is automatically updated with the one set in cookie.
	 */
	public void updateCache(final Authentication authentication) {

		if (authentication == null) {
			clearAuthentication();
			return;
		}

		// Caches the authentication data.
		authentication.setAuthenticationToken(Cookies.getCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE));
		this.authentication = authentication;
	}

	/**
	 * <p>
	 * Clears the current authentication (cookies + cached authentication data).
	 * </p>
	 * <p>
	 * Maintains the {@link Language} previously set.
	 * </p>
	 * 
	 * @return {@code true} if the authentication has been successfully cleared.
	 */
	public boolean clearAuthentication() {

		Cookies.removeCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE, org.sigmah.shared.Cookies.COOKIE_PATH);
		// TODO Also clear GXT theme cookie?

		authentication = new Authentication(authentication.getLanguage());

		return true;
	}

	/**
	 * Checks if no user is currently authenticated.
	 * 
	 * @return {@code true} if no user is currently authenticated, {@code false} otherwise.
	 */
	public boolean isAnonymous() {
		
		if (authentication == null) {
			authentication = new Authentication();
			authentication.setAuthenticationToken(getAuthenticationToken());
		}
		
		final boolean anonymous = ClientUtils.isBlank(authentication.getAuthenticationToken())
			&& !authentication.isAuthorized();

		if (anonymous) {
			// Just to be sure that all cookies are properly cleared.
			clearAuthentication();
		}

		return anonymous;
	}

	/**
	 * Returns a date corresponding to present time plus a full day (24h).
	 * 
	 * @return a date corresponding to present time plus a full day (24h).
	 */
	private static Date oneDayLater() {
		return new Date(new Date().getTime() + 1000 * 60 * 60 * 24);
	}

}
