package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.Result;

import com.extjs.gxt.ui.client.data.SortInfo;

/**
 * Base class for Commands that return lists of objects, support sorting on the server, and play nicely with GXT's
 * {@link com.extjs.gxt.ui.client.data.ListLoader}.
 *
 * @param <R>
 *          The result type of the command.
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @see org.sigmah.shared.command.result.ListResult
 * @see org.sigmah.shared.command.result.PagingResult
 */
public abstract class GetListCommand<R extends Result> extends AbstractCommand<R> {

	private SortInfo sortInfo = new SortInfo();

	public SortInfo getSortInfo() {
		return sortInfo;
	}

	public void setSortInfo(SortInfo sortInfo) {
		this.sortInfo = sortInfo;
	}

}
