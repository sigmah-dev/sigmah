package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.StringResult;

/**
 * Returns the XML definition of the {@code org.sigmah.shared.report.model.Report} for a given
 * {@link org.sigmah.server.domain.ReportDefinition ReportDefinition} database entity.
 *
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetReportDef extends AbstractCommand<StringResult> {

	private int id;

	protected GetReportDef() {
		// Serialization.
	}

	/**
	 * @param id
	 *          The id of the {@link org.sigmah.server.domain.ReportDefinition} database entity for which to return the
	 *          XML definition.
	 */
	public GetReportDef(int id) {
		this.id = id;
	}

	/**
	 * @return The id of the {@link org.sigmah.server.domain.ReportDefinition} database entity for which to return the XML
	 *         definition.
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
