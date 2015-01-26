package org.sigmah.shared.command;

import java.util.Date;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.GlobalExportDTO;

/**
 * @author sherzod (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetGlobalExports extends AbstractCommand<ListResult<GlobalExportDTO>> {

	private Date fromData;
	private Date toDate;
	private String dateFormat;

	public GetGlobalExports() {
		// Serialization.
	}

	public GetGlobalExports(Date fromDate, Date toData, String dateFormat) {
		this.fromData = fromDate;
		this.toDate = toData;
		this.dateFormat = dateFormat;
	}

	public Date getFromData() {
		return fromData;
	}

	public void setFromData(Date fromData) {
		this.fromData = fromData;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

}
