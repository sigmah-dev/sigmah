/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;
import org.sigmah.server.util.logging.Trace;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigModule extends AbstractModule {
    private static Logger logger = Logger.getLogger(ConfigModule.class);

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Trace
    public Properties provideConfigProperties(ServletContext context) {

        final Properties properties = new Properties();

        tryToLoadFrom(properties, webInfDirectory(context));
        tryToLoadFrom(properties, tomcatConfigurationDirectory());
        tryToLoadFrom(properties, classesDirectory());

        // Debug
        if (logger.isDebugEnabled()) {
            logger.debug("Properties 'repository.files' [sigmah.properties] = "
                    + properties.getProperty("repository.files"));
            logger.debug("Properties 'repository.images' [sigmah.properties] = "
                    + properties.getProperty("repository.images"));
        }

        return properties;
    }

    private boolean tryToLoadFrom(Properties properties, File file) {
        try {
            logger.info("Trying to read properties from: " + file.getAbsolutePath());
            if (file.exists()) {
                logger.info("Reading properties from " + file.getAbsolutePath());
                properties.load(new FileInputStream(file));
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    private boolean tryToLoadFrom(Properties properties, InputStream input) {
        try {
            logger.info("Trying to read properties from input stream.");
            properties.load(input);
            logger.info("Reading properties from input stream.");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private File webInfDirectory(ServletContext context) {
        return new File(context.getRealPath("WEB-INF") + File.separator + "sigmah.properties");
    }

    private File tomcatConfigurationDirectory() {
        return new File(System.getenv("CATALINA_BASE") + File.separator + "conf" + File.separator + "sigmah.properties");
    }

    private InputStream classesDirectory() {
        return this.getClass().getClassLoader().getResourceAsStream("/sigmah.properties");
    }
}
