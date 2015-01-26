package org.sigmah.shared.command.result;

/**
 * Result which contains a long.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LongResult implements Result {

	private Long value;

	public LongResult() {
		// Serialization.
	}

	public LongResult(final Long value) {
		this.value = value;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(final Long value) {
		this.value = value;
	}

}
