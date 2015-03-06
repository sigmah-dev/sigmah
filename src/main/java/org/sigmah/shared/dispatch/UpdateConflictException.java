package org.sigmah.shared.dispatch;

import org.sigmah.shared.dto.referential.ContainerInformation;

/**
 * Sub type of {@link FunctionException}.
 * Contains informations about why a conflict happened and what objects it concern.
 * 
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class UpdateConflictException extends FunctionalException {
	
	/**
	 * Information about the modified project/org unit.
	 */
	private ContainerInformation container;
	
	/**
	 * <code>true</code> if this conflict concern a file operation.
	 */
	private boolean file;
	
	protected UpdateConflictException() {
		// Serialization.
	}
	
	public UpdateConflictException(ContainerInformation container, String... parameters) {
		this(container, false, parameters);
	}

	public UpdateConflictException(ContainerInformation container, boolean file, String... parameters) {
		super(ErrorCode.UPDATE_CONFLICT, parameters);
		this.container = container;
		this.file = file;
		this.parameters = parameters;
	}

	public ContainerInformation getContainer() {
		return container;
	}

	public boolean isFile() {
		return file;
	}

	public String[] getParameters() {
		return parameters;
	}
	
}
