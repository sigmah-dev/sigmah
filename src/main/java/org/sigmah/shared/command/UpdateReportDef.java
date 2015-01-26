package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateReportDef extends AbstractCommand<VoidResult> {

	private int id;
	private String newXml;

	public UpdateReportDef() {
		// Serialization.
	}

	public UpdateReportDef(int id, String newXml) {
		this.id = id;
		this.newXml = newXml;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNewXml() {
		return newXml;
	}

	public void setNewXml(String newXml) {
		this.newXml = newXml;
	}

}
