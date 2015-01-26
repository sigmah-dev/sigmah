package org.sigmah.server;

import java.util.Collection;
import java.util.HashSet;


/**
 * Stores a files of properties files.
 * 
 * @author Guerline Jean-Baptiste(gjbaptiste@ideia.fr)
 */

public class PropertiesFilesSet {
	/**
	   * The list of the properties files.
	   */
	  private final HashSet<String> propertiesFiles;

	  public PropertiesFilesSet() {
	    propertiesFiles = new HashSet<String>();
	  }

	  /**
	   * Adds a file path (relative from the classpath).
	   * 
	   * @param file
	   *          The new file path.
	   */
	  public void add(String file) {
	    propertiesFiles.add(file);
	  }

	  /**
	   * Gets the files list.
	   * 
	   * @return The file list.
	   */
	  public Collection<String> getFiles() {
	    return propertiesFiles;
	  }

}
