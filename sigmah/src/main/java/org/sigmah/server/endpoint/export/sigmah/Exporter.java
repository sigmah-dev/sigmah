/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah;

import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.server.Cookies;
import org.sigmah.server.Translator;
import org.sigmah.server.UIConstantsTranslator;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.gwtrpc.CommandServlet;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.RemoteCommandService;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.export.GlobalExportSettings;
import org.sigmah.shared.dto.ExportUtils;

import com.google.inject.Injector;

/**
 * Represents an exporter.
 * 
 * @author tmi
 */
public abstract class Exporter {

    /**
     * The export parameters.
     */
    protected final Map<String, Object> parametersMap;

    /**
     * The injector.
     */
    protected final Injector injector;
           
    /**
     * Export format
     */
    protected ExportUtils.ExportFormat exportFormat;
    
    /**
     * Locale to be used to load a proper i18n properties
     */     
    
    /**
     * Server side command service link{CommandServlet}
     */
    private RemoteCommandService serverSideCommandService=null;	
    
    /**
     * String id of authtoken
     */
    private final String authToken;
    
    /**
     *  User's locale
     */
    protected final Locale locale;
    
    /**
     * Server side localization interface
     */
    private final Translator translator;
       
    
    /**
     * Builds an new exporter.
     * 
     * @param parametersMap
     *            The export parameters.
     */
    @SuppressWarnings("unchecked")
	public Exporter(final Injector injector, final HttpServletRequest req) throws Throwable {
        this.injector = injector;
        this.parametersMap = (Map<String, Object>) req.getParameterMap();
        
        // set up user's locale
        String localeString=Cookies.getCookieValue(Cookies.LOCALE_COOKIE, req);
        if(localeString==null){
        	localeString=Cookies.DEFAULT_LOCALE;
        }
        this.locale=new Locale(localeString);        
        this.translator = new UIConstantsTranslator(new Locale(""));
                        
        // get auth token from cookie
         authToken=Cookies.getCookieValue(Cookies.AUTH_TOKEN_COOKIE, req);
        if(authToken==null)
        	throw new ServletException("Auth token obtained from request cookie is null ");
        
        //Set the export format
        final String formatString = req.getParameter(ExportUtils.PARAM_EXPORT_FORMAT);
        if(formatString!=null){
        	this.exportFormat = ExportUtils.ExportFormat.valueOfOrNull(formatString);
        }else{
 			final AuthenticationDAO authDAO = injector.getInstance(AuthenticationDAO.class);
			final Authentication auth = authDAO.findById(authToken);
			final Integer organizationId = auth.getUser().getOrganization().getId();
			
			final GlobalExportDAO exportDao = injector.getInstance(GlobalExportDAO.class);
			final GlobalExportSettings exportSettings = exportDao
					.getGlobalExportSettingsByOrganization(organizationId);
			
			this.exportFormat = exportSettings.getDefaultOrganizationExportFormat();
        }
    }

    /**
     * Retrieves a parameter with the given name. If the parameter doesn't
     * exist, an exception is thrown.
     * 
     * @param name
     *            The parameter name.
     * @return The parameter value.
     * @throws ExportException
     *             If the parameter doesn't exists.
     */
    protected String requireParameter(String name) throws ExportException {
        final String[] param = (String[]) parametersMap.get(name);
        if (param == null) {
            throw new ExportException("The parameter '" + ExportUtils.PARAM_EXPORT_PROJECT_ID + "' 'id missing.");
        }
        return param[0];
    }

    /**
     * Gets the exported file name.
     * 
     * @return The exported file name.
     */
    public abstract String getFileName();

     
    /*
     * Returns document's MIME type
     */
    public String getContentType(){
    	return ExportUtils.getContentType(exportFormat);
    }
    
    /*
     * Returns file extension
     */
    public String getExtention(){
    	return ExportUtils.getExtension(exportFormat);
    }
    
    /*
     * Returns localized version of a key
     */
    public String localize(String key){
    	String localized = translator.translate(key, locale);
    	if(localized==null)
    		localized = translator.translate(key, null);
    	return localized;
    }
    
	@SuppressWarnings("rawtypes")
	public CommandResult executeCommands(final List<Command> commands)
			throws Throwable {
		if (serverSideCommandService == null) {
			serverSideCommandService = injector.getInstance(CommandServlet.class);
		}
		List<CommandResult> results = serverSideCommandService.execute(authToken, commands);
		CommandResult result = results.get(0);
		if (result instanceof Throwable) {
			throw (Throwable) result;
		}
		return result;
	}

    /**
     * Performs the export into the output stream.
     * 
     * @param output
     *            The output stream.
     * @throws ExportException
     *             If an error occurs during the export.
     */
    public abstract void export(OutputStream output) throws ExportException;
}
