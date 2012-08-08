package org.sigmah.shared.dto;

import java.util.HashMap;
import java.util.Map;

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
        PROJECT_REPORT,        
        /**
         * INDICATORS
         */
        PROJECT_INDICATOR_LIST,
        /**
         * PROJECT SYNTHESIS 
         */
        PROJECT_SYNTHESIS,
        /**
         * PROJECT SYNTHESIS with Log frame
         */
        PROJECT_SYNTHESIS_LOGFRAME,
        /**
         * PROJECT SYNTHESIS with indicators 
         */
        PROJECT_SYNTHESIS_INDICATORS,
        /**
         * PROJECT SYNTHESIS with logframe and indicators
         */
        PROJECT_SYNTHESIS_LOGFRAME_INDICATORS;

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
       
    /*
     * Maps to specify document type and extension
     */
	private final static Map<ExportFormat, String> contentTypeMap = new HashMap<ExportFormat, String>();
	private final static Map<ExportFormat, String> extensionMap = new HashMap<ExportFormat, String>();
	
	static {
		contentTypeMap.put(ExportFormat.MS_EXCEL, "application/vnd.ms-excel");
		contentTypeMap.put(ExportFormat.OPEN_DOCUMENT_SPREADSHEET, "application/vnd.oasis.opendocument.spreadsheet");
		contentTypeMap.put(ExportFormat.MS_WORD, "application/msword");
		
		extensionMap.put(ExportFormat.MS_EXCEL, ".xls");
		extensionMap.put(ExportFormat.OPEN_DOCUMENT_SPREADSHEET, ".ods");
		extensionMap.put(ExportFormat.MS_WORD, ".rtf");
	}
 
	public static String getContentType(ExportFormat format) {
		return contentTypeMap.get(format);
	}
	
	public static String getExtension(ExportFormat format) {
		return extensionMap.get(format);
	}

	public static enum ExportFormat {
		MS_EXCEL, OPEN_DOCUMENT_SPREADSHEET, MS_WORD;		
		
		  public static ExportFormat valueOfOrNull(String name) {
	            try {
	                return ExportFormat.valueOf(name.toUpperCase());
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
     * Document format such as MS Excel, MS Word, Open document spreadsheet
     */
    public static final String PARAM_EXPORT_FORMAT = "format";

    /**
     * The parameter name to identify the id of the project during an export.
     */
    public static final String PARAM_EXPORT_PROJECT_ID = "id";
   

}
