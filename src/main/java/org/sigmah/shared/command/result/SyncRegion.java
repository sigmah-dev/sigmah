package org.sigmah.shared.command.result;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SyncRegion implements Result {

	private String id;
	private boolean required;

	public SyncRegion() {
		// Serialization.
	}

	public SyncRegion(String id) {
		this.id = id;
	}

	public SyncRegion(String id, boolean required) {
		this.id = id;
		this.required = required;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
