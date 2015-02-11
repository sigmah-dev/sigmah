package org.sigmah.client.ui.presenter.project.projectcore;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.project.projectcore.DiffEntry;
import org.sigmah.client.ui.view.project.projectcore.ProjectCoreDiffView;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;

/**
 * Manages the diff view between core versions.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectCoreDiffPresenter extends AbstractPagePresenter<ProjectCoreDiffPresenter.View> {
	
	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	private ProjectDTO project;

	private boolean show = true;

	@ImplementedBy(ProjectCoreDiffView.class)
	public static interface View extends ViewPopupInterface {

		ContentPanel getMainPanel();

		ComboBox<AmendmentDTO> getAmendmentsComboBox1();

		ListStore<AmendmentDTO> getAmendmentStore1();

		ComboBox<AmendmentDTO> getAmendmentsComboBox2();

		ListStore<AmendmentDTO> getAmendmentStore2();

		Grid<DiffEntry> getProjectFields();

		ListStore<DiffEntry> getProjectFieldsValueStore();

	}

	@Inject
	protected ProjectCoreDiffPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.PROJECT_AMENDMENT_DIFF;
	}

	@Override
	public void onBind() {

		view.getAmendmentsComboBox1().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {
				loadCoreVersion(se, LEFT);
			}
		});

		view.getAmendmentsComboBox2().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {
				loadCoreVersion(se, RIGHT);
			}

		});

	}
	
	@Override
	public void onPageRequest(PageRequest request) {

		setPageTitle(I18N.CONSTANTS.projectCoreCompareVersion());

		project = request.getData(RequestParameter.DTO);

		if (project.getAmendments().isEmpty()) {
			show = false;
			N10N.info(I18N.MESSAGES.amendmentCompareNoValue());
		}

		if (initView(project) == false) {
			show = false;
			N10N.info(I18N.MESSAGES.amendmentCompareNoFields());
		}

	}

	public boolean initView(ProjectDTO project) {

		view.getAmendmentStore1().removeAll();
		view.getAmendmentStore2().removeAll();
		view.getProjectFieldsValueStore().removeAll();
		
		view.getAmendmentsComboBox1().setValue(null);
		view.getAmendmentsComboBox2().setValue(null);
		
		final AmendmentDTO projectAmendment = new AmendmentDTO(project);

		view.getAmendmentStore1().add(project.getAmendments());
		view.getAmendmentStore1().add(projectAmendment);
		view.getAmendmentStore2().add(project.getAmendments());
		view.getAmendmentStore2().add(projectAmendment);

		List<DiffEntry> listFieldsAmendable = new ArrayList<DiffEntry>();

		for (FlexibleElementDTO field : project.getProjectModel().getAllElements()) {
			if (field.getAmendable()) {
				DiffEntry ligne = new DiffEntry();
				ligne.setField(field);
				listFieldsAmendable.add(ligne);
			}
		}

		view.getProjectFieldsValueStore().add(listFieldsAmendable);
		view.getProjectFieldsValueStore().commitChanges();
		view.getAmendmentStore1().commitChanges();
		view.getAmendmentStore2().commitChanges();
		
		if(!project.getAmendments().isEmpty()) {
			view.getAmendmentsComboBox1().setValue(project.getAmendments().get(project.getAmendments().size() - 1));
		}
		view.getAmendmentsComboBox2().setValue(projectAmendment);

		return !listFieldsAmendable.isEmpty();
	}
	
	private void loadCoreVersion(SelectionChangedEvent<AmendmentDTO> event, final int position) {
		if (event.getSelectedItem() != null) {
			final AmendmentDTO coreVersion = event.getSelectedItem();

			final BatchCommand batchCommand = new BatchCommand();
			
			for (int index = 0; index < view.getProjectFieldsValueStore().getCount(); index++) {
				final DiffEntry entry = view.getProjectFieldsValueStore().getAt(index);
				final FlexibleElementDTO field = entry.getField();

				batchCommand.add(new GetValue(project.getId(), field.getId(), 
					field.getEntityName(), coreVersion.getId()));
			}
			
			dispatch.execute(batchCommand, new CommandResultHandler<ListResult<Result>>() {

				@Override
				protected void onCommandSuccess(ListResult<Result> results) {
					for (int index = 0; index < view.getProjectFieldsValueStore().getCount(); index++) {
						final ValueResult result = (ValueResult) results.getList().get(index);
						final DiffEntry entry = view.getProjectFieldsValueStore().getAt(index);
						
						entry.setValue(position, result.getValueObject());
						view.getProjectFieldsValueStore().update(entry);
					}
				}
			});
		}
	}

	@Override
	protected void onViewRevealed() {

		if (!show) {
			view.hide();
		}

	}
}
