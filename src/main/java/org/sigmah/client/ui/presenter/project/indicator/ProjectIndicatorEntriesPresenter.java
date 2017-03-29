package org.sigmah.client.ui.presenter.project.indicator;

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


import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorEntriesView;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.view.pivot.ProjectPivotContainer;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

/**
 * Project's indicators entries presenter which manages the {@link ProjectIndicatorEntriesView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectIndicatorEntriesPresenter extends AbstractProjectPresenter<ProjectIndicatorEntriesPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends AbstractProjectPresenter.View {
		ProjectPivotContainer getProjectPivotContainer();
	}
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	public ProjectIndicatorEntriesPresenter(final View view, final ClientFactory injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_INDICATORS_ENTRIES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		final SplitButton saveButton = view.getProjectPivotContainer().getSaveButton();
		final MenuItem saveItem = (MenuItem) saveButton.getMenu().getItem(0);
		final MenuItem discardChangesItem = (MenuItem) saveButton.getMenu().getItem(1);
		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent event) {
				onSave();
			}
		});
		
		saveItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent event) {
				onSave();
			}
		});
		
		discardChangesItem.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent event) {
				onDiscard();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		final Integer projectId = request.getParameterInteger(RequestParameter.ID);
		
		final boolean canEditIndicator = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_INDICATOR);
		final boolean canManageIndicator = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MANAGE_INDICATOR);
		
		view.getProjectPivotContainer().getSaveButton().setVisible(canEditIndicator);
		view.getProjectPivotContainer().getSaveButtonSeparator().setVisible(canEditIndicator);
		
		view.getProjectPivotContainer().getGridPanel().setHeaderDecoratorEditable(canManageIndicator);
		
		dispatch.execute(new GetProject(projectId), new CommandResultHandler<ProjectDTO>() {
			@Override
			public void onCommandSuccess(ProjectDTO project) {
				view.getProjectPivotContainer().onPageRequest(auth(), project);
			}
		}, view.getProjectPivotContainer());
	}

	@Override
	protected boolean hasValueChanged() {
		return view.getProjectPivotContainer().hasValueChanged();
	}
	
	@Override
	public void beforeLeaving(EventBus.LeavingCallback callback) {
		view.getProjectPivotContainer().onPageChange();
		super.beforeLeaving(callback);
	}

	private void onSave() {
		view.getProjectPivotContainer().save();
		view.getProjectPivotContainer().setUpdated(false);
	}

	private void onDiscard() {
		view.getProjectPivotContainer().discard();
		view.getProjectPivotContainer().setUpdated(false);
	}
}
