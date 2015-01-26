package org.sigmah.client.ui.presenter.reports;

import java.io.Serializable;
import java.util.Date;
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
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.reports.ReportCreateView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Report create presenter which manages the {@link ReportCreateView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReportCreatePresenter extends AbstractPagePresenter<ReportCreatePresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ReportCreateView.class)
	public static interface View extends ViewInterface {

		FormPanel getForm();

		TextField<String> getReportTitleField();

		LabelField getElementLabelField();

		Button getSaveButton();

		Button getCancelButton();

	}

	/**
	 * The container id.
	 */
	private Integer containerId;

	/**
	 * The flexible element.
	 */
	private FlexibleElementDTO flexibleElement;

	/**
	 * The phase name.
	 */
	private String phaseName;

	/**
	 * The source item (most-likely the menu item).
	 */
	private Component sourceItem;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ReportCreatePresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.REPORT_CREATE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		view.getCancelButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hideView();
			}
		});

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// Container id parameter.
		// --

		containerId = request.getParameterInteger(RequestParameter.ID);
		if (containerId == null) {
			hideView();
			throw new IllegalArgumentException("Invalid container id.");
		}

		// --
		// Phase name parameter.
		// --

		phaseName = request.getData(RequestParameter.NAME);

		// --
		// Flexible element data.
		// --

		flexibleElement = request.getData(RequestParameter.DTO);
		if (flexibleElement == null) {
			hideView();
			throw new IllegalArgumentException("Invalid flexible element.");
		}

		// --
		// Source item.
		// --

		sourceItem = request.getData(RequestParameter.SOURCE);
		if (sourceItem == null) {
			hideView();
			throw new IllegalArgumentException("Invalid source item.");
		}

		// --
		// View reset.
		// --

		view.getForm().clearAll();
		setPageTitle(I18N.CONSTANTS.reportCreateReport());
		view.getElementLabelField().setValue(flexibleElement.getElementLabel());
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Method executed on save action.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final Map<String, Serializable> properties = new HashMap<String, Serializable>();
		properties.put(ProjectReportDTO.NAME, view.getReportTitleField().getValue());
		properties.put(ProjectReportDTO.PHASE_NAME, phaseName);
		properties.put(getIdPropertyKey(), containerId);
		properties.put(ProjectReportDTO.CONTAINER_ID, containerId);
		properties.put(ProjectReportDTO.FLEXIBLE_ELEMENT_ID, flexibleElement.getId());

		if (flexibleElement instanceof ReportElementDTO) {
			properties.put(ProjectReportDTO.REPORT_MODEL_ID, ((ReportElementDTO) flexibleElement).getModelId());

		} else if (flexibleElement instanceof ReportListElementDTO) {
			properties.put(ProjectReportDTO.REPORT_MODEL_ID, ((ReportListElementDTO) flexibleElement).getModelId());
			properties.put(ProjectReportDTO.MULTIPLE, true);
		}

		dispatch.execute(new CreateEntity(ProjectReportDTO.ENTITY_NAME, properties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				hideView();
				N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateError());
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				final ReportReference reportReference = new ReportReference();
				reportReference.setId((Integer) result.getEntity().getId());
				reportReference.setName(view.getReportTitleField().getValue());
				reportReference.setFlexibleElementLabel(flexibleElement.getElementLabel());
				reportReference.setEditorName(auth().getUserShortName());
				reportReference.setPhaseName(phaseName);
				reportReference.setLastEditDate(new Date());

				N10N.infoNotif(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateSuccess());

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.REPORT_DOCUMENTS_UPDATE, reportReference));

				if (flexibleElement instanceof ReportElementDTO) {
					sourceItem.setEnabled(false);
				}

				hideView();
			}
		}, view.getSaveButton(), view.getCancelButton());
	}

	/**
	 * Returns the appropriate {@code id} property key based on the current <em>main</em> page (not popup).
	 * 
	 * @return The appropriate {@code id} property key based on the current <em>main</em> page (not popup).
	 */
	private final String getIdPropertyKey() {

		final Page page = injector.getPageManager().getCurrentPage(false);

		switch (page) {

			case PROJECT_REPORTS:
				return ProjectReportDTO.PROJECT_ID;

			case ORGUNIT_REPORTS:
				return ProjectReportDTO.ORGUNIT_ID;

			default:
				throw new UnsupportedOperationException("Report creation is not supported from page '" + page + "'.");
		}
	}

}
