package org.sigmah.client.ui.widget;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Backup widget rendering a given {@link BackupDTO} status.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupStatusWidget implements IsWidget {

	// Styles.
	private static final String CSS_BACKUP_PANEL = "backup-status";
	private static final String CSS_DETAILS_PANEL = "details-panel";
	private static final String CSS_DETAILS_PANEL_LABEL = "details-panel-label";

	/**
	 * Date time formatter.
	 */
	private static final DateTimeFormat DATE_TIME_FORMATTER = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT);

	private final FlowPanel panel;

	public BackupStatusWidget() {
		panel = new FlowPanel();
		panel.setStyleName(CSS_BACKUP_PANEL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return panel;
	}

	/**
	 * Updates the backup status widget with the given {@code backupFile}.
	 * 
	 * @param backupFile
	 *          The backup file, may be {@code null}.
	 * @param downloadHandler
	 *          The click handler for backup download link (if a link is rendered).
	 */
	public void update(final BackupDTO backupFile, final ClickHandler downloadHandler) {

		panel.clear();

		final Widget labelWidget;

		if (backupFile == null) {
			labelWidget = new Label(I18N.CONSTANTS.backupManagement_status_noBackup());

		} else if (backupFile.isRunning()) {
			labelWidget = new Label(I18N.CONSTANTS.backupManagement_status_runningProcess());

		} else {
			labelWidget = new Anchor(I18N.CONSTANTS.backupManagement_status_downloadLink());
			if (downloadHandler != null) {
				((HasClickHandlers) labelWidget).addClickHandler(downloadHandler);
			}
		}

		panel.add(labelWidget);

		if (backupFile == null) {
			return;
		}

		final Grid detailsPanel = new Grid(3, 2);
		detailsPanel.setStyleName(CSS_DETAILS_PANEL);

		detailsPanel.setText(0, 0, I18N.CONSTANTS.backupManagement_status_creationDate());
		detailsPanel.getCellFormatter().setStyleName(0, 0, CSS_DETAILS_PANEL_LABEL);
		detailsPanel.setText(0, 1, DATE_TIME_FORMATTER.format(backupFile.getCreationDate()));

		detailsPanel.setText(1, 0, I18N.CONSTANTS.backupManagement_status_orgUnit());
		detailsPanel.getCellFormatter().setStyleName(1, 0, CSS_DETAILS_PANEL_LABEL);
		detailsPanel.setText(1, 1, backupFile.getOrgUnitName());

		detailsPanel.setText(2, 0, I18N.CONSTANTS.backupManagement_status_filesScope());
		detailsPanel.getCellFormatter().setStyleName(2, 0, CSS_DETAILS_PANEL_LABEL);
		detailsPanel.setText(2, 1, LoadingScope.getName(backupFile.getLoadingScope()));

		panel.add(detailsPanel);
	}
}
