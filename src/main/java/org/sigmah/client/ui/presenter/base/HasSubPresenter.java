package org.sigmah.client.ui.presenter.base;

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

import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.view.base.HasSubView;

/**
 * Interface implemented by presenters managing one or several sub-presenters.
 * 
 * @param <V>
 *          Parent presenter's view interface type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface HasSubPresenter<V extends HasSubView> extends Presenter<V> {

	/**
	 * Interface implemented by all sub-presenters managed by a parent presenter.
	 * 
	 * @param <P>
	 *          Parent presenter type.
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface SubPresenter<P extends HasSubPresenter<? extends HasSubView>> {

		/**
		 * Returns the parent presenter managing the current presenter.
		 * 
		 * @return The parent presenter managing the current presenter.
		 */
		P getParentPresenter();

	}

	/**
	 * Method executed each time the parent presenter is <em>requested</em> through one of its sub-presenters.
	 * 
	 * @param subPageRequest
	 *          The sub-presenter page request.
	 */
	void onSubPresenterRequest(PageRequest subPageRequest);

}
