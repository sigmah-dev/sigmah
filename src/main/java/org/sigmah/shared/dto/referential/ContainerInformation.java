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
	
    /**
     * Identifier of the container.
     */
	private int id;
    
    /**
     * Code of the container.
     */
	private String name;
    
    /**
     * Name of the container.
     */
	private String fullName;
    
    /**
     * <code>true</code> if the container is a project,
     * <code>false</code> otherwise (i.e. it is an orgunit).
     */
	private boolean project;

    /**
     * Empty constructor for serialization.
     */
	protected ContainerInformation() {
		// Serialization.
	}

    /**
     * Creates a new instance.
     * 
     * @param id
     *          Identifier of the container.
     * @param name
     *          Code of the container.
     * @param fullName
     *          Name of the container.
     * @param project 
     *          <code>true</code> if the container is a project,
     *          <code>false</code> otherwise (i.e. it is an orgunit).
     */
	public ContainerInformation(int id, String name, String fullName, boolean project) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.project = project;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + this.id;
		hash = 17 * hash + (this.project ? 13 : 19);
		return hash;
	}

    /**
     * {@inheritDoc}
     */
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
