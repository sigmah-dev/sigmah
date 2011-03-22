/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server;

import org.sigmah.server.domain.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cookies extends org.sigmah.shared.Cookies {
    
    private class For {
        private static final int THIRTY_DAYS = 30 * 24 * 60 * 60;
        private static final int THIS_SESSION = -1;
    }


    public static void addAuthCookie(HttpServletResponse response, Authentication auth, boolean remember) {
        Cookie authCookie = new Cookie(Cookies.AUTH_TOKEN_COOKIE, auth.getId());
        authCookie.setMaxAge(remember ? For.THIRTY_DAYS : For.THIS_SESSION);
        authCookie.setPath("/");
        response.addCookie(authCookie);
    }

    public static String getCookieValue(String cookieName, HttpServletRequest req) {
        final Cookie[] cookies = req.getCookies();

        if(cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
