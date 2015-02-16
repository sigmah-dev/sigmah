package org.sigmah.shared.dispatch;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.shared.dto.referential.Container;

/**
 * Sub type of {@link FunctionException}.
 * Contains informations about why a conflict happened and what objects it concern.
 * 
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class UpdateConflictException extends FunctionalException {
	
	private final Container container;
	private final boolean file;

	public UpdateConflictException(Project project, String... parameters) {
		this(Container.Type.PROJECT, project.getId(), project.getName(), project.getFullName(), false, parameters);
	}

	public UpdateConflictException(OrgUnit orgUnit, String... parameters) {
		this(Container.Type.PROJECT, orgUnit.getId(), orgUnit.getName(), orgUnit.getFullName(), false, parameters);
	}

	public UpdateConflictException(Project project, boolean file, String... parameters) {
		this(Container.Type.PROJECT, project.getId(), project.getName(), project.getFullName(), file, parameters);
	}

	public UpdateConflictException(OrgUnit orgUnit, boolean file, String... parameters) {
		this(Container.Type.PROJECT, orgUnit.getId(), orgUnit.getName(), orgUnit.getFullName(), file, parameters);
	}

	public UpdateConflictException(Container.Type containerType, int containerId, String containerName, String containerFullName, boolean file, String... parameters) {
		super(ErrorCode.UPDATE_CONFLICT, parameters);
		this.container = new Container(containerId, containerName, containerFullName, containerType);
		this.file = file;
	}

	public Container getContainer() {
		return container;
	}

	public boolean isFile() {
		return file;
	}
	
}
