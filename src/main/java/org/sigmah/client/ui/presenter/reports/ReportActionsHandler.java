package org.sigmah.client.ui.presenter.reports;

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

/**
 * Report actions interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ReportActionsHandler {

	/**
	 * Returns the report edit button enabled/visibility state.
	 * 
	 * @return {@code true} if the edit button should be visible and enabled.
	 */
	boolean isEditionEnabled();

	/**
	 * Callback executed on report <b>close</b> action (upper right close icon).
	 */
	void onCloseReport();

	/**
	 * Callback executed on report <b>delete</b> button action.
	 */
	void onDeleteReport();

	/**
	 * Callback executed on report <b>share (send)</b> button action.
	 */
	void onShareReport();

	/**
	 * Callback executed on report <b>save</b> button action.
	 */
	void onSaveReport();

	/**
	 * Callback executed on report <b>edit</b> action.
	 */
	void onEditReport();

	/**
	 * Callback executed on report <b>export</b> button action.
	 */
	void onExportReport();

}
