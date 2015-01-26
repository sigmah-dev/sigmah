package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Entirely removes a project report draft.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RemoveProjectReportDraft extends AbstractCommand<VoidResult> {

	private int versionId;

	public RemoveProjectReportDraft() {
		// Serialization.
	}

	public RemoveProjectReportDraft(int versionId) {
		this.versionId = versionId;
	}

	public int getVersionId() {
		return versionId;
	}

	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

}
