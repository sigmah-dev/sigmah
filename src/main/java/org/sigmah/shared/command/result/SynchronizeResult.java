package org.sigmah.shared.command.result;

import java.util.List;
import java.util.Map;
import org.sigmah.shared.dto.referential.Container;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeResult implements Result {
	
	private Map<Container, List<String>> errors;
	private boolean errorConcernFiles;
	private Map<Integer, Integer> files;

	protected SynchronizeResult() {
	}

	public SynchronizeResult(Map<Container, List<String>> errors, boolean errorConcernFiles, Map<Integer, Integer> files) {
		this.errors = errors;
		this.errorConcernFiles = errorConcernFiles;
		this.files = files;
	}

	public Map<Container, List<String>> getErrors() {
		return errors;
	}

	public Map<Integer, Integer> getFiles() {
		return files;
	}

	public boolean isErrorConcernFiles() {
		return errorConcernFiles;
	}
	
}
