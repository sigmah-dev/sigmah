package org.sigmah.offline.js;

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
