package org.sigmah.client.ui.presenter.admin.models.base;

import org.sigmah.client.ui.presenter.base.Presenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.shared.dto.IsModel;

/**
 * Interface implemented by all presenters managing a model tab component.
 * 
 * @param <E>
 *          The model type.
 * @param <V>
 *          The model tab-presenter's view interface.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface IsModelTabPresenter<E extends IsModel, V extends ViewInterface> extends Presenter<V> {

	/**
	 * Returns the corresponding tab title.
	 * 
	 * @return The corresponding tab title.
	 */
	String getTabTitle();

	/**
	 * Loads the tab for the given {@code model}.
	 * 
	 * @param model
	 *          The current model (never {@code null}).
	 */
	void loadTab(E model);

	/**
	 * Returns if a value change event has been detected.
	 * 
	 * @return {@code true} if a value change event has been detected.
	 */
	boolean hasValueChanged();

}
