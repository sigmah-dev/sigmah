package org.sigmah.server.endpoint.export.sigmah;

import java.io.OutputStream;
import java.util.Map;

import javax.persistence.EntityManager;

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

    /**
     * Builds an new exporter.
     * 
     * @param parametersMap
     *            The export parameters.
     */
    public Exporter(final EntityManager em, final Map<String, Object> parametersMap) {
        this.em = em;
        this.parametersMap = parametersMap;
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

    /**
     * Gets the exported format.
     * 
     * @return the exported format.
     */
    public abstract ExportFormat getFormat();

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
