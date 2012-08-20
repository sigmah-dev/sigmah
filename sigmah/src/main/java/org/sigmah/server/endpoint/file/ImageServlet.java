package org.sigmah.server.endpoint.file;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.Cookies;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.sun.mail.iap.Response;

/**
 * Servlet responsible providing images.
 * 
 * @author tmi
 * @author Aurélien Ponçon
 */
@Singleton
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = 7665694048776212525L;

    private static final Log log = LogFactory.getLog(ImageServlet.class);

    private Injector injector;

    /**
     * To get the images.
     */
    private final FileStorageProvider fileStorageProvider;
    private final Provider<EntityManager> entityManager;

    /**
     * The root directory where images are stored.
     */
    private final String imageRepositoryRoot;

    @Inject
    public ImageServlet(Properties configProperties, FileStorageProvider fileStorageProvider, Provider<EntityManager> entityManager, Injector injector) {
        this.fileStorageProvider = fileStorageProvider;
        this.entityManager = entityManager;

        // Initializes images repository path.
        String root = configProperties.getProperty(FileModule.REPOSITORY_LOGOS);

        // No prefix.
        if (root == null) {
            root = "";
        }

        imageRepositoryRoot = root;

        this.injector = injector;
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

        final BufferedInputStream inputStream =
                new BufferedInputStream(fileStorageProvider.open(imageRepositoryRoot + "/" + url));

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("[doGet] Starts downloading image.");
        }

        if (ServletFileUpload.isMultipartContent(request)) {
            Integer organizationId;
            try {
                organizationId = Integer.parseInt(request.getParameter("organization"));
            } catch (NumberFormatException nfe) {
                throw new ServletException("organization is not an integer", nfe);
            }
            if (request.getContentLength() <= FileUploadUtils.MAX_UPLOAD_IMAGE_SIZE) {

                Organization organization = new Organization();
                organization.setId(organizationId);

                final String authToken = Cookies.getCookieValue(Cookies.AUTH_TOKEN_COOKIE, request);
                if (authToken == null) {
                    log.error("[doGet] You need to be authenticated");
                    throw new ServletException("You need to be authenticated");
                }
                final AuthenticationDAO authenticationDAO = injector.getInstance(AuthenticationDAO.class);
                final Authentication authentication = authenticationDAO.findById(authToken);
                User user = authentication.getUser();
                EntityManager em = entityManager.get();
                user = em.merge(user);
                
                Organization userOrganization = em.merge(user.getOrganization());
                
                if (!organization.equals(userOrganization)) {
                    throw new ServletException("You can't modify an other organization than your own organization");
                }

                final ServletFileUpload upload = new ServletFileUpload();

                try {

                    // Reads HTTP request elements.
                    final FileItemIterator iterator = upload.getItemIterator(request);

                    boolean fileUploaded = false;

                    while (iterator.hasNext() && !fileUploaded) {

                        // Gets the next HTTP request element.
                        final FileItemStream item = iterator.next();

                        // Field name.
                        final String name = item.getFieldName();

                        // Field value.
                        final InputStream stream = item.openStream();

                        if (!item.isFormField()) {
                            // Else it's the uploaded file content.
                            if (log.isDebugEnabled()) {
                                log.debug("[doPost] Reads image content from the field ; name: " + name + ".");
                            }

                            final LogoManager logoManager = injector.getInstance(LogoManager.class);
                            logoManager.updateLogo(stream, imageRepositoryRoot + "/" + userOrganization.getLogo());

                            stream.close();

                            fileUploaded = true;
                            response.setStatus(Response.OK);
                            response.getWriter().write("ok");
                        }
                    }
                }
                // HTTP request I/O error.
                catch (FileUploadException e) {
                    log.error("[doPost] Error while reading the HTTP request elements.", e);
                    throw new ServletException("Error while reading the HTTP request elements.", e);
                }

            } else {
                if (log.isDebugEnabled()) {
                    log.debug("[doPost] File too big to be uploaded (size: " + request.getContentLength() + ").");
                }
                throw new ServletException("File too big to be uploaded (size: " + request.getContentLength() + ").");
            }

        }
    }
}
