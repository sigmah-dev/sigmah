/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.command;

import java.util.Date;

import org.sigmah.shared.command.result.GlobalExportListResult;
/*
 * @author sherzod
 */
public class GetGlobalExports implements Command<GlobalExportListResult>{
	
 
	private static final long serialVersionUID = -4625058899560747321L;
	private Date fromData;
	private Date toDate;
	
	public GetGlobalExports(){}
	
	public GetGlobalExports(Date fromDate, Date toData){
		this.fromData=fromDate;
		this.toDate=toData;
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
	
	

}
