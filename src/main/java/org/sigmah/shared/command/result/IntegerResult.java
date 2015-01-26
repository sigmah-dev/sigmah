package org.sigmah.shared.command.result;

/**
 * Result which contains an Integer.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IntegerResult implements Result {

	private Integer value;

	public IntegerResult() {
		// Serialization.
	}

	public IntegerResult(final Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(final Integer value) {
		this.value = value;
	}

}
