package org.sigmah.shared.command;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
