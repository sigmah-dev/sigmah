package org.sigmah.server.endpoint.management;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.Cookies;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.file.FileManager;
import org.sigmah.server.endpoint.file.FileStorageProvider;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Servlet for upload logo from the organization management pages
 * 
 * @author Aurélien Ponçon
 *
 */
@Singleton
public class ManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 7500508689612185678L;
    private static final Log log = LogFactory.getLog(ManagementServlet.class);
    
    public static final int LOGO_WIDTH = 126;
    public static final int LOGO_HEIGHT = 56;

    private Injector injector;
    private final FileManager fileManager;
    private final FileStorageProvider fileStorageProvider;
    
    @Inject
    public ManagementServlet(Injector injector) {
        super();
        this.injector = injector;
        fileManager = injector.getInstance(FileManager.class);
        fileStorageProvider = injector.getInstance(FileStorageProvider.class);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {       
        
        final String authToken = Cookies.getCookieValue(Cookies.AUTH_TOKEN_COOKIE, request);
        if(authToken == null) {
            log.error("[doGet] You need to be authenticated");
            throw new ServletException("You need to be authenticated");
        }
        final AuthenticationDAO authenticationDAO = injector.getInstance(AuthenticationDAO.class);
        final Authentication authentication = authenticationDAO.findById(authToken);
        User user = authentication.getUser();
        
        response.setContentType("text/plain");
        if (ServletFileUpload.isMultipartContent(request)) {
            if (request.getContentLength() <= FileUploadUtils.MAX_UPLOAD_IMAGE_SIZE) {
                final ServletFileUpload upload = new ServletFileUpload();

                // Map to store the fields of the HTTP form (name -> value).
                final HashMap<String, String> properties = new HashMap<String, String>();

                // Uploaded file content.
                byte[] data = null;

                try {

                    // Reads HTTP request elements.
                    final FileItemIterator iterator = upload.getItemIterator(request);

                    while (iterator.hasNext()) {

                        // Gets the next HTTP request element.
                        final FileItemStream item = iterator.next();

                        // Field name.
                        final String name = item.getFieldName();

                        // Field value.
                        final InputStream stream = item.openStream();

                        if (item.isFormField()) {

                            // If the field belongs to the HTTP form, stores
                            // it in the map.
                            final String value = Streams.asString(stream);
                            properties.put(name, value);

                            if (log.isDebugEnabled()) {
                                log.debug("[doPost] Reads form field data ; name: " + name + "; value: " + value
                                        + ".");
                            }
                        } else {

                            // Else it's the uploaded file content.
                            if (log.isDebugEnabled()) {
                                log.debug("[doPost] Reads file content from the field ; name: " + name + ".");
                            }
                            
                            BufferedImage image = ImageIO.read(stream);
                            
                            if(image.getWidth() != LOGO_WIDTH || image.getHeight() != LOGO_HEIGHT) {
                                BufferedImage resizedImage = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                                Graphics2D g = resizedImage.createGraphics();
                                g.drawImage(image, 0, 0, LOGO_WIDTH, LOGO_HEIGHT, null);
                                g.dispose();
                                g.setComposite(AlphaComposite.Src);
                                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
                                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                                image = resizedImage;
                            }
                                                        
                            OutputStream outputStream = new BufferedOutputStream(fileStorageProvider.create(user.getOrganization().getLogo()));
                            ImageIO.write(image, "png", outputStream);
                            outputStream.close();
                            stream.close();
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

                // HTTP response.
                final StringBuilder responseBuilder = new StringBuilder();

                responseBuilder.append(FileUploadUtils.TAG_START_CODE);
                responseBuilder.append(FileUploadUtils.TOO_BIG_DOC_ERROR_CODE);
                responseBuilder.append(FileUploadUtils.TAG_END_CODE);

                try {
                    response.getWriter().write(responseBuilder.toString());
                } catch (IOException e) {
                    log.error("[doPost] HTTP response I/O error.");
                    throw e;
                }
            }
        } else {
            throw new ServletException("The mime type of the request is not MultipartContent");
        }
    }

}
