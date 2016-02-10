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

import org.sigmah.shared.dto.BoundingBoxDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class BoundingBoxJS extends JavaScriptObject {
	
	protected BoundingBoxJS() {
	}
	
	public static native BoundingBoxJS toJavaScript(BoundingBoxDTO boundingBoxDTO) /*-{
		if(boundingBoxDTO == null) {
			return null;
		} else {
			return {
				x1: boundingBoxDTO.@org.sigmah.shared.dto.BoundingBoxDTO::getX1()(),
				y1: boundingBoxDTO.@org.sigmah.shared.dto.BoundingBoxDTO::getY1()(),
				x2: boundingBoxDTO.@org.sigmah.shared.dto.BoundingBoxDTO::getX2()(),
				y2: boundingBoxDTO.@org.sigmah.shared.dto.BoundingBoxDTO::getY2()()
			};
		}
	}-*/;
	
	public native BoundingBoxDTO toDTO() /*-{
		var dto = @org.sigmah.shared.dto.BoundingBoxDTO::new()();
		dto.@org.sigmah.shared.dto.BoundingBoxDTO::setX1(D)(this.x1);
		dto.@org.sigmah.shared.dto.BoundingBoxDTO::setY1(D)(this.y1);
		dto.@org.sigmah.shared.dto.BoundingBoxDTO::setX2(D)(this.x2);
		dto.@org.sigmah.shared.dto.BoundingBoxDTO::setY2(D)(this.y2);
		return dto;
	}-*/;
}
