package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.CreateResult;

/**
 * Creates a new Report Definition
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CreateReportDef extends AbstractCommand<CreateResult> {

	private String xml;
	private Integer databaseId;

	protected CreateReportDef() {
		// Serialization.
	}

	public CreateReportDef(int databaseId, String xml) {
		super();
		this.databaseId = databaseId;
		this.xml = xml;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Integer getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Integer databaseId) {
		this.databaseId = databaseId;
	}

}
