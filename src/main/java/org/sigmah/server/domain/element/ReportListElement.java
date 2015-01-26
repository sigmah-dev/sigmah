package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Report list element domain entity
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.REPORT_LIST_ELEMENT_TABLE)
public class ReportListElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1752871868903402599L;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Link to the ProjectReportModel that will be used by the report contained by this element.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.REPORT_LIST_ELEMENT_COLUMN_MODEL_ID, nullable = true)
	private ProjectReportModel model;

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public ProjectReportModel getModel() {
		return model;
	}

	public void setModel(ProjectReportModel model) {
		this.model = model;
	}

}
