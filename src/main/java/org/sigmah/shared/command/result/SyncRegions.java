package org.sigmah.shared.command.result;

import java.util.Iterator;
import java.util.List;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SyncRegions implements Result, Iterable<SyncRegion> {

	private List<SyncRegion> list;

	public SyncRegions() {
		// Serialization.
	}

	public SyncRegions(List<SyncRegion> list) {
		this.list = list;
	}

	public List<SyncRegion> getList() {
		return list;
	}

	protected void setList(List<SyncRegion> list) {
		this.list = list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<SyncRegion> iterator() {
		return list.iterator();
	}

}
