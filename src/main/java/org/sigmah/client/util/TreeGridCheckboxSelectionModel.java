package org.sigmah.client.util;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.Event;

/**
 * This selection model can be only used with a {@link TreeGrid}. When an element is selected, all its children elements
 * will be selected too.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <M>
 *          Tree data model type.
 */
public class TreeGridCheckboxSelectionModel<M extends TreeModel> extends CheckBoxSelectionModel<M> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleMouseDown(final GridEvent<M> e) {

		if (e.getEvent().getButton() == Event.BUTTON_LEFT && e.getTarget().getClassName().equals("x-grid3-row-checker")) {

			final M m = listStore.getAt(e.getRowIndex());

			if (m != null) {
				if (isSelected(m)) {
					deselect(m);
					deselectChildren(m);
				} else {
					select(m, true);
					selectChildren(m, true);
				}
			}

		} else {
			super.handleMouseDown(e);
		}
	}

	private void deselectChildren(final M m) {

		for (final ModelData md : m.getChildren()) {

			if (md instanceof BaseTreeModel) {
				@SuppressWarnings("unchecked")
				final M child = (M) md;
				deselect(child);
				deselectChildren(child);
			}
		}
	}

	private void selectChildren(final M m, final boolean keepExisting) {

		for (final ModelData md : m.getChildren()) {

			if (md instanceof BaseTreeModel) {
				@SuppressWarnings("unchecked")
				final M child = (M) md;
				select(child, keepExisting);
				selectChildren(child, keepExisting);
			}
		}
	}

}
