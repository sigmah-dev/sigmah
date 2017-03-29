package org.sigmah.client.ui.presenter.project;

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
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.project.ProjectReportsView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetProjectDocuments.FilesListElement;
import org.sigmah.shared.dto.ProjectDTO.LocalizedElement;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Project's reports presenter which manages the {@link ProjectReportsView}.<br>
 * Most part of its logic is delegated to the {@link ReportsPresenter}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectReportsPresenter extends AbstractProjectPresenter<ProjectReportsPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends AbstractProjectPresenter.View {

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

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param factory
	 *          Injected client injector.
	 */
	public ProjectReportsPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
		reportsPresenter = factory.getReportsPresenter();
		view.provideReportsView(reportsPresenter.getView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_REPORTS;
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

		reportsPresenter.setPhaseName(getProject().getCurrentPhase().getPhaseModel().getName());

		// --
		// Loads current project related documents (files & reports).
		// --

		final List<FilesListElement> filesLists = new ArrayList<GetProjectDocuments.FilesListElement>();
		for (final LocalizedElement<FilesListElementDTO> element : getProject().getLocalizedElements(FilesListElementDTO.class)) {
			filesLists.add(new GetProjectDocuments.FilesListElement(element.getElement().getId(),
				element.getPhaseModel() != null ? element.getPhaseModel().getName() : I18N.CONSTANTS.projectDetails(), element.getElement().getLabel()));
		}
		reportsPresenter.loadProjectDocuments(getProject().getId(), filesLists);

		// --
		// Loads received report id (if any).
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
		final List<LocalizedElement<FilesListElementDTO>> filesListElements = getProject().getLocalizedElements(FilesListElementDTO.class);

		return createMenu(filesListElements, false);
	}

	/**
	 * Creates the <em>create report</em> menu.
	 * 
	 * @return The menu.
	 */
	private Menu createCreateReportMenu() {

		// Retrieves all the report and report list elements in the current project.
		final List reportElements = getProject().getLocalizedElements(ReportElementDTO.class);
		final List reportsListElements = getProject().getLocalizedElements(ReportListElementDTO.class);

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
	private <E extends FlexibleElementDTO> Menu createMenu(final List<LocalizedElement<E>> elements, final boolean createReportMenu) {

		final Menu menu = new Menu();
		menu.setEnabled(false);

		if (elements == null) {
			return menu;
		}

		// For each files list.
		for (final LocalizedElement<E> element : elements) {
			final PrivacyGroupPermissionEnum permission = ProfileUtils.getPermissionForOrgUnit(auth(),
				getProject().getOrgUnitId(), element.getElement().getPrivacyGroup());

			if (permission != PrivacyGroupPermissionEnum.WRITE) {
				continue;
			}

			boolean itemEnabled = false;

			// Builds the item label.
			final String phaseName;
			if (element.getPhaseModel() != null) {
				phaseName = element.getPhaseModel().getName();
			} else {
				phaseName = I18N.CONSTANTS.projectDetails();
			}

			// Builds the corresponding menu item.
			final MenuItem item = new MenuItem(phaseName + " | " + element.getElement().getLabel());

			if (element.getPhase() == null) {

				// --
				// If the phase is the details page.
				// --

				item.addSelectionListener(new ReportMenuSelectionListener(eventBus, getProject().getId(), I18N.CONSTANTS.projectDetails(), element.getElement(),
					createReportMenu));
				itemEnabled = true;

			} else if (element.getPhase().isEnded()) {

				// --
				// If the phase is closed.
				// --

				item.setIcon(IconImageBundle.ICONS.close());
				item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseClosed());

			} else if (element.getPhase().equals(getProject().getCurrentPhase())) {

				// --
				// If the phase is active.
				// --

				item.setIcon(IconImageBundle.ICONS.activate());
				item.addSelectionListener(new ReportMenuSelectionListener(eventBus, getProject().getId(), element.getPhaseModel().getName(), element.getElement(),
					createReportMenu));
				itemEnabled = true;

			} else if (element.getPhase().isSuccessor(getProject().getCurrentPhase())) {

				// --
				// If the phase is a successor of the active one.
				// --

				item.addSelectionListener(new ReportMenuSelectionListener(eventBus, getProject().getId(), element.getPhaseModel().getName(), element.getElement(),
					createReportMenu));
				itemEnabled = true;

			} else {

				// --
				// Future phase, not yet accessible.
				// --

				item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseInactive());
			}

			if (itemEnabled) {
				reportsPresenter.setMenuItemEnabled(item, getProject().getId(), element.getElement(), createReportMenu);
				menu.setEnabled(true);
			}

			menu.add(item);
		}

		return menu;
	}

}
