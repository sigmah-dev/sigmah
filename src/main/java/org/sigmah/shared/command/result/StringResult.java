package org.sigmah.shared.command.result;

/**
 * Result which contains an String.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class StringResult implements Result {

	/**
	 * The string value.
	 */
	private String value;

	public StringResult() {
		// Serialization.
	}

	public StringResult(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
