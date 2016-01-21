package org.sigmah.shared.dto.referential;

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
