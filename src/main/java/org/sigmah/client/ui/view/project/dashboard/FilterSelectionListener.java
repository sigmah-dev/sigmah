package org.sigmah.client.ui.view.project.dashboard;

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

/**
 * Define a listener to apply a one filter at a time and manage the menu item state.
 * 
 * @author Tom Miette
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <E>
 *          Store model type.
 */
final class FilterSelectionListener<E extends AbstractModelDataEntityDTO<?>> {

	private final Store<E> store;
	private MenuItem currentItem;
	private StoreFilter<E> currentFilter;

	public FilterSelectionListener(final Store<E> store) {
		this.store = store;
	}

	public void filter(final MenuItem item, final StoreFilter<E> filter) {
		activate();
		currentItem = item;
		filter(filter);
		desactivate();
	}

	private void activate() {
		if (currentItem != null) {
			currentItem.setEnabled(true);
		}
	}

	private void desactivate() {
		if (currentItem != null) {
			currentItem.setEnabled(false);
		}
	}

	private void filter(final StoreFilter<E> filter) {

		if (store == null) {
			return;
		}

		store.removeFilter(currentFilter);

		if (filter != null) {
			store.addFilter(filter);
		}

		store.applyFilters(null);
		currentFilter = filter;
	}

}
