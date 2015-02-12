package org.sigmah.shared.command.result;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeResult implements Result {
	
	private List<String> errors;
	private Map<Integer, Integer> files;

	protected SynchronizeResult() {
	}

	public SynchronizeResult(List<String> errors, Map<Integer, Integer> files) {
		this.errors = errors;
		this.files = files;
	}

	public List<String> getErrors() {
		return errors;
	}

	public Map<Integer, Integer> getFiles() {
		return files;
	}
	
}
