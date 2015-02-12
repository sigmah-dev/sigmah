package org.sigmah.offline.js;

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
