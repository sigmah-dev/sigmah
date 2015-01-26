package org.sigmah.offline.view;

import com.google.gwt.user.client.ui.Label;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

/**
 * Popup displayed during the synchronization.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizePopup extends AbstractPopupView<PopupWidget> {
    
    // CSS style names.
	private static final String STYLE_HEADER_LABEL = "header-label";
	private static final String STYLE_PROGRESS_BAR = "offline-sync-progress";

    private Label label;
    private RatioBar progressBar;
    
    public SynchronizePopup() {
        super(new PopupWidget(true, false));
    }
    
    @Override
    public void initialize() {
        setPopupTitle(I18N.CONSTANTS.offlineSynchronizeTitle());
        
        label = new Label();
        label.setStyleName(STYLE_HEADER_LABEL);
        
        progressBar = new RatioBar(0.0);
        progressBar.setWidth("250px");
        progressBar.setStyleName(STYLE_PROGRESS_BAR);
        
        final FormPanel form = Forms.panel();
        form.add(label);
        form.add(progressBar);
        initPopup(form);
    }
    
    public void setTask(String task) {
        label.setText(task);
    }
    
    public void setProgress(double progress) {
        progressBar.setRatio(progress * 100.0);
    }

    @Override
    public void center() {
        setProgress(0.0);
        super.center();
    }
}
