/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared;

/**
 * List of the cookies used by the application.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Cookies {
    /**
     * Authentication token.
     */
    public static final String AUTH_TOKEN_COOKIE = "authToken";

    /**
     * Current language.
     */
    public static final String LOCALE_COOKIE = "locale";

    /**
     * Sigmah default locale.
     */
    public static final String DEFAULT_LOCALE = "fr";

    /**
     * Gets the current user locale.
     * 
     * @return The current user locale.
     */
    public static String getUserLocale() {

        String userLocale = com.google.gwt.user.client.Cookies.getCookie(LOCALE_COOKIE);
        if (userLocale == null) {
            userLocale = DEFAULT_LOCALE;
        }

        return userLocale;
    }

    protected Cookies() {
    }
}
