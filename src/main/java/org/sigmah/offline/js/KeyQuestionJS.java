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

import org.sigmah.shared.dto.report.KeyQuestionDTO;
import org.sigmah.shared.dto.report.RichTextElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class KeyQuestionJS extends ProjectReportContentJS {

	protected KeyQuestionJS() {
	}
	
	public static KeyQuestionJS toJavaScript(KeyQuestionDTO keyQuestionDTO) {
		final KeyQuestionJS keyQuestionJS = Values.createJavaScriptObject(KeyQuestionJS.class);
		
		keyQuestionJS.setId(keyQuestionDTO.getId());
		keyQuestionJS.setLabel(keyQuestionDTO.getLabel());
		keyQuestionJS.setRichTextElementDTO(keyQuestionDTO.getRichTextElementDTO());
		keyQuestionJS.setNumber(keyQuestionDTO.getNumber());
		
		return keyQuestionJS;
	}
	
	protected KeyQuestionDTO createDTO() {
		final KeyQuestionDTO keyQuestionDTO = new KeyQuestionDTO();
		
		keyQuestionDTO.setId(getId());
		keyQuestionDTO.setLabel(getLabel());
		keyQuestionDTO.setRichTextElementDTO(getRichTextElementDTO());
		keyQuestionDTO.setNumber(getNumber());
		
		return keyQuestionDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native RichTextElementJS getRichTextElement() /*-{
		return this.richTextElement;
	}-*/;

	public native void setRichTextElement(RichTextElementJS richTextElement) /*-{
		this.richTextElement = richTextElement;
	}-*/;
	
	public RichTextElementDTO getRichTextElementDTO() {
		if(getRichTextElement() != null) {
			return getRichTextElement().createDTO();
		}
		return null;
	}

	public void setRichTextElementDTO(RichTextElementDTO richTextElementDTO) {
		if(richTextElementDTO != null) {
			setRichTextElement(RichTextElementJS.toJavaScript(richTextElementDTO));
		}
	}

	public native int getNumber() /*-{
		return this.number;
	}-*/;

	public native void setNumber(int number) /*-{
		this.number = number;
	}-*/;
}
