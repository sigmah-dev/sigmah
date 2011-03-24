/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.bootstrap;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.Cookies;

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

    private final HashMap<String, byte[]> pageMap = new HashMap<String, byte[]>();

    @Inject
    private Properties properties;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final String locale = getLocale(req.getCookies());
        byte[] hostPageData = pageMap.get(locale);
        
        if(hostPageData == null) {
            hostPageData = readHostPage(locale);
            pageMap.put(locale, hostPageData);
        }

        final OutputStream outputStream = resp.getOutputStream();

        // Headers
        resp.setContentType("text/html");
        resp.setCharacterEncoding(charset);

        // Writing to the output by packets of 1 KB
        int index = 0;
        int remaining = hostPageData.length;
        while(remaining > 0) {
            int length = remaining > 1024 ? 1024 : remaining;
            
            outputStream.write(hostPageData, index, length);

            index += length;
            remaining -= length;
        }
        
        outputStream.close();
        
    }

    private byte[] readHostPage(final String locale) throws IOException {
        byte[] data = null;

        try {
            // Opening the host page
            final URI hostPageURI = SigmahHostController.class.getResource("SigmahHostController.html").toURI();
            final File hostPageFile = new File(hostPageURI);

            final BufferedReader inputStream = new BufferedReader(new FileReader(hostPageFile));

            // Preparing dynamic contents
            final String localeMetaTag = "<meta name='gwt:property' content='locale="+locale+"'>";
            final String version = properties.getProperty("version.number");

            // Parsing the page
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            final Charset utf8 = Charset.forName(charset);
            String line = inputStream.readLine();
            while(line != null) {
                line = line.replace("<!--Locale-->", localeMetaTag);
                line = line.replace("<!--Version-->", version);

                outputStream.write(line.getBytes(utf8));
                line = inputStream.readLine();
            }

            inputStream.close();
            data = outputStream.toByteArray();

        } catch (URISyntaxException ex) {
            LOG.fatal("Error while opening the sigmah host page.", ex);
        }

        return data;
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
}
