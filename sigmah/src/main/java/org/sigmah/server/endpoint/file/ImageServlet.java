package org.sigmah.server.endpoint.file;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Servlet responsible providing images.
 * 
 * @author tmi
 * 
 */
@Singleton
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 7665694048776212525L;

    private static final Log log = LogFactory.getLog(ImageServlet.class);

    /**
     * To get the images.
     */
    private final FileStorageProvider fileStorageProvider;

    /**
     * The root directory where images are stored.
     */
    private final String imageRepositoryRoot;

    @Inject
    public ImageServlet(Properties configProperties, FileStorageProvider fileStorageProvider) {
        this.fileStorageProvider = fileStorageProvider;

        // Initializes images repository path.
        String root = configProperties.getProperty(FileModule.REPOSITORY_LOGOS);

        // No prefix.
        if (root == null) {
            root = "";
        }

        imageRepositoryRoot = root;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (log.isDebugEnabled()) {
            log.debug("[doGet] Starts providing image.");
        }

        // Gets the image url.
        final String url = request.getParameter(FileUploadUtils.IMAGE_URL);

        // Checks if the url is correct.
        if (url == null) {

            if (log.isWarnEnabled()) {
                log.warn("[doGet] No image url specified.");
            }

            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("[doGet] Provides image at url '" + url + "'.");
        }

        // Retrieves the image.
        // final URI image = imageRepository.getImage(url);
        // final BufferedInputStream inputStream = new
        // BufferedInputStream(image.toURL().openStream());

        final BufferedInputStream inputStream = new BufferedInputStream(fileStorageProvider.open(imageRepositoryRoot
                + "/" + url));

        try {

            // Writes image content to the HTTP response.
            final ServletOutputStream outputStream = response.getOutputStream();
            IOUtil.copy(inputStream, outputStream);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("[doGet] HTTP response I/O error.");
            throw e;
        }

        if (log.isDebugEnabled()) {
            log.debug("[doGet] Ends providing image.");
        }
    }
}
