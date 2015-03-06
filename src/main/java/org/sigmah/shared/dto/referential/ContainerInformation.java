package org.sigmah.shared.dto.referential;

import java.io.Serializable;

/**
 * Informations about a container of flexible elements.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ContainerInformation implements Serializable {
	
	private int id;
	private String name;
	private String fullName;
	private boolean project;

	protected ContainerInformation() {
		// Serialization.
	}

	public ContainerInformation(int id, String name, String fullName, boolean project) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.project = project;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + this.id;
		hash = 17 * hash + (this.project ? 13 : 19);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ContainerInformation other = (ContainerInformation) obj;
		if (this.id != other.id) {
			return false;
		}
		return this.project == other.project;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public boolean isProject() {
		return project;
	}

	public void setProject(boolean project) {
		this.project = project;
	}
	
}
