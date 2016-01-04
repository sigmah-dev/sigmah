package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.OrgUnitModelJS;
import org.sigmah.shared.dto.OrgUnitModelDTO;

import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>OrgUnitModelDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrgUnitModelAsyncDAO extends AbstractUserDatabaseAsyncDAO<OrgUnitModelDTO, OrgUnitModelJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.ORG_UNIT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitModelJS toJavaScriptObject(OrgUnitModelDTO t) {
		return OrgUnitModelJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitModelDTO toJavaObject(OrgUnitModelJS js) {
		return js.toDTO();
	}

}
