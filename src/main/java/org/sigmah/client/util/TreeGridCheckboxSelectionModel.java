package org.sigmah.client.util;

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
