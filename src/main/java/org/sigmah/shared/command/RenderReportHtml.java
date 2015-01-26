package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.StringResult;
import org.sigmah.shared.util.DateRange;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RenderReportHtml extends AbstractCommand<StringResult> {

	private int templateId;
	private DateRange dateRange;

	public RenderReportHtml() {
		// Serialization.
	}

	public RenderReportHtml(int templateId, DateRange dateRange) {
		this.templateId = templateId;
		this.dateRange = dateRange;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
}
