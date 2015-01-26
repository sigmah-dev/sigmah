package org.sigmah.client.ui.view.zone;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.view.OfflineMenuPanel;
import org.sigmah.offline.view.SynchronizePopup;

/**
 * Offline banner view (not a real view, just a widget set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineBannerView extends AbstractView implements OfflineBannerPresenter.View {

	private static final String DROP_DOWN =  " ▾";

	private Panel statusPanel;
	private HTML statusLabel;
    private RatioBar progressBar;
    private OfflineMenuPanel menuPanel;
    private SynchronizePopup synchronizePopup;
    private Image warningIcon;
    private Image connectionIcon;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		statusPanel = new FlowPanel();
		statusPanel.getElement().setId("offline-status");
        
        warningIcon = IconImageBundle.ICONS.warning().createImage();
        warningIcon.setVisible(false);
        warningIcon.setStyleName("offline-icon");
        statusPanel.add(warningIcon);
        
        connectionIcon = IconImageBundle.ICONS.connection().createImage();
        connectionIcon.setVisible(false);
        connectionIcon.setStyleName("offline-icon");
        statusPanel.add(connectionIcon);

		statusLabel = new HTML();
        statusLabel.setStyleName("offline-button");
		statusPanel.add(statusLabel);
        
        progressBar = new RatioBar(0);
        progressBar.setStyleName("offline-progress");
        progressBar.getElement().getStyle().setDisplay(Style.Display.NONE);
        statusPanel.add(progressBar);
		
		menuPanel = new OfflineMenuPanel();
        menuPanel.setVisible(false);
		statusPanel.add(menuPanel);
        
        synchronizePopup = new SynchronizePopup();
		
		// initWidget(); Useless.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getStatusPanel() {
		return statusPanel;
	}
    
    /**
	 * {@inheritDoc}
	 */
    @Override
    public HTML getStatusLabel() {
        return statusLabel;
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
    public OfflineMenuPanel getMenuPanel() {
        return menuPanel;
    }

    @Override
    public SynchronizePopup getSynchronizePopup() {
        return synchronizePopup;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatus(ApplicationState state) {
        switch(state) {
            case ONLINE:
                statusLabel.setHTML(I18N.CONSTANTS.online() + DROP_DOWN);
                break;
                
            default:
                statusLabel.setHTML(I18N.CONSTANTS.offline() + DROP_DOWN);
                break;
        }
	}

    @Override
    public void setProgress(double progress, boolean undefined) {
		if(undefined) {
			progressBar.getElement().getStyle().clearDisplay();
			progressBar.setRatioUndefined();
			
		} else if(progress <= 0 || progress >= 1) {
            progressBar.getElement().getStyle().setDisplay(Style.Display.NONE);
			
        } else {
            progressBar.getElement().getStyle().clearDisplay();
            progressBar.setRatio(progress * 100.0);
        }
    }
	
    @Override
    public void setSynchronizeAnchorEnabled(boolean enabled) {
		setAnchorEnabled(menuPanel.getUpdateDatabaseAnchor(), enabled);
    }
	
    @Override
    public void setTransferFilesAnchorEnabled(boolean enabled) {
		setAnchorEnabled(menuPanel.getTransferFilesAnchor(), enabled);
    }
	
	private void setAnchorEnabled(Anchor anchor, boolean enabled) {
        anchor.setEnabled(enabled);
        if(enabled) {
            anchor.removeStyleName("offline-action-disabled");
        } else {
            anchor.addStyleName("offline-action-disabled");
        }
	}
    
    @Override
    public void setWarningIconVisible(boolean visible) {
        warningIcon.setVisible(visible);
    }
    
    @Override
    public void setConnectionIconVisible(boolean visible) {
        connectionIcon.setVisible(visible);
    }

}
