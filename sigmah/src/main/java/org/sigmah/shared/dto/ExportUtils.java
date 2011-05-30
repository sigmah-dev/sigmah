package org.sigmah.shared.dto;

/**
 * Constants to manage exports.
 * 
 * @author tmi
 * 
 */
public final class ExportUtils {

    /**
     * Provides only static methods.
     */
    private ExportUtils() {
    }

    /**
     * Defines the different types of entity supported by the export.
     * 
     * @author tmi
     */
    public static enum ExportType {

        /**
         * Log frame.
         */
        PROJECT_LOG_FRAME,
        /**
         * Project report.
         */
        PROJECT_REPORT;


        public static ExportType valueOfOrNull(String name) {
            try {
                return ExportType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    /**
     * The parameter name to identify the entity to export.
     */
    public static final String PARAM_EXPORT_TYPE = "type";

    /**
     * The parameter name to identify the id of the project during an export.
     */
    public static final String PARAM_EXPORT_PROJECT_ID = "id";

    /**
     * The parameter name to identify the labels list during an export.
     */
    public static final String PARAM_EXPORT_LABELS_LIST = "labels";

    /**
     * The separator to merge/split the labels list.
     */
    public static final String PARAM_EXPORT_LABELS_LIST_SEPARATOR = "~";
}
