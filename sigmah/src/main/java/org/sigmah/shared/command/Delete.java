/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.client.page.dashboard.CreateProjectWindow.Mode;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.EntityDTO;


/**
 * Deletes a database entity.
 *
 * Note: the specified entity must be <code>Deletable</code>
 *
 * Returns <code>VoidResult</code>
 *
 * @see org.sigmah.shared.domain.Deleteable
 *
 */
public class Delete implements Command<VoidResult> {
	

	private String entityName;
	private int id;
	
	private Mode mode;
	private ProjectModelStatus projectModelStatus;

    protected Delete() {}
	
	public Delete(EntityDTO entity) {
		this.entityName = entity.getEntityName();
		this.id =  entity.getId();
	}

    public Delete(String entityName, int id) {
        this.entityName = entityName;
        this.id = id;
    }

    public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	/**
	 * @return the projectModelStatus
	 */
	public ProjectModelStatus getProjectModelStatus() {
		return projectModelStatus;
	}

	/**
	 * @param projectModelStatus the projectModelStatus to set
	 */
	public void setProjectModelStatus(ProjectModelStatus projectModelStatus) {
		this.projectModelStatus = projectModelStatus;
	}
	
}
