package org.sigmah.shared.command;

import java.util.ArrayList;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.util.Month;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateMonthlyReports extends AbstractCommand<VoidResult> {

	public static class Change implements IsSerializable {

		public Month month;
		public int indicatorId;
		public Double value;

		public Change() {
			// Serialization.
		}

		public Change(int indicatorId, Month month, Double value) {
			this.indicatorId = indicatorId;
			this.month = month;
			this.value = value;
		}

	}

	private int siteId;
	private ArrayList<Change> changes;

	public UpdateMonthlyReports() {
		// Serialization.
	}

	public UpdateMonthlyReports(int siteId, ArrayList<Change> changes) {
		this.siteId = siteId;
		this.changes = changes;
	}

	public UpdateMonthlyReports(int siteId, Change... changes) {
		this.siteId = siteId;
		this.changes = new ArrayList<Change>();
		for (Change c : changes) {
			this.changes.add(c);
		}
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public ArrayList<Change> getChanges() {
		return changes;
	}

	public void setChanges(ArrayList<Change> changes) {
		this.changes = changes;
	}
}
