package org.sigmah.client.ui.widget;

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

import org.sigmah.shared.dto.base.DTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;

/**
 * Interface implemented by all widgets managing a tree grid component.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <M>
 *          Tree grid model type.
 */
public interface HasTreeGrid<M extends DTO & ModelData> {

	/**
	 * Tree grid events handler interface.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 * @param <M>
	 *          Tree grid model type.
	 */
	public static interface TreeGridEventHandler<M extends DTO & ModelData> {

		/**
		 * Method executed each time a row receives click event.
		 * 
		 * @param rowElement
		 *          The row element.
		 */
		void onRowClickEvent(final M rowElement);

	}

	/**
	 * Returns the {@link TreeGrid} managed by this component.
	 * 
	 * @return The {@link TreeGrid} managed by this component.
	 */
	TreeGrid<M> getTreeGrid();

	/**
	 * Returns the {@link TreeStore} managed by this component.
	 * 
	 * @return The {@link TreeStore} managed by this component.
	 */
	TreeStore<M> getStore();

	/**
	 * Sets the {@link TreeGridEventHandler} implementation listening to tree grid events.
	 * 
	 * @param handler
	 *          The {@link TreeGridEventHandler} implementation.
	 */
	void setTreeGridEventHandler(TreeGridEventHandler<M> handler);

}
