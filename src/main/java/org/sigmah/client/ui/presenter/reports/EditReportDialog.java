package org.sigmah.client.ui.presenter.reports;

import java.io.Serializable;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.report.ProjectReportDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * EditReportDialog utility class.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class EditReportDialog {

	/**
	 * Only provides static methods.
	 */
	private EditReportDialog() {
		// Utility class constructor.
	}

	private static Dialog editReportDialog;

	private static Dialog getDialog() {
		if (editReportDialog == null) {
			final Dialog dialog = new Dialog();
			dialog.setButtons(Dialog.OKCANCEL);
			dialog.setHeadingText(I18N.CONSTANTS.reportCreateReport());
			dialog.setModal(true);

			dialog.setResizable(false);
			dialog.setWidth("340px");

			dialog.setLayout(new FormLayout());

			// Report name
			final TextField<String> nameField = new TextField<String>();
			nameField.setFieldLabel(I18N.CONSTANTS.reportName());
			nameField.setAllowBlank(false);
			nameField.setName("name");
			dialog.add(nameField);

			// Cancel button
			dialog.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(ButtonEvent ce) {
					dialog.hide();
				}
			});

			editReportDialog = dialog;
		}
		return editReportDialog;
	}

	/**
	 * Dialog used to <b>create</b> a report from outside the "Report & Documents" page.
	 * 
	 * @param properties
	 *          Base properties of the new report (should contain the report model id).
	 * @param reportButton
	 * @param registrations
	 * @param eventBus
	 *          The application event bus.
	 * @param dispatch
	 *          The application command dispatcher.
	 * @return The create report dialog.
	 */
	public static Dialog getDialog(final Map<String, Serializable> properties, final com.google.gwt.user.client.ui.Button reportButton,
			final HandlerRegistration[] registrations, final EventBus eventBus, final DispatchAsync dispatch) {

		final Dialog dialog = getDialog();

		// OK Button
		final Button okButton = dialog.getButtonById(Dialog.OK);

		okButton.removeAllListeners();
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void componentSelected(ButtonEvent ce) {
				final String name = ((TextField<String>) dialog.getWidget(0)).getValue();

				properties.put("name", name);

				dispatch.execute(new CreateEntity(ProjectReportDTO.ENTITY_NAME, properties), new CommandResultHandler<CreateResult>() {

					@Override
					public void onCommandFailure(Throwable caught) {
						N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateError());
					}

					@Override
					public void onCommandSuccess(final CreateResult result) {

						final ProjectReportDTO createdProjetReport = (ProjectReportDTO) result.getEntity();

						reportButton.setText(I18N.MESSAGES.reportOpenReport(name));
						registrations[0].removeHandler();

						reportButton.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								final PageRequest request = new PageRequest(Page.PROJECT_REPORTS);
								request.addParameter(RequestParameter.ID, properties.get("projectId"));
								request.addParameter(RequestParameter.REPORT_ID, createdProjetReport.getId());
								eventBus.navigateRequest(request);
							}
						});

						N10N.validNotif(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateSuccess());
					}
				});

				dialog.hide();
			}

		});

		return dialog;
	}

	/**
	 * Dialog used to <b>rename</b> a report from the "Report & Documents" page.
	 * 
	 * @param properties
	 *          Base properties of the report (may be empty but not null).
	 * @param dispatch
	 *          The application command dispatcher.
	 * @return The rename report dialog.
	 */
	public static Dialog getDialog(final Map<String, Serializable> properties, final Integer reportId, final DispatchAsync dispatch,
			final CommandResultHandler<VoidResult> callback) {

		final Dialog dialog = getDialog();

		// OK Button
		final Button okButton = dialog.getButtonById(Dialog.OK);

		okButton.removeAllListeners();
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@SuppressWarnings("unchecked")
			@Override
			public void componentSelected(ButtonEvent ce) {

				final String name = ((TextField<String>) dialog.getWidget(0)).getValue();

				properties.put("name", name);

				final UpdateEntity updateEntity = new UpdateEntity(ProjectReportDTO.ENTITY_NAME, reportId, (Map<String, Object>) (Map<String, ?>) properties);
				dispatch.execute(updateEntity, callback);

				dialog.hide();
			}

		});

		return dialog;
	}
}
