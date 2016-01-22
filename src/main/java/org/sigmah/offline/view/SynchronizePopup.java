package org.sigmah.offline.view;

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
