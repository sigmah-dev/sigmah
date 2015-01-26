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
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineMenuPanel extends Composite {
    
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

	public OfflineMenuPanel() {
		initWidget(createPanel());
	}

    public Anchor getUpdateDatabaseAnchor() {
        return updateDatabaseAnchor;
    }

    public Anchor getTransferFilesAnchor() {
        return transferFilesAnchor;
    }

    public Anchor getRemoveOfflineDataAnchor() {
        return removeOfflineDataAnchor;
    }

    public RatioBar getBar(ProgressType progressType) {
        return bars.get(progressType);
    }
	
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
		rootPanel.getElement().setId("offline-menu");
        
        // Application cache status
        rootPanel.add(createHeader(I18N.CONSTANTS.offlineModeHeader(), I18N.CONSTANTS.offlineModeAvailability()));
        sigmahUpdateVariable = createVariable(I18N.CONSTANTS.offlineDatabaseLastCompleteUpdateNever());
        rootPanel.add(sigmahUpdateVariable);
        
        final RatioBar applicationCacheRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.APPLICATION_CACHE, applicationCacheRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineModeProgress(), applicationCacheRatioBar, ProgressType.APPLICATION_CACHE));
        
        rootPanel.add(createSeparator(true));
        
        // Local database status
        rootPanel.add(createHeader(I18N.CONSTANTS.offlineDatabaseHeader(), I18N.CONSTANTS.offlineDatabaseLastCompleteUpdate()));
        databaseUpdateVariable = createVariable(I18N.CONSTANTS.offlineDatabaseLastCompleteUpdateNever());
        rootPanel.add(databaseUpdateVariable);
        
        final RatioBar localDatabaseRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.DATABASE, localDatabaseRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineSynchronizeProgress(), localDatabaseRatioBar, ProgressType.DATABASE));
        
        updateDatabaseAnchor = createActionButton(I18N.CONSTANTS.offlineActionUpdateDatabase());
		rootPanel.add(updateDatabaseAnchor);
        
        rootPanel.add(createSeparator(false));
        
        // File base status
        final InlineHTML offlineFileBaseHeader = createHeader(I18N.CONSTANTS.offlineFileBaseHeader(), "");
        rootPanel.add(offlineFileBaseHeader);
		
		final FlowPanel pendingUploadsPanel = new FlowPanel();
		pendingUploadsPanel.add(new InlineLabel(I18N.CONSTANTS.offlineTransfertUploadPending()));
		pendingUploadsVariable = createVariable("0");
		pendingUploadsPanel.add(pendingUploadsVariable);
		rootPanel.add(pendingUploadsPanel);
		
        final RatioBar uploadRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.UPLOAD, uploadRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineTransfertUploadProgress(), uploadRatioBar, ProgressType.UPLOAD));
		
		final FlowPanel pendingDownloadsPanel = new FlowPanel();
		pendingDownloadsPanel.add(new InlineLabel(I18N.CONSTANTS.offlineTransfertDownloadPending()));
		pendingDownloadsVariable = createVariable("0");
		pendingDownloadsPanel.add(pendingDownloadsVariable);
		rootPanel.add(pendingDownloadsPanel);
        
        final RatioBar downloadRatioBar = new RatioBar(0.0);
        bars.put(ProgressType.DOWNLOAD, downloadRatioBar);
        rootPanel.add(wrapProgressBar(I18N.CONSTANTS.offlineTransfertDownloadProgress(), downloadRatioBar, ProgressType.DOWNLOAD));
        
        transferFilesAnchor = createActionButton(I18N.CONSTANTS.offlineActionTransferFiles());
		rootPanel.add(transferFilesAnchor);
        
        fileBaseWidgets = new Widget[] {offlineFileBaseHeader, pendingUploadsPanel, uploadRatioBar, pendingDownloadsPanel, downloadRatioBar, transferFilesAnchor};
        
        rootPanel.add(createSeparator(true));
        
        // Destroy offline mode button
        removeOfflineDataAnchor = createActionButton(I18N.CONSTANTS.offlineActionDestroyLocalData());
        removeOfflineDataAnchor.addStyleName("offline-action-remove");
        rootPanel.add(removeOfflineDataAnchor);
		
		return rootPanel;
	}
    
    private InlineHTML createHeader(String header, String statusLabel) {
        return new InlineHTML(new SafeHtmlBuilder()
            .appendHtmlConstant("<h1>")
            .appendEscaped(header)
            .appendHtmlConstant("</h1>")
            .appendEscaped(statusLabel)
            .toSafeHtml());
    }
    
    private InlineLabel createVariable(String value) {
        final InlineLabel label = new InlineLabel(value);
        label.setStyleName("offline-menu-variable");
        return label;
    }
    
    private InlineHTML createSeparator(boolean visible) {
        final InlineHTML inlineHTML = new InlineHTML(new SafeHtmlBuilder()
            .appendHtmlConstant("<hr class=\"offline-action-separator\">")
            .toSafeHtml());
        
        if(!visible) {
            inlineHTML.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        }
        
        return inlineHTML;
    }
    
    private Anchor createActionButton(String label) {
        final Anchor anchor = new Anchor(label);
        anchor.setStyleName("offline-action");
        return anchor;
    }
	
	private FlowPanel wrapProgressBar(String prefix, RatioBar ratioBar, ProgressType type) {
		final FlowPanel panel = new FlowPanel();
		
		final Label label = new Label(prefix);
		panel.add(label);
		panel.add(ratioBar);
		
		label.setStyleName("offline-menu-progress-label");
		ratioBar.setStyleName("offline-menu-progress");
		
		wraps.put(type, panel);
		return panel;
	} 
}
