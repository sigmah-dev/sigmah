package org.sigmah.client.ui.presenter.admin;

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

import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter.SubPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;

/**
 * Abstract <b>functional parent</b> presenter for the administration presenters.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <V>
 *          View type.
 */
public abstract class AbstractAdminPresenter<V extends AbstractAdminPresenter.View> extends AbstractPagePresenter<V> implements SubPresenter<AdminPresenter> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		// No methods yet.

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	protected AbstractAdminPresenter(final V view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdminPresenter getParentPresenter() {
		return injector.getAdminPresenter();
	}

}
