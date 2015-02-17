package org.sigmah.shared.dispatch;

import java.io.Serializable;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.shared.dto.referential.Container;
import org.sigmah.shared.dto.referential.ContainerType;

/**
 * Sub type of {@link FunctionException}.
 * Contains informations about why a conflict happened and what objects it concern.
 * 
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class UpdateConflictException extends FunctionalException implements Serializable {
	
	private int containerId;
	private String containerName;
	private String containerFullName;
	private boolean file;

	protected UpdateConflictException() {
		// Serialization.
	}
	
	public UpdateConflictException(Project project, String... parameters) {
		this(ContainerType.PROJECT, project.getId(), project.getName(), project.getFullName(), false, parameters);
	}

	public UpdateConflictException(OrgUnit orgUnit, String... parameters) {
		this(ContainerType.ORG_UNIT, orgUnit.getId(), orgUnit.getName(), orgUnit.getFullName(), false, parameters);
	}

	public UpdateConflictException(Project project, boolean file, String... parameters) {
		this(ContainerType.PROJECT, project.getId(), project.getName(), project.getFullName(), file, parameters);
	}

	public UpdateConflictException(OrgUnit orgUnit, boolean file, String... parameters) {
		this(ContainerType.ORG_UNIT, orgUnit.getId(), orgUnit.getName(), orgUnit.getFullName(), file, parameters);
	}

	public UpdateConflictException(ContainerType containerType, int containerId, String containerName, String containerFullName, boolean file, String... parameters) {
		super(ErrorCode.UPDATE_CONFLICT, parameters);
//		this.containerType = containerType;
		this.containerId = containerId;
		this.containerName = containerName;
		this.containerFullName = containerFullName;
		this.file = file;
	}

	public Container getContainer() {
		return new Container(containerId, containerName, containerFullName, ContainerType.PROJECT);
	}

	public boolean isFile() {
		return file;
	}
	
}
