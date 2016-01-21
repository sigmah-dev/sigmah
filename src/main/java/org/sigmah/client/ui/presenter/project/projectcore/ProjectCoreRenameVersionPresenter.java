package org.sigmah.client.ui.presenter.project.projectcore;

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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.project.projectcore.ProjectCoreRenameVersionView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.ProjectDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class ProjectCoreRenameVersionPresenter extends AbstractPagePresenter<ProjectCoreRenameVersionPresenter.View> {

	private boolean show = true;

	@ImplementedBy(ProjectCoreRenameVersionView.class)
	public static interface View extends ViewPopupInterface {

		ContentPanel getMainPanel();

		Button getSaveButton();

		Button getCanncelButton();

		Grid<AmendmentDTO> getGridContentAmendment();

		ListStore<AmendmentDTO> getAmendmentStore();

	}

	@Inject
	protected ProjectCoreRenameVersionPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.PROJECT_AMENDMENT_RENAME;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		setPageTitle(I18N.CONSTANTS.projectCoreEditVersionName());

		ProjectDTO project = request.getData(RequestParameter.DTO);

		if (project.getAmendments().size() <= 0) {
			show = false;
			N10N.info(I18N.MESSAGES.amendmentCompareNoValue());
		}

		loadAmendment(project);

	}

	public void loadAmendment(ProjectDTO project) {

		view.getAmendmentStore().removeAll();

		view.getAmendmentStore().add(project.getAmendments());

		view.getAmendmentStore().commitChanges();

	}

	@Override
	public void onBind() {

		view.getCanncelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.hide();
			};
		});

		view.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				updateProjectsCorName();
			}
		});

	}

	public void updateProjectsCorName() {

		for (int i = 0; i < view.getAmendmentStore().getCount(); i++) {

			final AmendmentDTO amendmentDTO = view.getAmendmentStore().getAt(i);

			Map<String, Object> properties = new HashMap<String, Object>();

			properties.put("name", amendmentDTO.getName());

			UpdateEntity cm = new UpdateEntity(AmendmentDTO.ENTITY_NAME, amendmentDTO.getId(), properties);

			dispatch.execute(cm, new CommandResultHandler<VoidResult>() {

				@Override
				protected void onCommandFailure(Throwable caught) {
					N10N.error(I18N.MESSAGES.renameAmenmdmentError() + " : " + amendmentDTO.getName());
				}

				@Override
				protected void onCommandSuccess(VoidResult result) {
					N10N.infoNotif(I18N.MESSAGES.renameAmedmentSuccess() + " : " + amendmentDTO.getName());
				}
			});

		}

		eventBus.fireEvent(new UpdateEvent(UpdateEvent.AMENDMENT_RENAME));
		view.hide();
	}

	@Override
	protected void onViewRevealed() {
		if (!show) {
			view.hide();
		}
	}
}
