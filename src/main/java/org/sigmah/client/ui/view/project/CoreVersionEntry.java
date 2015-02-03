package org.sigmah.client.ui.view.project;

import com.extjs.gxt.ui.client.data.BaseModelData;
import org.sigmah.shared.dto.referential.CoreVersionAction;
import org.sigmah.shared.dto.referential.CoreVersionActionType;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CoreVersionEntry extends BaseModelData implements CoreVersionAction {
	
	public static final String NAME = "name";
	public static final String TYPE = "entry";

	protected CoreVersionEntry(String type) {
		this("", type);
	}
	
	protected CoreVersionEntry(String name, String type) {
		set(NAME, name);
		set(TYPE, type);
	}
	
	public static CoreVersionEntry create(String name, CoreVersionActionType type) {
		return new CoreVersionEntry(name, type.name());
	}
	
	public static CoreVersionEntry createSeparator() {
		return new CoreVersionEntry(CoreVersionActionType.SEPARATOR.name());
	}
	
	public static CoreVersionEntry createComment(String comment) {
		return new CoreVersionEntry(comment, CoreVersionActionType.COMMENT.name());
	}
	
	public String getName() {
		return get(NAME);
	}
	
	public String getRawType() {
		return get(TYPE);
	}

	@Override
	public CoreVersionActionType getType() {
		return CoreVersionActionType.valueOf(getRawType());
	}
	
}
