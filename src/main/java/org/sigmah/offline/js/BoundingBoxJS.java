package org.sigmah.offline.js;

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
