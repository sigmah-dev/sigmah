package org.sigmah.server.endpoint.file;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.reminder.MonitoredPoint;
import org.sigmah.shared.domain.value.File;
import org.sigmah.shared.dto.value.FileUploadUtils;

/**
 * Manages files (upload and download).
 * 
 * @author tmi
 * @author Aurélien Ponçon
 */
public interface FileManager {

    /**
     * Saves and stores a new file version. If the file doesn't exist already, creates it.
     * 
     * @param properties
     *            The properties map of the uploaded file (see {@link FileUploadUtils}).
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
     * Return a repository describing the file repository of an organization
     * 
     * @param orgunit
     *          The OrgUnit on what data is taken
     * @param user
     *          The user which want to have the repository
     * @param allVersions
     *          Indicates if the user wants only the last version of each file or all versions of each file 
     * @return a {@link DownloadableRepository} that contains all versions of all files
     */
    public RepositoryElement getRepository(OrgUnit orgunit, User user, boolean allVersions);

    /**
     * Return if an user has the rights to download the file in parameter
     * 
     * @param fileVersion
     *            The file which is requested
     * @return a boolean that indicates if the user can download the file in parameter
     */
    public boolean isGrantedToDownload(User user, File file);

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
     */
    public static class DownloadableFile {

        /**
         * The file's name (version-independent).
         */
        private String name;

        /**
         * The file's storage id (for the expected version). Use {@link FileStorageProvider} to open an InputStream
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

    /**
     * This abstract class is used for representing a repository It needs an id to retrieve it in the hierarchy and a
     * name for naming it
     * 
     * @author AurÃ©lien PonÃ§on
     */
    public static abstract class RepositoryElement {

        private String id;
        private String name;
        private RepositoryElement parent;

        public RepositoryElement(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public RepositoryElement getParent() {
            return parent;
        }

        public void setParent(RepositoryElement parent) {
            this.parent = parent;
        }
    }

    public static class FolderElement extends RepositoryElement {

        private Map<String, RepositoryElement> children;

        public FolderElement(String id, String name) {
            super(id, name);
            children = new HashMap<String, RepositoryElement>();
        }

        public Collection<RepositoryElement> getChildren() {
            return children.values();
        }

        public boolean containsChild(RepositoryElement re) {
            return children.containsKey(re.getId());
        }

        public void appendChild(RepositoryElement re) {
            if (!this.containsChild(re)) {
                if (re.getParent() != null && re.getParent() instanceof FolderElement) {
                    FolderElement r = (FolderElement) re.getParent();
                    r.removeChild(re);
                }
                children.put(re.getId(), re);
            }
        }

        public void removeChild(RepositoryElement re) {
            children.remove(re.getId());
        }

        /**
         * Find the element which have the id given. The search is made only on the direct children
         * 
         * @param id
         *            the id which identify the element
         * @return the element which have the id given
         */
        public RepositoryElement getById(String id) {
            return children.get(id);
        }
    }

    public static class FileElement extends RepositoryElement {

        /**
         * The file's storage id (for the expected version). Use {@link FileStorageProvider} to open an InputStream
         */
        private final String storageId;

        public FileElement(String id, String name, String storageId) {
            super(id, name);
            this.storageId = storageId;
        }

        public String getStorageId() {
            return storageId;
        }
    }

}
