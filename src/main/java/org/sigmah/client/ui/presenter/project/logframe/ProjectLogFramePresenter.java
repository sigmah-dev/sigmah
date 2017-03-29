package org.sigmah.client.ui.presenter.project.logframe;

import org.sigmah.client.ClientFactory;

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


import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.presenter.project.logframe.ConfirmPasteDialog.ConfirmPasteDialogCallback;
import org.sigmah.client.ui.view.project.logframe.ProjectLogFrameGrid;
import org.sigmah.client.ui.view.project.logframe.ProjectLogFrameView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CopyLogFrame;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.IndicatorCopyStrategy;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ProfileUtils;
import org.sigmah.shared.util.ProjectUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Project's log frame presenter which manages the {@link ProjectLogFrameView}.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ProjectLogFramePresenter extends AbstractProjectPresenter<ProjectLogFramePresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends AbstractProjectPresenter.View {

		Component getMainPanel();

		ProjectLogFrameGrid getLogFrameGrid();

		Button getSaveButton();

		Button getCopyButton();

		Button getPasteButton();

		Button getExportButton();

		Field<Object> getLogFrameTitleField();

		Field<String> getLogFrameMainObjectiveField();

	}

	/**
	 * The label used for the no-name groups.
	 */
	public static final String DEFAULT_GROUP_LABEL = "-";

	/**
	 * LogFrame id used in copy/paste process.
	 */
	private static Integer logFrameIdCopySource;

	/**
	 * Has current log frame been updated?
	 */
	private boolean logFrameUpdated;

	/**
	 * The displayed log frame.
	 */
	private LogFrameDTO logFrame;

	/**
	 * Presenters's initialization.
	 *
	 * @param view
	 *          Presenter's view interface.
	 * @param factory
	 *          Injected client injector.
	 */
	public ProjectLogFramePresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_LOGFRAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Enable the save button when the log frame is edited.
		view.getLogFrameGrid().addListener(new ProjectLogFrameGrid.LogFrameGridListener() {

			@Override
			public void logFrameEdited() {
				logFrameUpdated = true;
				view.getSaveButton().setEnabled(true);
			}
		});

		// Log frame main objective box listener.
		view.getLogFrameMainObjectiveField().addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {

				if (logFrame == null) {
					return;
				}

				logFrame.setMainObjective(view.getLogFrameMainObjectiveField().getValue());
				logFrameUpdated = true;
				view.getSaveButton().setEnabled(true);
			}
		});

		// Save action.
		view.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				onSaveAction();
			}
		});

		// Copy action.
		view.getCopyButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				onCopyAction();
			}
		});

		// Paste action.
		view.getPasteButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				onPasteAction();
			}
		});

		// Excel export action.
		view.getExportButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(final ButtonEvent be) {

				final ServletUrlBuilder urlBuilder =
						new ServletUrlBuilder(factory.getAuthenticationProvider(), factory.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_PROJECT_LOGFRAME);

				urlBuilder.addParameter(RequestParameter.ID, getProject().getId());

				ClientUtils.launchDownload(urlBuilder.toString());
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		if(getProject().getCurrentAmendment() != null) {
			logFrame = getProject().getCurrentAmendment().getLogFrame();
		} else {
			logFrame = getProject().getLogFrame();
		}

		fillAndInit();

		logFrameUpdated = false; // Must be set after 'fillAndInit()' execution.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean hasValueChanged() {
		return logFrameUpdated;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns if the current {@link #logFrame} is editable or not.
	 *
	 * @return {@code true} if the current {@link #logFrame} is editable.
	 */
	private boolean isEditable() {
		return logFrame != null
			&& getProject().getAmendmentState() == AmendmentState.DRAFT
			&& getProject().getCurrentAmendment() == null
			&& ProjectUtils.isProjectEditable(getProject(), auth())
			&& ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_LOGFRAME);
	}

	/**
	 * Fills the view with the current log frame and initializes buttons state.
	 */
	private void fillAndInit() {

		// Fill the log frame title with the project's title.
		view.getLogFrameTitleField().setValue(getProject().getFullName());

		if (logFrame != null) {
			// Fill the log frame main objective.
			view.getLogFrameMainObjectiveField().setValue(logFrame.getMainObjective());

			final boolean editable = isEditable();

			if (!editable) {
				view.getLogFrameMainObjectiveField().setEnabled(false);
			}

			// Fill the grid.
			view.getLogFrameGrid().displayLogFrame(getProject().getId(), logFrame, editable);
		}

		// Default buttons states.
		view.getSaveButton().setEnabled(false);
		view.getCopyButton().setEnabled(true);
		view.getPasteButton().setEnabled(isEditable() && logFrameIdCopySource != null && getProject().getCurrentAmendment() == null);
	}

	/**
	 * Method executed on copy button action.
	 */
	private void onCopyAction() {

		logFrameIdCopySource = logFrame.getId();
		view.getPasteButton().setEnabled(isEditable() && getProject().getCurrentAmendment() == null);

		N10N.notification(I18N.CONSTANTS.copy(), I18N.CONSTANTS.logFrameCopied(), MessageType.INFO);
	}

	/**
	 * Method executed on paste button action.
	 */
	private void onPasteAction() {

		new ConfirmPasteDialog(new ConfirmPasteDialogCallback() {

			@Override
			public void onOk(final boolean linkIndicatorsChecked) {

				final CopyLogFrame copyLogFrame =
						CopyLogFrame.from(logFrameIdCopySource).to(getProject())
							.with(linkIndicatorsChecked ? IndicatorCopyStrategy.DUPLICATE_AND_LINK : IndicatorCopyStrategy.DUPLICATE);

				dispatch.execute(copyLogFrame, new CommandResultHandler<LogFrameDTO>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.warn(I18N.CONSTANTS.paste(), I18N.CONSTANTS.logFramePasteError());
					}

					@Override
					public void onCommandSuccess(final LogFrameDTO result) {
						logFrame = result;
						getProject().setLogFrame(result);

						fillAndInit();

						N10N.notification(I18N.CONSTANTS.paste(), I18N.CONSTANTS.logFramePasted(), MessageType.INFO);
					}

				});
			}
		}).show();
	}

	/**
	 * Method executed on save button action.
	 */
	private void onSaveAction() {

		// Logs the modified log frame.
		if (Log.isDebugEnabled()) {
			Log.debug("Merges the following log frame: " + logFrame.toString());
		}

		// Sends the merge action to the server.
		dispatch.execute(new UpdateLogFrame(logFrame, getProject().getId()), new CommandResultHandler<LogFrameDTO>() {

			@Override
			public void onCommandFailure(final Throwable e) {

				if (Log.isErrorEnabled()) {
					Log.error("Error when saving the log frame.", e);
				}

				// Informs of the error.
				N10N.warn(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
			}

			@Override
			public void onCommandSuccess(final LogFrameDTO updated) {

				if (Log.isDebugEnabled()) {
					Log.debug("Log frame successfully saved.");
				}

				// Updates local entities with the new returned log frame (to get the generated ids).
				getProject().setLogFrame(updated);
				view.getLogFrameGrid().updateLogFrame(updated);
				logFrame = updated;

				// Informs of the success.
				N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.saveConfirm(), MessageType.INFO);
				logFrameUpdated = false;
				view.getSaveButton().setEnabled(false);

				// TODO [INDICATORS] A major redesign of the indicators is in progress...
				// broadcast an indicator change event to be safe
				// eventBus.fireEvent(new IndicatorEvent(IndicatorEvent.CHANGED, ProjectLogFramePresenter.this));
			}
		}, new LoadingMask(view.getMainPanel()));
	}

}
