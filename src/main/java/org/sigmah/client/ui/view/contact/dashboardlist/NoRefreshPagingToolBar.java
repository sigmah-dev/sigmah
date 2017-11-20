package org.sigmah.client.ui.view.contact.dashboardlist;

import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class NoRefreshPagingToolBar extends PagingToolBar {
	/**
	 * Creates a new paging tool bar with the given page size.
	 *
	 * @param pageSize the page size
	 */
	public NoRefreshPagingToolBar(int pageSize) {
		super(pageSize);
		remove(refresh);
	}
}
