package org.sigmah.shared.command.result;

/**
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SyncRegionUpdate implements Result {

	private String version;
	private boolean complete;
	private String sql;

	public SyncRegionUpdate() {
		// Serialization.
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}
