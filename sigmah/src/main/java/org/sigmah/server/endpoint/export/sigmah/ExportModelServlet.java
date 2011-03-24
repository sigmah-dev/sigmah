/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.export.sigmah;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
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
import org.sigmah.server.auth.SigmahAuthDictionaryServlet;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.export.sigmah.handler.ModelHandler;
import org.sigmah.server.endpoint.export.sigmah.handler.OrgUnitModelHandler;
import org.sigmah.server.endpoint.export.sigmah.handler.ProjectModelHandler;
import org.sigmah.server.endpoint.export.sigmah.handler.ProjectReportModelHandler;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.profile.ProfileUtils;

/**
 * Export and import models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ExportModelServlet extends HttpServlet {

	private static final long serialVersionUID = -7530695817850669821L;
	private final static Log LOG = LogFactory.getLog(ExportModelServlet.class);
    private final static long MAXIMUM_FILE_SIZE = 2097152; // 2 Mo

    private final Injector injector;
    private final Map<String, ModelHandler> handlers;

    @Inject
    public ExportModelServlet(Injector injector) {
        this.injector = injector;

        final HashMap<String, ModelHandler> map = new HashMap<String, ModelHandler>();
        map.put("project-model", new ProjectModelHandler());
        map.put("project-report-model", new ProjectReportModelHandler());
        map.put("org-unit-model", new OrgUnitModelHandler());

        this.handlers = map;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final Authentication authentication = retrieveAuthentication(req);
        boolean hasPermission = false;
        
        if(authentication != null) {
            final User user = authentication.getUser();
            ProfileDTO profile = SigmahAuthDictionaryServlet.aggregateProfiles(user, null, injector);
            hasPermission = ProfileUtils.isGranted(profile, GlobalPermissionEnum.VIEW_ADMIN);
        }

        if(hasPermission) {
            // Export the model
            
            final String type = (String) req.getParameter("type");
            final String identifier = (String) req.getParameter("id");

            final ModelHandler handler = handlers.get(type);
            if(handler != null) {
                try {
                    handler.exportModel(resp.getOutputStream(), identifier,
                            injector.getInstance(EntityManager.class));

                    resp.setContentType("application/octet-stream");
                    resp.addHeader("Content-Disposition", "attachment; filename="+type+".dat");

                } catch (ExportException ex) {
                    LOG.error("Model export error, type: "+type+", id: "+identifier+'.', ex);
                    resp.sendError(500);
                }
                
            } else {
                LOG.warn("The asked model type ("+type+") doesn't have any handler registered.");
                resp.sendError(404);
            }

        } else {
            LOG.warn("Unauthorized access to the export service from user "+authentication);
            resp.sendError(401);
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(ServletFileUpload.isMultipartContent(req)) {

            final Authentication authentication = retrieveAuthentication(req);
            boolean hasPermission = false;

            if(authentication != null) {
                final User user = authentication.getUser();
                ProfileDTO profile = SigmahAuthDictionaryServlet.aggregateProfiles(user, null, injector);
                hasPermission = ProfileUtils.isGranted(profile, GlobalPermissionEnum.VIEW_ADMIN);
            }

            if(hasPermission) {

                final ServletFileUpload fileUpload = new ServletFileUpload();

                final HashMap<String, String> properties = new HashMap<String, String>();
                byte[] data = null;

                try {
                    final FileItemIterator iterator = fileUpload.getItemIterator(req);

                    // Iterating on the fields sent into the request
                    while(iterator.hasNext()) {

                        final FileItemStream item = iterator.next();
                        final String name = item.getFieldName();

                        final InputStream stream = item.openStream();

                        if(item.isFormField()) {
                            final String value = Streams.asString(stream);
                            LOG.debug("field '"+name+"' = '"+value+'\'');

                            // The current field is a property
                            properties.put(name, value);

                        } else {
                            // The current field is a file
                            LOG.debug("field '"+name+"' (FILE)");

                            final ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
                            long dataSize = 0L;

                            int b = stream.read();

                            while(b != -1 && dataSize < MAXIMUM_FILE_SIZE) {
                                serializedData.write(b);

                                dataSize++;
                                b = stream.read();
                            }

                            stream.close();

                            data = serializedData.toByteArray();
                        }
                    }

                } catch (FileUploadException ex) {
                    LOG.warn("Error while receiving a serialized model.", ex);
                }

                if(data != null) {
                    // A file has been received

                    final String type = properties.get("type");
                    final ModelHandler handler = handlers.get(type);

                    if(handler != null) {
                    	
                    	if(handler instanceof ProjectModelHandler){
                    		final String projectModelTypeAsString = properties.get("project-model-type");
                    		try {
                    			final ProjectModelType projectModelType = ProjectModelType.valueOf(projectModelTypeAsString);
                    			((ProjectModelHandler)handler).setProjectModelType(projectModelType);
                    		} catch (IllegalArgumentException e) {
								LOG.debug("Bad value for project model type: "+projectModelTypeAsString, e);
							}
                    	}

                        final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

                        try {
                            handler.importModel(inputStream, injector.getInstance(EntityManager.class), authentication);

                        } catch (ExportException ex) {
                            LOG.error("Model import error, type: "+type, ex);
                            resp.sendError(500);
                        }

                    } else {
                        LOG.warn("The asked model type ("+type+") doesn't have any handler registered.");
                        resp.sendError(501);
                    }
                } else {
                    LOG.warn("No file has been received.");
                    resp.sendError(400);
                }
            } else {
                LOG.warn("Unauthorized access to the import service from user "+authentication);
            resp.sendError(401);
            }

        } else {
            LOG.warn("The request doesn't have the correct enctype.");
            resp.sendError(400);
        }
    }


    private Authentication retrieveAuthentication(HttpServletRequest req) {

        final String authToken = Cookies.getCookieValue(Cookies.AUTH_TOKEN_COOKIE, req);

        if(authToken == null)
            return null;

        final AuthenticationDAO authenticationDAO = injector.getInstance(AuthenticationDAO.class);
        final Authentication authentication = authenticationDAO.findById(authToken);

        return authentication;
    }
}
