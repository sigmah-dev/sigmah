package org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.ModelData;

public interface TeamMemberDTO extends ModelData {
	String ID = "id";
	String NAME = "name";
	String TYPE = "type";
	String ORDER = "order";

	enum TeamMemberType {
		MANAGER, TEAM_MEMBER
	}
}
