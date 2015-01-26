/**
 * 
 */
package org.sigmah.client.page.project.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.dashboard.EditFormWindow.FormSubmitListener;
import org.sigmah.client.util.Notification;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * The render for edit icon button in LocalPartnerProjectGrid. The button has a
 * click handler to response the On-Click event.
 * 
 * @author HUZHE(zhe.hu32@gmail.com)
 * 
 */
public class LocalPartnerProjectEditButtonGridCellRender implements GridCellRenderer<ProjectFundingDTO> {

	private final ProjectDashboardView view;
	private final Dispatcher dispatcher;
	private final ProjectPresenter projectPresenter;

	/**
	 * @param view
	 *            The view class in which the edit icon locates
	 * @param dispatcher
	 *            The dispatcher
	 * @param projectPresenter
	 *            The current project presenter
	 */

	public LocalPartnerProjectEditButtonGridCellRender(ProjectDashboardView view, Dispatcher dispatcher,
	                ProjectPresenter projectPresenter) {
		super();
		this.view = view;
		this.dispatcher = dispatcher;
		this.projectPresenter = projectPresenter;

	}

	@Override
	public Object render(final ProjectFundingDTO model, String property, ColumnData config, int rowIndex, int colIndex,
	                ListStore<ProjectFundingDTO> store, Grid<ProjectFundingDTO> grid) {

		// Create a button with a icon
		Image editIcon = IconImageBundle.ICONS.editLinkedProject().createImage();
		PushButton editButton = new PushButton(editIcon);
		editButton.setStylePrimaryName("project-linkedProject-editButton");

		// Add a click handler to this button
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				BudgetSubFieldDTO plannedBudgetField = null;
				BudgetElementDTO budgetElementDTO = null;
				// Get budget Element
				for (FlexibleElementDTO fleDTO : projectPresenter.getCurrentProjectDTO().getProjectModelDTO()
				                .getGlobalExportElements()) {
					if (fleDTO instanceof BudgetElementDTO) {
						budgetElementDTO = (BudgetElementDTO) fleDTO;
						plannedBudgetField = budgetElementDTO.getPlannedBudget();
					}
				}

				// To be accessible in the asynccallback
				final BudgetSubFieldDTO plannedBudgetFieldFound = plannedBudgetField;
				final GetValue cmd = new GetValue(projectPresenter.getCurrentProjectDTO().getId(),
				                Long.valueOf(budgetElementDTO.getId()), budgetElementDTO.getEntityName());
				dispatcher.execute(cmd, null, new AsyncCallback<ValueResult>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ValueResult result) {

						Double plannedBudget = 0d;
						Map<Integer, String> values = ValueResultUtils.splitMapElements(result.getValueObject());
						if (values.get(plannedBudgetFieldFound.getId()) != null) {
							plannedBudget = Double.valueOf(values.get(plannedBudgetFieldFound.getId()));
						}

						final Double plannedBudgetfinal = plannedBudget;

						// Create a window to edit the information
						final EditFormWindow window = new EditFormWindow();

						// Add amountField into window
						final NumberField amountField = window.addNumberField(
						                I18N.MESSAGES.projectFinancesDetails(projectPresenter.getCurrentProjectDTO()
						                                .getName()) + " (" + I18N.CONSTANTS.currencyEuro() + ')',
						                model.getPercentage(), true);

						// Add a label to signal the percentage into window
						final LabelField percentageField = window.addLabelField(I18N.CONSTANTS
						                .createProjectPercentage(), NumberUtils.ratioAsString(model.getPercentage(),
						                				plannedBudget));

						// Add a listener for the event fired when the
						// amountField's value is changed
						amountField.addListener(Events.Change, new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {

								if (amountField.getValue() == null) {
									amountField.setValue(0);
								}

								percentageField.setText(NumberUtils.ratioAsString(amountField.getValue(),
												plannedBudgetfinal));
							}
						});

						// SubmitLister, see the definition of EditFormWindow
						// for details
						window.addFormSubmitListener(new FormSubmitListener() {

							// ---------Updating
							// Handler-------------------------
							// --------------------------------------------------

							@Override
							public void formSubmitted(Object... values) {

								// Check the input
								final Object value1 = values[0];
								if (!(value1 instanceof Number)) {
									return;
								}

								// Store the properties changed
								final HashMap<String, Object> changes = new HashMap<String, Object>();
								changes.put("percentage", ((Number) value1).doubleValue());

								// Update
								dispatcher.execute(new UpdateEntity("ProjectFunding", model.getId(), changes),
								                new MaskingAsyncMonitor(view.getLocalPartnerProjectGrid(),
								                                I18N.CONSTANTS.loading()),
								                new AsyncCallback<VoidResult>() {

									                @Override
									                public void onFailure(Throwable caught) {

										                Log.error("[execute] Error while updating the linked project.",
										                                caught);
										                MessageBox.alert(
										                                I18N.CONSTANTS.linkedProjectUpdateError(),
										                                I18N.CONSTANTS.linkedProjectUpdateErrorDetails(),
										                                null);

									                }

									                @Override
									                public void onSuccess(VoidResult result) {

										                // After the RPC,update
														// the local store
														// refresh the grid
										                model.setPercentage(((Number) value1).doubleValue());

										                view.getLocalPartnerProjectGrid().getStore().update(model);

										                Notification.show(I18N.CONSTANTS.infoConfirmation(),
										                                I18N.CONSTANTS.linkedProjectUpdateConfirm());

									                }

								                });
							}

							// ---------Updating
							// End-----------------------------

							// ---------Deletion
							// Handler-------------------------
							// --------------------------------------------------

							@Override
							public void deleteModelObject() {

								// Create a listener for the confirm message box
								Listener<MessageBoxEvent> confirmListener = new Listener<MessageBoxEvent>() {
									public void handleEvent(MessageBoxEvent be) {

										Button btn = be.getButtonClicked();

										// If user clicks the Yes button,begin
										// to delete
										if (btn.getText().equals(I18N.CONSTANTS.yes())) {
											HashMap<String, Object> properties = new HashMap<String, Object>();

											if (projectPresenter.getCurrentProjectDTO().getFunded().remove(model)) {
												properties.put("fundedId", model.getId());
											} else {// Delete locally failed
												MessageBox.alert(I18N.CONSTANTS.linkedProjectUpdateError(),
												                I18N.CONSTANTS.linkedProjectUpdateErrorDetails(), null);

												return;
											}

											// RPC
											dispatcher.execute(new UpdateEntity("Project", projectPresenter
											                .getCurrentProjectDTO().getId(), properties),
											                new MaskingAsyncMonitor(view.getLocalPartnerProjectGrid(),
											                                I18N.CONSTANTS.loading()),
											                new AsyncCallback<VoidResult>() {

												                @Override
												                public void onFailure(Throwable caught) {

													                Log.error("[execute] Error while updating the linked projects.",
													                                caught);
													                MessageBox.alert(
													                                I18N.CONSTANTS.linkedProjectUpdateError(),
													                                I18N.CONSTANTS.linkedProjectUpdateErrorDetails(),
													                                null);

												                }

												                @Override
												                public void onSuccess(VoidResult result) {

													                // After
																	// RPC,
																	// refresh
																	// the view
													                view.getLocalPartnerProjectGrid().getStore()
													                                .remove(model);
													                window.hide();
													                Notification.show(
													                                I18N.CONSTANTS.infoConfirmation(),
													                                I18N.CONSTANTS.linkedProjectUpdateConfirm());

												                }

											                });

										}

									}
								};

								// Create a confirm messagebox with the listener
								MessageBox confirmMessageBox = MessageBox.confirm(I18N.CONSTANTS.deleteConfirm(),
								                I18N.CONSTANTS.deleteConfirmMessage(), confirmListener);
								confirmMessageBox.setButtons(MessageBox.YESNO);
								((Button) confirmMessageBox.getDialog().getButtonBar().getItem(0))
								                .setText(I18N.CONSTANTS.yes());
								((Button) confirmMessageBox.getDialog().getButtonBar().getItem(1))
								                .setText(I18N.CONSTANTS.no());
								confirmMessageBox.setIcon(MessageBox.WARNING);
								confirmMessageBox.show();

							}

							// ---------Deletion
							// End-----------------------------

						});

						// Show the edit window.
						window.show(I18N.CONSTANTS.createProjectTypePartner(),
						                I18N.CONSTANTS.createProjectPartnerProjectEditDetails() + " '"
						                                + projectPresenter.getCurrentProjectDTO().getName() + "'.");

					}
				});
			}
		};

		editButton.addClickHandler(handler);

		// Return this editButton
		return editButton;
	}

}
