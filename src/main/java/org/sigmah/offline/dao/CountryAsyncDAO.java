package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.CountryJS;
import org.sigmah.shared.dto.country.CountryDTO;

import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>CountryDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CountryAsyncDAO extends AbstractUserDatabaseAsyncDAO<CountryDTO, CountryJS> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.COUNTRY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CountryJS toJavaScriptObject(CountryDTO t) {
		return CountryJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CountryDTO toJavaObject(CountryJS js) {
		return js.toDTO();
	}
	
}
