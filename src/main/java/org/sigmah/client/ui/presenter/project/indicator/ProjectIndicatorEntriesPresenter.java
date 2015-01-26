package org.sigmah.client.ui.presenter.project.indicator;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorEntriesView;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.view.pivot.ProjectPivotContainer;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Project's indicators entries presenter which manages the {@link ProjectIndicatorEntriesView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectIndicatorEntriesPresenter extends AbstractProjectPresenter<ProjectIndicatorEntriesPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectIndicatorEntriesView.class)
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
	@Inject
	public ProjectIndicatorEntriesPresenter(final View view, final Injector injector) {
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
		
		dispatch.execute(new GetProject(projectId), new CommandResultHandler<ProjectDTO>() {
			@Override
			public void onCommandSuccess(ProjectDTO project) {
				view.getProjectPivotContainer().onPageRequest(auth(), project);
			}
		}, view.getProjectPivotContainer());
	}

	@Override
	public void beforeLeaving(EventBus.LeavingCallback callback) {
		view.getProjectPivotContainer().onPageChange();
		callback.leavingOk();
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
