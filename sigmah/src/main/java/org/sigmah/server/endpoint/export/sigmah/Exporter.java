package org.sigmah.server.endpoint.export.sigmah;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.server.Cookies;
import org.sigmah.server.Translator;
import org.sigmah.server.UIConstantsTranslator;
import org.sigmah.shared.dto.ExportUtils;

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
     * The entity manager.
     */
    protected final EntityManager em;
       
    
    /*
     * Export format
     */
    protected final ExportUtils.ExportFormat exportFormat;
    
    /*
     * Locale to be used to load a proper i18n properties
     */
    private final Locale locale;
    
    /*
     * Server size localization interface
     */
    private final Translator translator;
    
    /**
     * Builds an new exporter.
     * 
     * @param parametersMap
     *            The export parameters.
     */
    @SuppressWarnings("unchecked")
	public Exporter(final EntityManager em, final HttpServletRequest req) throws Throwable {
        this.em = em;
        this.parametersMap = (Map<String, Object>) req.getParameterMap();
        
        String localeString=Cookies.getCookieValue(Cookies.LOCALE_COOKIE, req);
        if(localeString==null){
        	localeString=Cookies.DEFAULT_LOCALE;
        }
        this.locale=new Locale(localeString);        
        this.translator = new UIConstantsTranslator(new Locale(""));
        
        //Set the export format
        final String formatString = req.getParameter(ExportUtils.PARAM_EXPORT_FORMAT);
        final ExportUtils.ExportFormat format = ExportUtils.ExportFormat.valueOfOrNull(formatString);

        if (format == null) {
            throw new ServletException("The export format '" + formatString + "' is unknown.");
        }
        
        this.exportFormat=format;
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
