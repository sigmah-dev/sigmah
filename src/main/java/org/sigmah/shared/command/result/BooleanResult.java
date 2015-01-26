package org.sigmah.shared.command.result;

/**
 * Result which contains a boolean.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BooleanResult implements Result {

	private boolean value;

	public BooleanResult() {
		// Serialization.
	}

	public BooleanResult(final boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(final boolean value) {
		this.value = value;
	}

}
