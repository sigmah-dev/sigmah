package org.sigmah.offline.view;

import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import java.util.Date;
import java.util.EnumMap;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.offline.status.ProgressType;

/**
 * Popup menu to manage the offline mode.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineMenuPanel extends Composite {
	
	private static final String MENU_ID = "offline-menu";
	private static final String ACTION_STYLE = "offline-action";
	private static final String UPDATE_DATABASE_ACTION_STYLE = "update-database";
	private static final String TRANSFER_FILES_ACTION_STYLE = "transfert-files";
	private static final String REMOVE_DATABASE_ACTION_STYLE = "delete-database";
	private static final String VARIABLE_STYLE = "offline-menu-variable";
	private static final String SEPARATOR_STYLE = "offline-action-separator";
	private static final String PROGRESS_BAR_STYLE = "offline-menu-progress";
	private static final String PROGRESS_LABEL_STYLE = "offline-menu-progress-label";
    
    private InlineLabel sigmahUpdateVariable;
    
    private InlineLabel databaseUpdateVariable;
    private Anchor updateDatabaseAnchor;
    
	private InlineLabel pendingUploadsVariable;
	private InlineLabel pendingDownloadsVariable;
    private Anchor transferFilesAnchor;
    
    private Anchor removeOfflineDataAnchor;
    
    private final EnumMap<ProgressType, RatioBar> bars = new EnumMap(ProgressType.class);
    private final EnumMap<ProgressType, FlowPanel> wraps = new EnumMap(ProgressType.class);
    
    private Widget[] fileBaseWidgets;

	/**
	 * Creates a new menu.
	 */
	public OfflineMenuPanel() {
		initWidget(createPanel());
	}

	/**
	 * Retrieves the link to update the database.
	 * 
	 * @return The link to update the database.
	 */
    public Anchor getUpdateDatabaseAnchor() {
        return updateDatabaseAnchor;
    }

	/**
	 * Retrieves the link to transfer files.
	 * 
	 * @return The link to transfer files.
	 */
    public Anchor getTransferFilesAnchor() {
        return transferFilesAnchor;
    }

	/**
	 * Retrieves the link to remove the current offline database.
	 * 
	 * @return The link to remove the current offline database.
	 */
    public Anchor getRemoveOfflineDataAnchor() {
        return removeOfflineDataAnchor;
    }

	/**
	 * Retrieves the progress bar associated with the given type.
	 * 
	 * @param progressType Type of loadable element.
	 * @return The associated progress bar.
	 */
    public RatioBar getBar(ProgressType progressType) {
        return bars.get(progressType);
    }
	
	/**
	 * Show or hide the progress bar associated with the given type.
	 * 
	 * @param progressType Type of loadable element.
	 * @param visible <code>true</code> to show the bar, <code>false</code> to hide it.
	 */
	public void setBarVisible(ProgressType progressType, boolean visible) {
		if(visible) {
			wraps.get(progressType).getElement().getStyle().clearDisplay();
		} else {
			wraps.get(progressType).getElement().getStyle().setDisplay(Style.Display.NONE);
		}
	}
    
    public void setSigmahUpdateDate(Date sigmahUpdateDate) {
        setUpdateDate(sigmahUpdateDate, sigmahUpdateVariable);
    }
    
    public void setDatabaseUpdateDate(Date databaseUpdateDate) {
        setUpdateDate(databaseUpdateDate, databaseUpdateVariable);
    }
    
    private void setUpdateDate(Date date, InlineLabel label) {
        if(date != null) {
            label.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(date));
        } else {
            label.setText(I18N.CONSTANTS.offlineDatabaseLastCompleteUpdateNever());
        }
    }
    
	public void setPendingsTransfers(ProgressType progressType, int pending) {
		switch(progressType) {
			case DOWNLOAD:
				setPendingDownloads(pending);
				break;
			case UPLOAD:
				setPendingUploads(pending);
				break;
		}
	}
	
    public void setPendingUploads(int pending) {
        setInt(pending, pendingUploadsVariable);
    }
    
    public void setPendingDownloads(int pending) {
        setInt(pending, pendingDownloadsVariable);
    }
	
	private void setInt(int value, InlineLabel label) {
		label.setText(Integer.toString(value));
	}
    
    public void removeFileBaseWidgets() {
        for(final Widget widget : fileBaseWidgets) {
            widget.removeFromParent();
        }
    }
	
	private Widget createPanel() {
		final FlowPanel rootPanel = new FlowPanel();
		rootPanel.getElement().setId(MENU_ID);
        
        // Application cache status
//        rootPanel.add(createHeader(I18N.CONSTANTS.offlineModeHeader(), I18N.CONSTANTS.offlineModeAvailability()));
        sigmahUpdateVariable = createVariable(I18N.CONSTANTS.offlineDatabaseLastCompleteUpdateNever());
//        rootPanel.add(sigmahUpdateVariable);
        
        final RatioBar applicationCacheRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.APPLICATION_CACHE, applicationCacheRatioBar);
//        rootPanel.add(
		wrapProgressBar(I18N.CONSTANTS.offlineModeProgress(), applicationCacheRatioBar, ProgressType.APPLICATION_CACHE);
//			);
//        
//        rootPanel.add(createSeparator(true));
        
        // Local database status
        rootPanel.add(createHeader(I18N.CONSTANTS.offlineDatabaseHeader(), I18N.CONSTANTS.offlineDatabaseLastCompleteUpdate()));
        databaseUpdateVariable = createVariable(I18N.CONSTANTS.offlineDatabaseLastCompleteUpdateNever());
        rootPanel.add(databaseUpdateVariable);
        
        final RatioBar localDatabaseRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.DATABASE, localDatabaseRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineSynchronizeProgress(), localDatabaseRatioBar, ProgressType.DATABASE));
        
        updateDatabaseAnchor = createActionButton(I18N.CONSTANTS.offlineActionUpdateDatabase());
		updateDatabaseAnchor.addStyleName(UPDATE_DATABASE_ACTION_STYLE);
		rootPanel.add(updateDatabaseAnchor);
        
        rootPanel.add(createSeparator(false));
        
        // File base status
        final InlineHTML offlineFileBaseHeader = createHeader(I18N.CONSTANTS.offlineFileBaseHeader());
        rootPanel.add(offlineFileBaseHeader);
		
		final FlowPanel pendingUploadsPanel = new FlowPanel();
		pendingUploadsPanel.add(createListEntry(I18N.CONSTANTS.offlineTransfertUploadPending()));
		pendingUploadsVariable = createVariable("0");
		pendingUploadsPanel.add(pendingUploadsVariable);
		rootPanel.add(pendingUploadsPanel);
		
        final RatioBar uploadRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.UPLOAD, uploadRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineTransfertUploadProgress(), uploadRatioBar, ProgressType.UPLOAD));
		
		final FlowPanel pendingDownloadsPanel = new FlowPanel();
		pendingDownloadsPanel.add(createListEntry(I18N.CONSTANTS.offlineTransfertDownloadPending()));
		pendingDownloadsVariable = createVariable("0");
		pendingDownloadsPanel.add(pendingDownloadsVariable);
		rootPanel.add(pendingDownloadsPanel);
        
        final RatioBar downloadRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.DOWNLOAD, downloadRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineTransfertDownloadProgress(), downloadRatioBar, ProgressType.DOWNLOAD));
        
        transferFilesAnchor = createActionButton(I18N.CONSTANTS.offlineActionTransferFiles());
		transferFilesAnchor.addStyleName(TRANSFER_FILES_ACTION_STYLE);
		rootPanel.add(transferFilesAnchor);
        
        fileBaseWidgets = new Widget[] {offlineFileBaseHeader, pendingUploadsPanel, uploadRatioBar, pendingDownloadsPanel, downloadRatioBar, transferFilesAnchor};
        
        rootPanel.add(createSeparator(true));
        
        // Destroy offline mode button
        removeOfflineDataAnchor = createActionButton(I18N.CONSTANTS.offlineActionDestroyLocalData());
        removeOfflineDataAnchor.addStyleName(REMOVE_DATABASE_ACTION_STYLE);
        rootPanel.add(removeOfflineDataAnchor);
		
		return rootPanel;
	}
    
    private InlineHTML createHeader(String header) {
		return createHeader(header, null);
	}
	
    private InlineHTML createHeader(String header, String statusLabel) {
		final SafeHtmlBuilder htmlBuilder = new SafeHtmlBuilder()
			.appendHtmlConstant("<h1>")
            .appendEscaped(header)
            .appendHtmlConstant("</h1>");
		
		if(statusLabel != null) {
			htmlBuilder
				.appendHtmlConstant("<span style=\"margin-left: 2em; font-style: italic\">")
				.appendEscaped(statusLabel)
				.appendHtmlConstant("</span>");
		}
		
        return new InlineHTML(htmlBuilder.toSafeHtml());
    }
	
	private InlineHTML createListEntry(String label) {
		return new InlineHTML(new SafeHtmlBuilder()
			.appendHtmlConstant("<span style=\"margin-left: 2em;\">- ")
			.appendEscaped(label)
			.appendHtmlConstant("</span>")
			.toSafeHtml());
	}
    
    private InlineLabel createVariable(String value) {
        final InlineLabel label = new InlineLabel(value);
        label.setStyleName(VARIABLE_STYLE);
        return label;
    }
    
    private InlineHTML createSeparator(boolean visible) {
        final InlineHTML inlineHTML = new InlineHTML(new SafeHtmlBuilder()
            .appendHtmlConstant("<hr class=\"" + SEPARATOR_STYLE + "\">")
            .toSafeHtml());
        
        if(!visible) {
            inlineHTML.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        
        return inlineHTML;
    }
    
    private Anchor createActionButton(String label) {
        final Anchor anchor = new Anchor(label);
        anchor.setStyleName(ACTION_STYLE);
        return anchor;
    }
	
	private FlowPanel wrapProgressBar(String prefix, RatioBar ratioBar, ProgressType type) {
		final FlowPanel panel = new FlowPanel();
		
		final Label label = new Label(prefix);
		panel.add(label);
		panel.add(ratioBar);
		
		label.setStyleName(PROGRESS_LABEL_STYLE);
		ratioBar.setStyleName(PROGRESS_BAR_STYLE);
		
		wraps.put(type, panel);
		return panel;
	} 
}
