package org.sigmah.server.file.util;

/**
 * This abstract class is used for representing a repository. It needs an id to retrieve it in the hierarchy and a name
 * for naming it.
 * 
 * @author Aurélien Ponçon
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class RepositoryElement {

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
