package org.sigmah.client.util.profiler;

import java.util.Map;
import org.sigmah.offline.indexeddb.Schema;
import org.sigmah.offline.indexeddb.Stores;

/**
 * Schema of the IndexedDB database used by the <code>Profiler</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum ProfilerStore implements Schema {
	
	EXECUTION(true, 
			"versionNumber", "versionNumber",
			"scenario", "scenario");

	private final boolean autoIncrement;
	private final boolean enabled;
	private final Map<String, String> indexes;

	private ProfilerStore(String... indexes) {
		this(false, true, indexes);
	}

	private ProfilerStore(boolean autoIncrement, String... indexes) {
		this(autoIncrement, true, indexes);
	}

	private ProfilerStore(boolean autoIncrement, boolean enabled, String... indexes) {
		this.autoIncrement = autoIncrement;
		this.enabled = enabled;
		this.indexes = Stores.toIndexMap(indexes);
	}

	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Map<String, String> getIndexes() {
		return indexes;
	}
	
}
