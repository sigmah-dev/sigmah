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

import java.io.Serializable;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;

public class UpdateLayoutGroupIterations extends AbstractCommand<ListResult<UpdateLayoutGroupIterations.IterationChange>> {

	public static class IterationChange implements Serializable {
		private int iterationId;
		private int newIterationId;
		private String name;
		private int layoutGroupId;
		private boolean deleted;

		public IterationChange() {
		}

		public int getIterationId() {
			return iterationId;
		}

		public void setIterationId(int iterationId) {
			this.iterationId = iterationId;
		}

		public int getNewIterationId() {
			return newIterationId;
		}

		public void setNewIterationId(int newIterationId) {
			this.newIterationId = newIterationId;
		}

		public boolean isCreated() {
			return iterationId < 0;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getLayoutGroupId() {
			return layoutGroupId;
		}

		public void setLayoutGroupId(int layoutGroupId) {
			this.layoutGroupId = layoutGroupId;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
	}

	private List<IterationChange> iterationChanges;
	private int containerId;

	public UpdateLayoutGroupIterations() {
		// serialization
	}

  public UpdateLayoutGroupIterations(List<IterationChange> iterationChanges, int containerId) {
		this.iterationChanges = iterationChanges;
		this.containerId = containerId;
	}

	public List<IterationChange> getIterationChanges() {
		return iterationChanges;
	}

	public void setIterationChanges(List<IterationChange> iterationChanges) {
		this.iterationChanges = iterationChanges;
	}

	public int getContainerId() {
		return containerId;
	}

	public void setContainerId(int containerId) {
		this.containerId = containerId;
	}
}
