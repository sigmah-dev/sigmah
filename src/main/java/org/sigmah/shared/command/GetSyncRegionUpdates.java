package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.SyncRegionUpdate;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetSyncRegionUpdates extends AbstractCommand<SyncRegionUpdate> {

	private String regionId;
	private String localVersion;

	public GetSyncRegionUpdates() {
		// Serialization.
	}

	public GetSyncRegionUpdates(String regionId, String localVersion) {
		this.regionId = regionId;
		this.localVersion = localVersion;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getLocalVersion() {
		return localVersion;
	}

	public void setLocalVersion(String localVersion) {
		this.localVersion = localVersion;
	}
}
