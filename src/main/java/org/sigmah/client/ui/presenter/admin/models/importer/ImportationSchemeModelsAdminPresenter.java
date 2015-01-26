package org.sigmah.client.ui.presenter.admin.models.importer;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.importer.ImportationSchemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class ImportationSchemeModelsAdminPresenter<E extends IsModel> extends AbstractPresenter<ImportationSchemeModelsAdminPresenter.View>
																																																																						implements
																																																																						IsModelTabPresenter<E, ImportationSchemeModelsAdminPresenter.View> {

	private E currentModelDTO;

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ImportationSchemeModelsAdminView.class)
	public static interface View extends ViewInterface {

		ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore();

		ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementStore();

		Grid<ImportationSchemeModelDTO> getImportationSchemeModelsGrid();

		Grid<VariableFlexibleElementDTO> getVariableFlexibleElementsGrid();

		Button getAddVariableFlexibleElementButton();

		Button getDeleteVariableFlexibleElementButton();

		Button getAddImportationSchemeModelButton();

		Button getDeleteImportationSchemeModelButton();

	}

	@Inject
	protected ImportationSchemeModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminImportationSchemes();
	}

	@Override
	public boolean hasValueChanged() {
		return false;
	}

	@Override
	public void loadTab(E model) {

		currentModelDTO = model;

		// view.setToolbarEnabled(currentOrgUnitModelDTO.getStatus() == ProjectModelStatus.DRAFT);

	}

	@Override
	public void onBind() {

		// ADD IMPORTATION SCHEME MODEL

		view.getAddImportationSchemeModelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL.request().addData(RequestParameter.DTO, currentModelDTO));
			}
		});

		// DELETE IMPORTATION SCHEME MODEL

		view.getDeleteImportationSchemeModelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO
			}
		});

		// ADD MATCHING RULE

		view.getAddVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				eventBus.navigateRequest(Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE.request().addData(RequestParameter.DTO, currentModelDTO));

			}
		});

		// DELETE MATCHING RULE

		view.getDeleteVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO
			}
		});
	}
}
