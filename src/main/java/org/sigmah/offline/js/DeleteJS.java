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

import org.sigmah.shared.command.Delete;

/**
 * JavaScript version of the {@link Delete} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class DeleteJS extends CommandJS {
	
	protected DeleteJS() {
	}
	
	public static DeleteJS toJavaScript(Delete delete) {
		final DeleteJS deleteJS = Values.createJavaScriptObject(DeleteJS.class);
		
		deleteJS.setEntityName(delete.getEntityName());
		deleteJS.setEntityId(delete.getId());
		
		return deleteJS;
	}
	
	public Delete toDelete() {
		return new Delete(getEntityName(), getEntityId());
	}

	public native String getEntityName() /*-{
		return this.entityName;
	}-*/;

	public native void setEntityName(String entityName) /*-{
		this.entityName = entityName;
	}-*/;

	public Integer getEntityId() {
		return Values.getInteger(this, "entityId");
	}

	public void setEntityId(Integer id) {
		Values.setInteger(this, "entityId", id);
	}

}
