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

import org.sigmah.shared.dto.report.RichTextElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class RichTextElementJS extends ProjectReportContentJS {

	protected RichTextElementJS() {
	}
	
	public static RichTextElementJS toJavaScript(RichTextElementDTO richTextElementDTO) {
		final RichTextElementJS richTextElementJS = Values.createJavaScriptObject(RichTextElementJS.class);
		
		richTextElementJS.setId(richTextElementDTO.getId());
		richTextElementJS.setText(richTextElementDTO.getText());
		
		return richTextElementJS;
	}
	
	protected RichTextElementDTO createDTO() {
		final RichTextElementDTO richTextElementDTO = new RichTextElementDTO();
		
		richTextElementDTO.setId(getId());
		richTextElementDTO.setText(getText());
		
		return richTextElementDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getText() /*-{
		return this.text;
	}-*/;

	public native void setText(String text) /*-{
		this.text = text;
	}-*/;
}
