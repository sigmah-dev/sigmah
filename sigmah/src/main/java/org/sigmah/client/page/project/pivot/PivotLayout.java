package org.sigmah.client.page.project.pivot;

import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.data.ModelData;


/**
 * Encapsulates the state of a pivot table layout.
 * 
 * @author alexander
 *
 */
class PivotLayout {

	private ModelData filter;

	public PivotLayout(ModelData filter) {
		this.filter = filter;
	}

	public ModelData getFilter() {
		return filter;
	}

	public void setFilter(ModelData filter) {
		this.filter = filter;
	}

	
}
