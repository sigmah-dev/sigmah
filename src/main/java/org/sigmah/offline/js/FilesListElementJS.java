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
	
	protected FilesListElementDTO toFilesListElementDTO() {
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
