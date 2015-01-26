package org.sigmah.offline.js;

import com.extjs.gxt.ui.client.data.RpcMap;
import com.google.gwt.core.client.JsArrayString;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.shared.command.UpdateEntity;

/**
 * Simple JavaScript object used to store {@link UpdateEntity} commands inside
 * an IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateEntityJS extends CommandJS {
	
	protected UpdateEntityJS() {
	}
	
	public static UpdateEntityJS toJavaScript(UpdateEntity updateEntity) {
		final UpdateEntityJS updateEntityJS = Values.createJavaScriptObject(UpdateEntityJS.class);
		
		updateEntityJS.setEntityId(updateEntity.getId());
		updateEntityJS.setEntityName(updateEntity.getEntityName());
		updateEntityJS.setChanges(updateEntity.getChanges());
		
		return updateEntityJS;
	}
	
	public UpdateEntity toUpdateEntity() {
		return new UpdateEntity(getEntityName(), getEntityId(), getChangeMap());
	}
	
	public Integer getEntityId() {
		return Values.getInteger(this, "entityId");
	}

	public void setEntityId(Integer id) {
		Values.setInteger(this, "entityId", id);
	}
	
	public native String getEntityName() /*-{
		return this.entityName;
	}-*/;

	public native void setEntityName(String entityName) /*-{
		this.entityName = entityName;
	}-*/;
	
	public native JsMap<String, String> getChanges() /*-{
		return this.changes;
	}-*/;
	
	public native void setChanges(JsMap<String, String> changes) /*-{
		this.changes = changes;
	}-*/;
	
	public Map<String, Object> getChangeMap() {
		if(getChanges() != null) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			
			final JsMap<String, String> changes = getChanges();
			final JsArrayString keys = changes.keyArray();
			
			final ObjectJsMapBoxer boxer = new ObjectJsMapBoxer();
			
			for(int index = 0; index < keys.length(); index++) {
				final String key = keys.get(index);
				map.put(key, boxer.fromString(changes.get(key)));
			}
			
			return map;
		}
		return null;
	}
	
	public void setChanges(RpcMap properties) {
		if(properties != null) {
			final JsMap<String, String> map = JsMap.createMap();
			final ObjectJsMapBoxer boxer = new ObjectJsMapBoxer();
			
			for(final Map.Entry<String, Object> entry : properties.entrySet()) {
				map.put(entry.getKey(), boxer.toString(entry.getValue()));
			}
			
			setChanges(map);
		}
	}
}
