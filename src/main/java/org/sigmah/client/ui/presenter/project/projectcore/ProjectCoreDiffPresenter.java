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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
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

		ListStore<AmendmentDTO> getAmendmentStor1();

		ComboBox<AmendmentDTO> getAmendmentsComboBox2();

		ListStore<AmendmentDTO> getAmendmentStor2();

		Grid<ProjectCoreDiffLigne> getProjectFields();

		ListStore<ProjectCoreDiffLigne> getProjectFieldsValueStor();

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

					for (int i = 0; i < view.getProjectFieldsValueStor().getCount(); i++) {

						final ProjectCoreDiffLigne ligne = view.getProjectFieldsValueStor().getAt(i);

						FlexibleElementDTO field = ligne.getField();

						GetValue cm = new GetValue(project.getId(), field.getId(), field.getEntityName(), amendment.getId());

						dispatch.execute(cm, new CommandResultHandler<ValueResult>() {

							@Override
							protected void onCommandSuccess(ValueResult result) {

								if (result.getValueObject() != null) {
									ligne.setValue1(result.getValueObject());
									view.getProjectFieldsValueStor().update(ligne);
								}

							}
						});

					}

					view.getProjectFieldsValueStor().commitChanges();

				}

			}
		});

		view.getAmendmentsComboBox2().addSelectionChangedListener(new SelectionChangedListener<AmendmentDTO>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<AmendmentDTO> se) {

				if (se.getSelectedItem() != null) {

					AmendmentDTO amendment = se.getSelectedItem();

					for (int i = 0; i < view.getProjectFieldsValueStor().getCount(); i++) {

						final ProjectCoreDiffLigne ligne = view.getProjectFieldsValueStor().getAt(i);

						FlexibleElementDTO field = ligne.getField();

						GetValue cm = new GetValue(project.getId(), field.getId(), field.getEntityName(), amendment.getId());

						dispatch.execute(cm, new CommandResultHandler<ValueResult>() {

							@Override
							protected void onCommandSuccess(ValueResult result) {

								if (result.getValueObject() != null) {
									ligne.setValue2(result.getValueObject());
									view.getProjectFieldsValueStor().update(ligne);
								}

							}
						});

					}

					view.getProjectFieldsValueStor().commitChanges();

				}
			}

		});

		view.getAmendmentsComboBox1().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.getAmendmentsComboBox1().reset();
			};
		});
		view.getAmendmentsComboBox2().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.getAmendmentsComboBox2().reset();
			};
		});
	}

	public boolean initView(ProjectDTO project) {

		view.getAmendmentStor1().removeAll();
		view.getAmendmentStor2().removeAll();
		view.getProjectFieldsValueStor().removeAll();

		view.getAmendmentStor1().add(project.getAmendments());
		view.getAmendmentStor2().add(project.getAmendments());

		List<ProjectCoreDiffLigne> listFieldsAmendable = new ArrayList<ProjectCoreDiffLigne>();

		for (FlexibleElementDTO field : project.getProjectModel().getAllElements()) {
			if (field.getAmendable()) {
				ProjectCoreDiffLigne ligne = new ProjectCoreDiffLigne();
				ligne.setField(field);
				listFieldsAmendable.add(ligne);
			}
		}

		view.getProjectFieldsValueStor().add(listFieldsAmendable);
		view.getProjectFieldsValueStor().commitChanges();
		view.getAmendmentStor1().commitChanges();
		view.getAmendmentStor2().commitChanges();

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
