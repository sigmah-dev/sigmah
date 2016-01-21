package org.sigmah.shared.dispatch;

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
