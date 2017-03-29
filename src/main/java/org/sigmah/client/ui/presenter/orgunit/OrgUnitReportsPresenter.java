package org.sigmah.client.ui.presenter.orgunit;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.reports.ReportMenuSelectionListener;
import org.sigmah.client.ui.presenter.reports.ReportsPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.orgunit.OrgUnitReportsView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetProjectDocuments.FilesListElement;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO.LocalizedElement;

import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * OrgUnit Reports Presenter.<br>
 * Most part of its logic is delegated to the {@link ReportsPresenter}.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class OrgUnitReportsPresenter extends AbstractOrgUnitPresenter<OrgUnitReportsPresenter.View> {

	/**
	 * Presenter's view interface.
	 */
	public static interface View extends AbstractOrgUnitPresenter.View {

		/**
		 * Provides the reports presenter's view.<br>
		 * Should be called before view initialization.
		 * 
		 * @param view
		 *          The view.
		 */
		// Should be executed before view initialization.
		void provideReportsView(ViewInterface view);

	}

	/**
	 * The reports presenter.
	 */
	private final ReportsPresenter reportsPresenter;

	public OrgUnitReportsPresenter(View view, ClientFactory factory) {
		super(view, factory);
		reportsPresenter = factory.getReportsPresenter();
		view.provideReportsView(reportsPresenter.getView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ORGUNIT_REPORTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		reportsPresenter.initialize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// Provides phase name to the reports presenter.
		// --

		reportsPresenter.setPhaseName(null); // Organizational Units don't have phase element.

		// --
		// Loads current project related documents (files & reports).
		// --

		final List<FilesListElement> filesLists = new ArrayList<GetProjectDocuments.FilesListElement>();
		for (final LocalizedElement element : getOrgUnit().getLocalizedElements(FilesListElementDTO.class)) {
			filesLists.add(new FilesListElement(element.getElement().getId(), I18N.CONSTANTS.projectDetails(), element.getElement().getLabel()));
		}

		reportsPresenter.loadProjectDocuments(getOrgUnit().getId(), filesLists);

		// --
		// Loads received report id.
		// --

		reportsPresenter.loadReport(request.getParameterInteger(RequestParameter.REPORT_ID));

		// --
		// Creates attach file menu.
		// --

		reportsPresenter.setAttachFileButtonMenu(createAttachFileMenu());

		// --
		// Creates create report menu.
		// --

		reportsPresenter.setCreateReportButtonMenu(createCreateReportMenu());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasValueChanged() {
		return reportsPresenter.hasValueChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLeavingOk() {
		reportsPresenter.disableAutoSaveTimer();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Creates the <em>attach file</em> menu.
	 * 
	 * @return The menu.
	 */
	private Menu createAttachFileMenu() {

		// Retrieves all the files list elements in the current project.
		final List<LocalizedElement> filesListElements = getOrgUnit().getLocalizedElements(FilesListElementDTO.class);

		return createMenu(filesListElements, false);
	}

	/**
	 * Creates the <em>create report</em> menu.
	 * 
	 * @return The menu.
	 */
	private Menu createCreateReportMenu() {

		// Retrieves all the report and report list elements in the current project.
		final List<LocalizedElement> reportElements = getOrgUnit().getLocalizedElements(ReportElementDTO.class);
		final List<LocalizedElement> reportsListElements = getOrgUnit().getLocalizedElements(ReportListElementDTO.class);

		return createMenu(ClientUtils.concat(reportElements, reportsListElements), true);
	}

	/**
	 * Creates the menu for the given {@code elements}.
	 * 
	 * @param elements
	 *          The localized elements.
	 * @param createReportMenu
	 *          {@code true} to build a <em>create report</em> menu, {@code false} to build an <em>attach file</em> menu.
	 * @return The menu for the given {@code elements}.
	 */
	private Menu createMenu(final List<LocalizedElement> elements, final boolean createReportMenu) {

		final Menu menu = new Menu();
		menu.setEnabled(false);

		if (elements == null) {
			return menu;
		}

		// For each files list.
		for (final LocalizedElement element : elements) {

			boolean itemEnabled = false;

			// Builds the corresponding menu item.
			final MenuItem item = new MenuItem(I18N.CONSTANTS.projectDetails() + " | " + element.getElement().getLabel());

			// If the phase is the details page.
			item.addSelectionListener(new ReportMenuSelectionListener(eventBus, getOrgUnit().getId(), I18N.CONSTANTS.projectDetails(), element.getElement(),
				createReportMenu));
			item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseInactive());
			itemEnabled = true;

			if (itemEnabled) {
				reportsPresenter.setMenuItemEnabled(item, getOrgUnit().getId(), element.getElement(), createReportMenu);
				menu.setEnabled(true);
			}

			menu.add(item);
		}

		return menu;
	}

}
