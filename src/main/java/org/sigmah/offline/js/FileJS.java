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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;

import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class FileJS extends ListableValueJS {
	
	protected FileJS() {
	}
	
	public static FileJS toJavaScript(FileDTO fileDTO) {
		final FileJS fileJS = Values.createJavaScriptObject(FileJS.class);
		fileJS.setListableValueType(Type.FILE);
		
		fileJS.setId(fileDTO.getId());
		fileJS.setName(fileDTO.getName());
		fileJS.setVersions(fileDTO.getVersions());
		
		return fileJS;
	}
	
	public FileDTO toFileDTO() {
		final FileDTO fileDTO = new FileDTO();
		
		fileDTO.setId(getId());
		fileDTO.setName(getName());
		fileDTO.setVersions(getVersionsDTO());
		
		return fileDTO;
	}

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native JsArray<FileVersionJS> getVersions() /*-{
		return this.versions;
	}-*/;

	public List<FileVersionDTO> getVersionsDTO() {
		if(getVersions() != null) {
			final JsArray<FileVersionJS> versions = getVersions();
			final ArrayList<FileVersionDTO> list = new ArrayList<FileVersionDTO>();
			
			for(int index = 0; index < versions.length(); index++) {
				list.add(versions.get(index).toDTO());
			}
			
			return list;
		}
		return null;
	}

	public void setVersions(List<FileVersionDTO> versions) {
		if(versions != null) {
			final JsArray<FileVersionJS> array = Values.createTypedJavaScriptArray(FileVersionJS.class);
			
			for(final FileVersionDTO version : versions) {
				array.push(FileVersionJS.toJavaScript(version));
			}
			
			setVersions(array);
		}
	}
	
	public native void setVersions(JsArray<FileVersionJS> versions) /*-{
		this.versions = versions;
	}-*/;
}
