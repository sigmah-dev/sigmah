package org.sigmah.server.file.util;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
