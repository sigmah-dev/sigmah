package org.sigmah.offline.js;

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

import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DefaultFlexibleElementJS extends FlexibleElementJS {
	
	protected DefaultFlexibleElementJS() {
	}
	
	public static DefaultFlexibleElementJS toJavaScript(DefaultFlexibleElementDTO defaultFlexibleElementDTO) {
		final DefaultFlexibleElementJS defaultFlexibleElementJS;
		
		if(defaultFlexibleElementDTO.getType() == DefaultFlexibleElementType.BUDGET
				// FIXME: the following test should not be mandatory
				&& defaultFlexibleElementDTO.getClass() == BudgetElementDTO.class) {
			defaultFlexibleElementJS = BudgetElementJS.toJavaScript((BudgetElementDTO) defaultFlexibleElementDTO);
		} else {
			defaultFlexibleElementJS = Values.createJavaScriptObject(DefaultFlexibleElementJS.class);
		}
		
		defaultFlexibleElementJS.setType(defaultFlexibleElementDTO.getType());
		
		return defaultFlexibleElementJS;
	}
	
	public final DefaultFlexibleElementDTO toDefaultFlexibleElementDTO() {
		final DefaultFlexibleElementDTO defaultFlexibleElementDTO;
		
		if(getTypeEnum() == DefaultFlexibleElementType.BUDGET) {
			defaultFlexibleElementDTO = ((BudgetElementJS)this).toBudgetElementDTO();
		} else {
			defaultFlexibleElementDTO = new DefaultFlexibleElementDTO();
		}
		
		defaultFlexibleElementDTO.setType(getTypeEnum());
		return defaultFlexibleElementDTO;
	}

	public final native String getType() /*-{
		return this.type;
	}-*/;

	public final DefaultFlexibleElementType getTypeEnum() {
		if(getType() != null) {
			return DefaultFlexibleElementType.valueOf(getType());
		}
		return null;
	}

	public final void setType(DefaultFlexibleElementType type) {
		if(type != null) {
			setType(type.name());
		}
	}
	
	public final native void setType(String type) /*-{
		this.type = type;
	}-*/;
}
