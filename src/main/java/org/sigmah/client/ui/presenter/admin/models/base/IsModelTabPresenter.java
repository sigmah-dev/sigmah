package org.sigmah.client.ui.presenter.admin.models.base;

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
