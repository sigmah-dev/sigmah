package org.sigmah.shared.command.result;

import org.sigmah.server.domain.report.ProjectReportModel;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ModelReference extends BaseModelData implements Result {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6681884453456869082L;

	public ModelReference() {
		// Serialization.
	}

	public ModelReference(ProjectReportModel model) {
		this.set("id", model.getId());
		this.set("name", model.getName());
	}

	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		this.set("id", id);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		this.set("name", name);
	}

}
