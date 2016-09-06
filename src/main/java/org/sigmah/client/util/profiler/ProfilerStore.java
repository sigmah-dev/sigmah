package org.sigmah.client.util.profiler;

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
