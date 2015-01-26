package org.sigmah.client.ui.widget;

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
