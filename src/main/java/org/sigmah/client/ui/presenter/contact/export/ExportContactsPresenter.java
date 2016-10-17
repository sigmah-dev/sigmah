package org.sigmah.client.ui.presenter.contact.export;

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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.contact.export.ExportContactsView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetGlobalContactExports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.GlobalContactExportDTO;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ExportUtils.ExportDataVersion;

@Singleton
public class ExportContactsPresenter extends AbstractPagePresenter<ExportContactsPresenter.View> {

	@ImplementedBy(ExportContactsView.class)
	public static interface View extends ViewPopupInterface {

		Button getSettingsButton();

		Button getExportButton();

		Button getSearchButton();

		DateField getFromDate();

		DateField getToDate();

		ListStore<GlobalContactExportDTO> getPeriodsStore();

		ComboBox<GlobalContactExportDTO> getPeriods();

		Radio getLiveChoice();

		Radio getBackupChoice();

	}

	@Inject
	protected ExportContactsPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public void onBind() {

		// Search
		view.getSearchButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (view.getFromDate().isValid() && view.getToDate().isValid()) {
					view.getPeriods().setEmptyText("");
					dispatch.execute(new GetGlobalContactExports(view.getFromDate().getValue(), view.getToDate().getValue(), I18N.CONSTANTS.savedDateExportFormat()),
						new CommandResultHandler<ListResult<GlobalContactExportDTO>>() {

							@Override
							public void onCommandFailure(Throwable caught) {
								view.getPeriods().setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
							}

							@Override
							public void onCommandSuccess(ListResult<GlobalContactExportDTO> result) {
								view.getPeriodsStore().removeAll();
								if (result != null) {
									if (result.getList().size() > 0) {
										view.getPeriods().setEmptyText(I18N.CONSTANTS.createProjectTypeFundingSelect());
										view.getPeriodsStore().add(result.getList());
										view.getPeriodsStore().commitChanges();
										view.getPeriods().setEnabled(true);
									} else {
										N10N.warn("No Backup Export Found");
									}

								}
							}
						});
				}
			}
		});

		// Export
		view.getExportButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				final ServletUrlBuilder urlBuilder =
						new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_CONTACT_GLOBAL);

				urlBuilder.addParameter(RequestParameter.ID, auth().getOrganizationId());

				if (view.getLiveChoice().getValue()) {

					urlBuilder.addParameter(RequestParameter.VERSION, ExportDataVersion.LIVE_DATA);
					ClientUtils.launchDownload(urlBuilder.toString());
					view.hide();

				} else {
					if (view.getPeriods().getValue() != null) {

						urlBuilder.addParameter(RequestParameter.VERSION, ExportDataVersion.BACKED_UP_DATA);
						urlBuilder.addParameter(RequestParameter.GLOBAL_EXPORT_ID, view.getPeriods().getValue().getId());
						ClientUtils.launchDownload(urlBuilder.toString());
						view.hide();
					} else {
						N10N.warn("No Selected Backup Export");
					}
				}

			}
		});

		// Change Setting
		view.getSettingsButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				eventBus.navigate(Page.CONTACT_EXPORTS_SETTING);
			};
		});

	}

	@Override
	public Page getPage() {
		return Page.CONTACT_EXPORTS;
	}

	@Override
	public void onPageRequest(PageRequest request) {
		// view.cleanForm();
		setPageTitle(I18N.CONSTANTS.contactsExport());

	}
}
