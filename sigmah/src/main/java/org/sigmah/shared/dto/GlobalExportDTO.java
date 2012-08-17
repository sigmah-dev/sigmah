package org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GlobalExportDTO extends BaseModelData implements EntityDTO{

	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
		return id != null ? id : -1;
	}

	public void setId(int id) {
		set("id", id);
	}

	public String getDate() {
		return get("date");
	}

	public void setDate(String date) {
		set("date", date);
	}

	@Override
	public String getEntityName() {
 		return "GlobalExport";
	}

}
