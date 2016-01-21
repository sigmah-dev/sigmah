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

import org.sigmah.client.ui.widget.form.FormPanel;

/**
 * Interface implemented by presenters managing one or several form(s).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface HasForm {

	/**
	 * Returns the {@link FormPanel}(s) managed by the component.
	 * 
	 * @return The {@link FormPanel}(s) managed by the component ({@code null} value(s) are ignored).
	 */
	FormPanel[] getForms();

}
