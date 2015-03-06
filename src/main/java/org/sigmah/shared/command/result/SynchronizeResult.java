package org.sigmah.shared.command.result;

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
