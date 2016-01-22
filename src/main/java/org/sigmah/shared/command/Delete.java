package org.sigmah.shared.command;

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


import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * <p>
 * Deletes a database entity.
 * </p>
 * <p>
 * Note: the specified entity must be <code>Deletable</code>
 * </p>
 * <p>
 * Returns <code>VoidResult</code>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.server.domain.util.Deleteable
 */
public class Delete extends AbstractCommand<VoidResult> {

	private String entityName;
	private Integer id;
	
	// NOTE: projectId and elementId are required by the offline mode to find
	// the value to delete.
	private Integer projectId;
	private Integer elementId;

	private ProjectModelStatus projectModelStatus;

	protected Delete() {
		// Serialization.
	}

	public Delete(EntityDTO<?> entity) {
		this.entityName = entity.getEntityName();
		this.id = (Integer) entity.getId();
	}
	
	public Delete(EntityDTO<?> entity, int projectId, int elementId) {
		this(entity);
		this.projectId = projectId;
		this.elementId = elementId;
	}

	public Delete(String entityName, Integer id) {
		this.entityName = entityName;
		this.id = id;
	}

	public Delete(String entityName, Integer id, Integer parentId) {
		this.entityName = entityName;
		this.id = id;
		this.elementId = parentId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public Integer getElementId() {
		return elementId;
	}
	
	public Integer getParentId() {
		return elementId;
	}
	
	/**
	 * @return the projectModelStatus
	 */
	public ProjectModelStatus getProjectModelStatus() {
		return projectModelStatus;
	}

	/**
	 * @param projectModelStatus
	 *          the projectModelStatus to set
	 */
	public void setProjectModelStatus(ProjectModelStatus projectModelStatus) {
		this.projectModelStatus = projectModelStatus;
	}

}
