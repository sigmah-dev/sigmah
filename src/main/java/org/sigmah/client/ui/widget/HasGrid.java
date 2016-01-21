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
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.Grid;

/**
 * Interface implemented by all widgets managing a grid component.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <M>
 *          Grid model type.
 */
public interface HasGrid<M extends DTO & ModelData> {

	/**
	 * Grid events handler interface.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 * @param <M>
	 *          Grid model type.
	 */
	public static interface GridEventHandler<M extends DTO & ModelData> {

		/**
		 * Method executed each time a row receives click event.
		 * 
		 * @param rowElement
		 *          The row element.
		 */
		void onRowClickEvent(final M rowElement);

	}

	/**
	 * Returns the {@link Grid} managed by this component.
	 * 
	 * @return The {@link Grid} managed by this component.
	 */
	Grid<M> getGrid();

	/**
	 * Returns the {@link ListStore} managed by this component.
	 * 
	 * @return The {@link ListStore} managed by this component.
	 */
	ListStore<M> getStore();

	/**
	 * Sets the {@link GridEventHandler} implementation listening to grid events.
	 * 
	 * @param handler
	 *          The {@link GridEventHandler} implementation.
	 */
	void setGridEventHandler(GridEventHandler<M> handler);

}
