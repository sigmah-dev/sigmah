package org.sigmah.server.file.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a folder element.
 * 
 * @author Aurélien Ponçon
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FolderElement extends RepositoryElement {

	private final Map<String, RepositoryElement> children;

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
	 *          the id which identify the element
	 * @return the element which have the id given
	 */
	public RepositoryElement getById(String id) {
		return children.get(id);
	}

}
