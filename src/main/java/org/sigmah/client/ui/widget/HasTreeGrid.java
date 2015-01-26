package org.sigmah.client.ui.widget;

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
