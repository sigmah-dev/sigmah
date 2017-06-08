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


import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter;
import org.sigmah.client.ui.view.base.HasSubView;
import org.sigmah.client.ui.view.orgunit.OrgUnitView;
import org.sigmah.client.ui.widget.SubMenuItem;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.SubMenuListener;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.OrgUnitBannerDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.page.Page;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * <p>
 * <b>UI parent</b> presenter which manages the {@link OrgUnitView}.
 * </p>
 * <p>
 * Does not respond to a page token. Manages sub-presenters.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class OrgUnitPresenter extends AbstractPresenter<OrgUnitPresenter.View> implements HasSubPresenter<OrgUnitPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(OrgUnitView.class)
	public static interface View extends HasSubView {

		/**
		 * Returns the sub-menu widget.
		 * 
		 * @return The sub-menu widget.
		 */
		SubMenuWidget getSubMenuWidget();

		// --
		// ORG UNIT BANNER.
		// --

		/**
		 * Sets the OrgUnit view title.
		 * 
		 * @param orgUnitName
		 *          The OrgUnit name.
		 */
		void setOrgUnitTitle(String orgUnitName);

		ContentPanel getOrgUnitBannerPanel();

		void setOrgUnitBanner(final Widget bannerWidget);

		HTMLTable buildBannerTable(final int rows, final int cols);

	}

	/**
	 * The current OrgUnit.
	 */
	private OrgUnitDTO orgUnit;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public OrgUnitPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// SubMenu listener.
		view.getSubMenuWidget().addListener(new SubMenuListener() {

			@Override
			public void onSubMenuClick(final SubMenuItem menuItem) {

				final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest(false);
				eventBus.navigateRequest(menuItem.getRequest().addAllParameters(currentPageRequest.getParameters(true)));

			}

		});

		// --
		// Project banner update event handler.
		// --
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.PROJECT_BANNER_UPDATE)) {
					refreshBanner(orgUnit);

				}  else if(event.concern(UpdateEvent.CORE_VERSION_UPDATED)) {
					// This is really harsh but it was the simplest to have to latest project revision.
					// If too much, it is possible to set revision to revision + 1 and call loadAmendments instead.
					eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest(), new LoadingMask(view.getOrgUnitBannerPanel()));
				}
			}
		}));
		
		view.getSubMenuWidget().setRequiredPermissions(Page.ORGUNIT_CALENDAR, GlobalPermissionEnum.VIEW_ORG_UNIT_AGENDA);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSubPresenterRequest(final PageRequest subPageRequest) {

		// Updates sub-menu widget.
		view.getSubMenuWidget().initializeMenu(subPageRequest.getPage(), auth());

		// Updates parent view elements.
		refreshBanner(orgUnit);
	}

	/**
	 * Returns the current loaded OrgUnit.
	 * 
	 * @return The current loaded OrgUnit instance.
	 */
	public OrgUnitDTO getCurrentOrgUnit() {
		return orgUnit;
	}

	/**
	 * Sets the current OrgUnit.
	 * 
	 * @param orgUnit
	 *          The OrgUnit instance.
	 */
	public void setCurrentOrgUnit(final OrgUnitDTO orgUnit) {
		this.orgUnit = orgUnit;
	}

	/**
	 * Refresh header orgUnit dashboard panel.
	 * 
	 * @param orgUnit
	 *          The {@link OrgUnitDTO} instance.
	 */
	private void refreshBanner(final OrgUnitDTO orgUnit) {

		view.setOrgUnitTitle(orgUnit.getOrgUnitModel().getTitle() + ' ' + orgUnit.getName() + " (" + orgUnit.getFullName() + ")");

		// Banner data.
		final OrgUnitBannerDTO banner = orgUnit.getOrgUnitModel().getBanner();
		final LayoutDTO layout = banner.getLayout();

		final Widget bannerWidget;

		if (banner != null && layout != null && layout.getGroups() != null && !layout.getGroups().isEmpty()) {

			// --
			// Layout banner.
			// --

			// For visibility constraints, the banner accept a maximum of 2 rows and 4 columns.
			final int rows = layout.getRowsCount() > 2 ? 2 : layout.getRowsCount();
			final int cols = layout.getColumnsCount() > 4 ? 4 : layout.getColumnsCount();

			final HTMLTable gridLayout = view.buildBannerTable(rows, cols);
			bannerWidget = gridLayout;

			for (final LayoutGroupDTO groupLayout : layout.getGroups()) {

				// Checks group bounds.
				if (groupLayout.getRow() + 1 > rows || groupLayout.getColumn() + 1 > cols) {
					continue;
				}

				final ContentPanel groupPanel = new ContentPanel();
				groupPanel.setLayout(new FormLayout());
				groupPanel.setTopComponent(null);
				groupPanel.setHeaderVisible(false);

				gridLayout.setWidget(groupLayout.getRow(), groupLayout.getColumn(), groupPanel);

				if (groupLayout.getConstraints() == null) {
					continue;
				}

				for (final LayoutConstraintDTO constraint : groupLayout.getConstraints()) {

					final FlexibleElementDTO element = constraint.getFlexibleElementDTO();

					// Only default elements are allowed.
					if (!(element instanceof DefaultFlexibleElementDTO)) {
						continue;
					}

					// Builds the graphic component
					final DefaultFlexibleElementDTO defaultElement = (DefaultFlexibleElementDTO) element;
					defaultElement.setService(dispatch);
					defaultElement.setAuthenticationProvider(injector.getAuthenticationProvider());
					defaultElement.setCache(injector.getClientCache());
					defaultElement.setCurrentContainerDTO(orgUnit);

					dispatch.execute(new GetValue(orgUnit.getId(), defaultElement.getId(), defaultElement.getEntityName(), null),
						new CommandResultHandler<ValueResult>() {

							@Override
							public void onCommandFailure(final Throwable throwable) {
								if (Log.isErrorEnabled()) {
									Log.error("Error, element value not loaded.", throwable);
								}
								throw new RuntimeException(throwable);
							}

							@Override
							public void onCommandSuccess(final ValueResult valueResult) {

								if (Log.isDebugEnabled()) {
									Log.debug("Element value(s) object : " + valueResult);
								}

								final Component component;
								if (defaultElement instanceof BudgetElementDTO) {
									component = defaultElement.getElementComponentInBanner(valueResult);
								} else {
									component = defaultElement.getElementComponentInBanner(null);
								}

								if (component != null) {
									groupPanel.add(component);
								}

								groupPanel.layout();
							}
						});

					// Only one element per cell.
					break;
				}
			}

		} else {

			// --
			// Default banner.
			// --

			view.getOrgUnitBannerPanel().setLayout(new FormLayout());

			final LabelField codeField = new LabelField();
			codeField.setReadOnly(true);
			codeField.setFieldLabel(I18N.CONSTANTS.projectName());
			codeField.setLabelSeparator(I18N.CONSTANTS.form_label_separator());
			codeField.setValue(orgUnit.getName());

			bannerWidget = codeField;
		}

		view.setOrgUnitBanner(bannerWidget);
		view.getOrgUnitBannerPanel().layout();
	}
}
