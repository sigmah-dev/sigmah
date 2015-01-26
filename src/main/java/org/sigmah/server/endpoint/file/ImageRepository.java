package org.sigmah.server.endpoint.file;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

@Deprecated
public class ImageRepository {

    /**
     * Directory's path name where the images are stored.
     */
    private URI rootUri;

    /**
     * The property's name expected in the properties file to set the images
     * repository path name.
     */
    // @VisibleForTesting
    static final String IMAGES_REPOSITORY_NAME = "repository.images";

    private static final Logger logger = Logger.getLogger(ImageRepository.class);
    
       
	@Inject
	public ImageRepository(Properties configProperties) {

        // Initializes images repository path.
        String root = configProperties.getProperty(IMAGES_REPOSITORY_NAME);

        if (root == null) {
            throw new IllegalStateException("Missing required property '" + IMAGES_REPOSITORY_NAME
                    + "' in the upload properties file.");
        }
        
        try {
        	this.rootUri = new URI(root);
        	if(!rootUri.isAbsolute()) {
        		this.rootUri = new URI("file:///").resolve(root);
        	}
        } catch(URISyntaxException e) {
        	// automatically convert windows paths
        	File rootFile = new File(root);
        	this.rootUri = rootFile.toURI();
        }
        
        logger.info("Images repository = " + rootUri.toString());
	}
	
    /**
     * Retrieves an image with the given.
     * 
     * @param name
     *            The image's name.
     * @return The image as a file.
     */
    public URI getImage(String name) {

    	return rootUri.resolve(name);    
    }
	
    public URI getRootUri() {
    	return rootUri;
    }
}
