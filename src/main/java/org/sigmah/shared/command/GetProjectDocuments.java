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

import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.report.ReportReference;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectDocuments extends AbstractCommand<ListResult<ReportReference>> {

	public static final class FilesListElement implements IsSerializable {

		private Integer id;
		private String phaseName;
		private String elementLabel;

		protected FilesListElement() {
			// Serialization.
		}

		public FilesListElement(Integer id, String phaseName, String elementLabel) {
			this.id = id;
			this.phaseName = phaseName;
			this.elementLabel = elementLabel;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getPhaseName() {
			return phaseName;
		}

		public void setPhaseName(String phaseName) {
			this.phaseName = phaseName;
		}

		public String getElementLabel() {
			return elementLabel;
		}

		public void setElementLabel(String elementLabel) {
			this.elementLabel = elementLabel;
		}
	}

	private Integer projectId;

	private List<FilesListElement> elements;

	protected GetProjectDocuments() {
		// Serialization.
	}

	public GetProjectDocuments(Integer projectId, List<FilesListElement> elements) {
		this.projectId = projectId;
		this.elements = elements;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public List<FilesListElement> getElements() {
		return elements;
	}

}
