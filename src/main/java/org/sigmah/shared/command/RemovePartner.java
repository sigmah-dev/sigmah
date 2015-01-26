package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RemovePartner extends AbstractCommand<VoidResult> {

	private int databaseId;
	private int partnerId;

	public RemovePartner() {
		// Serialization.
	}

	public RemovePartner(int databaseId, int partnerId) {
		this.databaseId = databaseId;
		this.partnerId = partnerId;
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	public int getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(int partnerId) {
		this.partnerId = partnerId;
	}
}
