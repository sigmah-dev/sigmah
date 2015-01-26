package org.sigmah.shared.command;

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
