package org.sigmah.shared.command.result;

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

import java.util.List;
import java.util.Map;
import org.sigmah.shared.dto.referential.ContainerInformation;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeResult implements Result {
	
	private Map<ContainerInformation, List<String>> errors;
	private boolean errorConcernFiles;
	private Map<Integer, Integer> files;

	protected SynchronizeResult() {
	}

	public SynchronizeResult(Map<ContainerInformation, List<String>> errors, boolean errorConcernFiles, Map<Integer, Integer> files) {
		this.errors = errors;
		this.errorConcernFiles = errorConcernFiles;
		this.files = files;
	}

	public Map<ContainerInformation, List<String>> getErrors() {
		return errors;
	}

	public Map<Integer, Integer> getFiles() {
		return files;
	}

	public boolean isErrorConcernFiles() {
		return errorConcernFiles;
	}
	
}
