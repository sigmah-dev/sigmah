package org.sigmah.shared.command;


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
