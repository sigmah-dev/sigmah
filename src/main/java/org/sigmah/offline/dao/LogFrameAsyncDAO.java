package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.LogFrameJS;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>LogFrameDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class LogFrameAsyncDAO extends AbstractUserDatabaseAsyncDAO<LogFrameDTO, LogFrameJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.LOG_FRAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogFrameJS toJavaScriptObject(LogFrameDTO t) {
		return LogFrameJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LogFrameDTO toJavaObject(LogFrameJS js) {
		return js.toDTO();
	}
	
}
