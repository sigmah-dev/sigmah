package org.sigmah.server.endpoint.file;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.sigmah.shared.domain.reminder.MonitoredPoint;
import org.sigmah.shared.dto.value.FileUploadUtils;

/**
 * Manages files (upload and download).
 * 
 * @author tmi
 * 
 */
public interface FileManager {

    /**
     * Saves and stores a new file version. If the file doesn't exist already,
     * creates it.
     * 
     * @param properties
     *            The properties map of the uploaded file (see
     *            {@link FileUploadUtils}).
     * @param content
     *            The uploaded file content.
     * @return The id of the just saved file.
     * @throws IOException
     */
    public String save(Map<String, String> properties, byte[] content) throws IOException;

    /**
     * Returns the file for the given id and version number.
     * 
     * @param idString
     *            The file entity id.
     * @param versionString
     *            The desired version number.
     * @return The corresponding file.
     */
    public DownloadableFile getFile(String idString, String versionString);


    /**
     * Creates a monitored point.
     * 
     * @param projetId
     *            The project at which the point belongs.
     * @param label
     *            The point's label.
     * @param expectedDate
     *            The point's expected date.
     * @param fileId
     *            The point's file id.
     * @return The just created point.
     */
    public MonitoredPoint createMonitoredPoint(Integer projetId, String label, Date expectedDate, Integer fileId);

    /**
     * Utility class to represents a downloaded file.
     * 
     * @author tmi
     * 
     */
    public static class DownloadableFile {

        /**
         * The file's name (version-independent).
         */
        private final String name;

        /**
         * The file's storage id (for the expected version).
         * Use {@link FileStorageProvider} to open an InputStream
         */
        private final String storageId;

        public DownloadableFile(String name, String storageId) {
            super();
            this.name = name;
            this.storageId = storageId;
        }

        public String getName() {
            return name;
        }

		public String getStorageId() {
			return storageId;
		}

        
    }
}
