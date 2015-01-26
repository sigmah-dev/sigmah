package org.sigmah.server.endpoint.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

public class LocalStorageProvider implements FileStorageProvider {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(LocalStorageProvider.class);

    /**
     * Directory's path name where the uploaded files are stored.
     */
    private final String repositoryPath;

    @Inject
    public LocalStorageProvider(Properties configProperties) {
        super();
        repositoryPath = configProperties.getProperty(FileModule.REPOSITORY_NAME);
    }

    @Override
    public OutputStream create(String storageId) throws IOException {
        File contentFile = fileForId(storageId);
        contentFile.getParentFile().mkdirs();

        FileOutputStream fileOutputStream = new FileOutputStream(contentFile);

        if (log.isDebugEnabled()) {
            log.debug("[writeContent] Writes file content to the files repository '" + contentFile + "'.");
        }
        return fileOutputStream;

    }

    @Override
    public InputStream open(String storageId) throws IOException {
        File contentFile = fileForId(storageId);
        return new FileInputStream(contentFile);
    }

    private java.io.File fileForId(String storageId) {
        final java.io.File repository = new java.io.File(repositoryPath);
        final java.io.File contentFile = new java.io.File(repository, storageId);
        return contentFile;
    }
    
    @Override
    public Boolean delete(String storageId) {
    	File contentFile = fileForId(storageId);
    	return contentFile.delete();
    }

}
