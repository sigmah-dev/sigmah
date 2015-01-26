package org.sigmah.server.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.sigmah.shared.Cookies;
import org.sigmah.shared.Language;

/**
 * Languages related tests.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LanguageTest {

	/**
	 * Default language.
	 */
	private static final Language DEFAULT_LANGUAGE = Language.EN;

	/**
	 * HTTP request {@code Accept-Language} header name.
	 */
	private static final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";

	/**
	 * Retrieves language from HTTP cookies.
	 */
	@Test
	public void fromCookies() {

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		// Default language.
		Mockito.when(request.getCookies()).thenReturn(null);
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(new Cookie[] { });
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(cookies(null));
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		// Language detected.
		Mockito.when(request.getCookies()).thenReturn(cookies(Language.FR));
		Assert.assertEquals(Language.FR, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(cookies(Language.EN));
		Assert.assertEquals(Language.EN, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(cookies(Language.ES));
		Assert.assertEquals(Language.ES, Languages.getLanguage(request));
	}

	/**
	 * Retrieves language from HTTP header.
	 */
	@Test
	public void fromHeader() {

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		// Default language.
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("");
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn(null);
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		// Language detected.
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("fr,fr-fr;q=0.8,en;q=0.6,es;q=0.4,en-us;q=0.2");
		Assert.assertEquals(Language.FR, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("fr-fr,fr;q=0.8,en;q=0.6,es;q=0.4,en-us;q=0.2");
		Assert.assertEquals(Language.FR, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("en-us,en;q=0.8,fr-fr;q=0.6,fr;q=0.4,es;q=0.2");
		Assert.assertEquals(Language.EN, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("en,en-us;q=0.8,fr-fr;q=0.6,fr;q=0.4,es;q=0.2");
		Assert.assertEquals(Language.EN, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("es;en,en-us;q=0.8,fr-fr;q=0.6,fr;q=0.4,q=0.2");
		Assert.assertEquals(Language.ES, Languages.getLanguage(request));

		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("q=0.8;es;en,en-us;fr-fr;q=0.6,fr;q=0.4,q=0.2");
		Assert.assertEquals(Language.ES, Languages.getLanguage(request));
	}

	/**
	 * Retrieves language from HTTP cookies or header.
	 */
	@Test
	public void fromBoth() {

		final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		// Default language.
		Mockito.when(request.getCookies()).thenReturn(null);
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn(null);
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(null);
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("");
		Assert.assertEquals(DEFAULT_LANGUAGE, Languages.getLanguage(request));

		// Language detected.
		Mockito.when(request.getCookies()).thenReturn(null);
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("fr-fr,fr;q=0.8,en;q=0.6,es;q=0.4,en-us;q=0.2");
		Assert.assertEquals(Language.FR, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(cookies(Language.FR));
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("en;fr-fr,fr;q=0.8;q=0.6,es;q=0.4,en-us;q=0.2");
		Assert.assertEquals(Language.FR, Languages.getLanguage(request));

		Mockito.when(request.getCookies()).thenReturn(cookies(null));
		Mockito.when(request.getHeader(ACCEPT_LANGUAGE_HEADER)).thenReturn("es;fr-fr,fr;q=0.8;q=0.6,es;q=0.4,en-us;q=0.2");
		Assert.assertEquals(Language.ES, Languages.getLanguage(request));
	}

	/**
	 * Builds a {@link Cookie} array with the given {@code language} value.
	 * 
	 * @param language
	 *          The {@link Language} instance, may be {@code null}.
	 * @return A {@link Cookie} array containing the {@link Cookies#LANGUAGE_COOKIE} key referencing the given
	 *         {@code language} value.
	 */
	private static Cookie[] cookies(final Language language) {
		return new Cookie[] { new Cookie(Cookies.LANGUAGE_COOKIE, language != null ? language.getLocale() : null)
		};
	}

}
