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
import org.sigmah.client.ui.view.project.projectcore.ProjectCoreDiffLigne;
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

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class ProjectCoreDiffPresenter extends AbstractPagePresenter<ProjectCoreDiffPresenter.View> {

	private ProjectDTO project;

	private boolean show = true;

	@ImplementedBy(ProjectCoreDiffView.class)
	public static interface View extends ViewPopupInterface {

		ContentPanel getMainPanel();

		ComboBox<AmendmentDTO> getAmendmentsComboBox1();

		ListStore<AmendmentDTO> getAmendmentStore1();

		ComboBox<AmendmentDTO> getAmendmentsComboBox2();

		ListStore<AmendmentDTO> getAmendmentStore2();

		Grid<ProjectCoreDiffLigne> getProjectFields();

		ListStore<ProjectCoreDiffLigne> getProjectFieldsValueStore();

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
	public void onPageRequest(PageRequest request) {

		setPageTitle(I18N.CONSTANTS.projectCoreCompareVersion());

		project = request.getData(RequestParameter.DTO);

		if (project.getAmendments().size() <= 0) {
			show = false;
			N10N.info(I18N.MESSAGES.amendmentCompareNoValue());
		}

		if (initView(project) == false) {
			show = false;
			N10N.info(I18N.MESSAGES.amendmentCompareNoFields());
		}

	}

	@Override
	public void onBind() {

		view.getAmendmentsComboBox1().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {

				if (se.getSelectedItem() != null) {

					AmendmentDTO amendment = se.getSelectedItem();

					for (int i = 0; i < view.getProjectFieldsValueStore().getCount(); i++) {

						final ProjectCoreDiffLigne ligne = view.getProjectFieldsValueStore().getAt(i);

						FlexibleElementDTO field = ligne.getField();

						GetValue cm = new GetValue(project.getId(), field.getId(), field.getEntityName(), amendment.getId());

						dispatch.execute(cm, new CommandResultHandler<ValueResult>() {

							@Override
							protected void onCommandSuccess(ValueResult result) {

								if (result.getValueObject() != null) {
									ligne.setValue1(result.getValueObject());
									view.getProjectFieldsValueStore().update(ligne);
								}

							}
						});

					}

					view.getProjectFieldsValueStore().commitChanges();

				}

			}
		});

		view.getAmendmentsComboBox2().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {

				if (se.getSelectedItem() != null) {

					AmendmentDTO amendment = se.getSelectedItem();

					for (int i = 0; i < view.getProjectFieldsValueStore().getCount(); i++) {

						final ProjectCoreDiffLigne ligne = view.getProjectFieldsValueStore().getAt(i);

						FlexibleElementDTO field = ligne.getField();

						GetValue cm = new GetValue(project.getId(), field.getId(), field.getEntityName(), amendment.getId());

						dispatch.execute(cm, new CommandResultHandler<ValueResult>() {

							@Override
							protected void onCommandSuccess(ValueResult result) {

								if (result.getValueObject() != null) {
									ligne.setValue2(result.getValueObject());
									view.getProjectFieldsValueStore().update(ligne);
								}

							}
						});

					}

					view.getProjectFieldsValueStore().commitChanges();

				}
			}

		});

	}

	public boolean initView(ProjectDTO project) {

		view.getAmendmentStore1().removeAll();
		view.getAmendmentStore2().removeAll();
		view.getProjectFieldsValueStore().removeAll();

		view.getAmendmentStore1().add(project.getAmendments());
		view.getAmendmentStore1().add(new AmendmentDTO(project));
		view.getAmendmentStore2().add(project.getAmendments());
		view.getAmendmentStore2().add(new AmendmentDTO(project));

		List<ProjectCoreDiffLigne> listFieldsAmendable = new ArrayList<ProjectCoreDiffLigne>();

		for (FlexibleElementDTO field : project.getProjectModel().getAllElements()) {
			if (field.getAmendable()) {
				ProjectCoreDiffLigne ligne = new ProjectCoreDiffLigne();
				ligne.setField(field);
				listFieldsAmendable.add(ligne);
			}
		}

		view.getProjectFieldsValueStore().add(listFieldsAmendable);
		view.getProjectFieldsValueStore().commitChanges();
		view.getAmendmentStore1().commitChanges();
		view.getAmendmentStore2().commitChanges();

		if (listFieldsAmendable.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	protected void onViewRevealed() {

		if (!show) {
			view.hide();
		}

	}
}
