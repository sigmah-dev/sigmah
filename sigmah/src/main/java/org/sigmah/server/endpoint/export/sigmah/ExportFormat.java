package org.sigmah.server.endpoint.export.sigmah;

/**
 * Defines a exportable format.
 * 
 * @author tmi
 * 
 */
public class ExportFormat {

    /**
     * Microsoft Excel 2003 (XLS).
     */
    public static ExportFormat MSEXCEL = new ExportFormat("application/vnd.ms-excel");

    /**
     * Microsoft Word (RTF).
     */
    public static ExportFormat MSWORD = new ExportFormat("application/msword");

    /**
     * The format content type.
     */
    private final String contentType;

    public ExportFormat(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
