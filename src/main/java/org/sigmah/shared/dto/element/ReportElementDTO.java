package org.sigmah.shared.dto.element;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.reports.EditReportDialog;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.form.WidgetField;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

/**
 * Field that can hold a project report.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8861816666675419305L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.ReportElement";

	// DTO attributes keys.
	public static final String MODEL_ID = "modelId";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(MODEL_ID, getModelId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		final Button button = new Button();
		final WidgetField<Button, String> field = new WidgetField<Button, String>(button);

		if (valueResult != null && valueResult.isValueDefined()) {
			// If a report is attached to this element
			button.setText(I18N.CONSTANTS.reportOpenReport());

			final String reportId = valueResult.getValueObject();

			// Retrieving the name of the report attached to this element
			final GetProjectReports getProjectReports = new GetProjectReports();
			getProjectReports.setReportId(Integer.parseInt(reportId));

			dispatch.execute(getProjectReports, new CommandResultHandler<ListResult<ReportReference>>() {

				@Override
				public void onCommandSuccess(final ListResult<ReportReference> result) {

					final List<ReportReference> results = result.getList();

					if (results.size() == 1) {
						button.setText(I18N.MESSAGES.reportOpenReport(results.get(0).getName()));
					}
				}
			});

			// Report & Document path
			final PageRequest request;

			if (currentContainerDTO instanceof ProjectDTO) {
				// This element is displayed in a project
				request = new PageRequest(Page.PROJECT_REPORTS);
				request.addParameter(RequestParameter.ID, currentContainerDTO.getId());
				request.addParameter(RequestParameter.REPORT_ID, reportId);

			} else if (currentContainerDTO instanceof OrgUnitDTO) {
				request = new PageRequest(Page.ORGUNIT_REPORTS);
				request.addParameter(RequestParameter.ID, currentContainerDTO.getId());
				request.addParameter(RequestParameter.REPORT_ID, reportId);

			} else {
				if (Log.isDebugEnabled()) {
					Log.debug("ReportElementDTO does not know how to render properly from a '" + currentContainerDTO.getClass() + "' container.");
				}
				request = null;
			}

			button.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					eventBus.navigateRequest(request);
				}
			});

		} else {
			// New report button
			final IconImageBundle imageBundle = GWT.create(IconImageBundle.class);

			final Image addIcon = imageBundle.add().createImage();
			addIcon.getElement().getStyle().setVerticalAlign(VerticalAlign.TEXT_TOP);
			button.setHTML(addIcon.getElement().getString() + ' ' + I18N.CONSTANTS.reportCreateReport());

			// Report & Document path
			if (currentContainerDTO instanceof ProjectDTO) {
				// This element is displayed in a project
				final ProjectDTO projectDTO = (ProjectDTO) currentContainerDTO;

				final HandlerRegistration[] registrations = new HandlerRegistration[1];

				registrations[0] = button.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						final HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
						properties.put(ProjectReportDTO.FLEXIBLE_ELEMENT_ID, getId());
						// BUGFIX: Fixed regression, issue #696
						properties.put(ProjectReportDTO.CONTAINER_ID, currentContainerDTO.getId());
						properties.put(ProjectReportDTO.REPORT_MODEL_ID, getModelId());
						properties.put(ProjectReportDTO.PHASE_NAME, projectDTO.getCurrentPhase().getPhaseModel().getName());
						properties.put(ProjectReportDTO.PROJECT_ID, projectDTO.getId());

						EditReportDialog.getDialog(properties, button, registrations, eventBus, dispatch).show();
					}
				});

			} else {
				if (currentContainerDTO instanceof OrgUnitDTO) {
					final OrgUnitDTO orgUnitDTO = (OrgUnitDTO) currentContainerDTO;
					final HandlerRegistration[] registrations = new HandlerRegistration[1];

					registrations[0] = button.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							final HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
							properties.put(ProjectReportDTO.FLEXIBLE_ELEMENT_ID, getId());
							properties.put(ProjectReportDTO.CONTAINER_ID, currentContainerDTO.getId());
							properties.put(ProjectReportDTO.REPORT_MODEL_ID, getModelId());
							properties.put(ProjectReportDTO.PHASE_NAME, null);
							properties.put(ProjectReportDTO.ORGUNIT_ID, orgUnitDTO.getId());

							EditReportDialog.getDialog(properties, button, registrations, eventBus, dispatch).show();
						}
					});
				} else {
					Log.debug("ReportElementDTO does not know how to render properly from the '" + History.getToken() + "' page.");
				}
			}

			field.setEnabled(enabled);
		}

		field.setFieldLabel(getLabel());

		return field;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		return result != null && result.isValueDefined();
	}

	public Integer getModelId() {
		return get(MODEL_ID);
	}

	public void setModelId(Integer modelId) {
		set(MODEL_ID, modelId);
	}

}
