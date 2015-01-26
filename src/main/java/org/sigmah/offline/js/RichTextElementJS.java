package org.sigmah.offline.js;

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
	
	@Override
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
