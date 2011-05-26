/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.bootstrap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.Cookies;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Serve the Sigmah main page (that handles both the login and the application).
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class SigmahHostController extends HttpServlet {
    public static final String ENDPOINT = "Sigmah/";
    public static final String DEFAULT_LOCALE = "fr";

    private static final String charset = "UTF-8";

    private static final Log LOG = LogFactory.getLog(SigmahHostController.class);

    @Inject
    private Properties properties;

    /**
     * HTML page template
     */
    private String template;

    @Override
	public void init(ServletConfig config) throws ServletException {
    	try {
    					
			template = readAll(getClass().getResourceAsStream("SigmahHostController.html"))
				.replaceAll("<!--Version-->", properties.getProperty("version.number"))
				.replace("<!--ClientDictionaries-->",  readClientDictionaries(config));
			
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.setCharacterEncoding(charset);
        resp.getWriter().write( template.replace("<!--Locale-->", getLocaleTag(req) ) );
    }

    
    private String getLocaleTag(HttpServletRequest req) {
    	return "<meta name='gwt:property' content='locale=" + getLocale(req.getCookies()) + "'>"; 
    }

    private String getLocale(Cookie[] cookies) {
        if(cookies != null) {
            for (final Cookie cookie : cookies) {
                if (Cookies.LOCALE_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return DEFAULT_LOCALE;
    }
    
    private String readAll(InputStream in) throws IOException {
    	StringBuilder sb = new StringBuilder();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    	String line;
    	while( (line=reader.readLine()) != null) {
    		sb.append(line);
    	}
    	return sb.toString();
    }

	private String readClientDictionaries(ServletConfig config) throws IOException {
		String clientDictionaries = readAll(new FileInputStream(
				config.getServletContext().getRealPath("/WEB-INF/sigmah.client.js")));
		return clientDictionaries;
	}

}
