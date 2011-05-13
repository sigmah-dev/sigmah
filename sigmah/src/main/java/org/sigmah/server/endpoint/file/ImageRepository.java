package org.sigmah.server.endpoint.file;

import java.util.Properties;

import com.google.inject.Inject;

public class ImageRepository {

    /**
     * Directory's path name where the images are stored.
     */
    private final String IMAGES_REPOSITORY;

    /**
     * The property's name expected in the properties file to set the images
     * repository path name.
     */
    // @VisibleForTesting
    static final String IMAGES_REPOSITORY_NAME = "repository.images";

       
	@Inject
	public ImageRepository(Properties configProperties) {

        // Initializes images repository path.
        IMAGES_REPOSITORY = configProperties.getProperty(IMAGES_REPOSITORY_NAME);

        if (IMAGES_REPOSITORY == null) {
            throw new IllegalStateException("Missing reqquired property '" + IMAGES_REPOSITORY_NAME
                    + "' in the upload properties file.");
        }
	}
	
    /**
     * Retrieves an image with the given.
     * 
     * @param name
     *            The image's name.
     * @return The image as a file.
     */
    public java.io.File getImage(String name) {

        // Images repository.
        final java.io.File repository = new java.io.File(IMAGES_REPOSITORY);

        // Image file.
        final java.io.File imageFile = new java.io.File(repository, name);

        return imageFile;
    }
	
}
