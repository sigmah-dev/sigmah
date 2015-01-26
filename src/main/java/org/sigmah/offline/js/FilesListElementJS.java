package org.sigmah.offline.js;

import org.sigmah.shared.dto.element.FilesListElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class FilesListElementJS extends FlexibleElementJS {
	
	protected FilesListElementJS() {
	}
	
	public static FilesListElementJS toJavaScript(FilesListElementDTO filesListElementDTO) {
		final FilesListElementJS filesListElementJS = Values.createJavaScriptObject(FilesListElementJS.class);
		
		filesListElementJS.setLimit(filesListElementDTO.getLimit());
		
		return filesListElementJS;
	}
	
	@Override
	protected FilesListElementDTO createDTO() {
		final FilesListElementDTO filesListElementDTO = new FilesListElementDTO();
		
		filesListElementDTO.setLimit(getLimitInteger());
		
		return filesListElementDTO;
	}
	
	public native boolean hasLimit() /*-{
		return typeof this.limit != 'undefined';
	}-*/;
	
	public native int getLimit() /*-{
		return this.limit;
	}-*/;
	
	public Integer getLimitInteger() {
		if(hasLimit()) {
			return getLimit();
		}
		return null;
	}
	
	public native void setLimit(int limit) /*-{
		this.limit = limit;
	}-*/;
	
	public void setLimit(Integer limit) {
		if(limit != null) {
			setLimit(limit.intValue());
		}
	}
}
