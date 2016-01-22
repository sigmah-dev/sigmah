package org.sigmah.client.ui.view.project;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
