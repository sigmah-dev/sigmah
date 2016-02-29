package org.sigmah.client.ui.view.trace;
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

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.i18n.I18N;

/**
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class TraceMenuPanel extends Composite{
	
	private static final String MENU_ID = "trace-menu";
	
	private static final String VARIABLE_STYLE = "trace-menu-variable";
	private static final String ENABLE_DISABLE_ACTION_STYLE = "enable-disable-trace";
	private static final String ACTION_STYLE = "trace-action";
	
	private InlineLabel dateActivationModeLabel;
	private Anchor activeDesactiveModeAnchor;
	private Anchor sendReportAnchor;
	
	/**
	 * Creates a new menu.
	 */
	public TraceMenuPanel() {
		initWidget(createPanel());
	}

	
	
	private Widget createPanel() {
		final FlowPanel rootPanel = new FlowPanel();
		rootPanel.getElement().setId(MENU_ID);
		
		dateActivationModeLabel = createVariable(I18N.CONSTANTS.probesDateTraceActivation());
        rootPanel.add(dateActivationModeLabel);
		
		activeDesactiveModeAnchor = createActionButton(I18N.CONSTANTS.probesActivationDesactivationTRace());
		activeDesactiveModeAnchor.addStyleName(ENABLE_DISABLE_ACTION_STYLE);
		rootPanel.add(activeDesactiveModeAnchor);
		
		sendReportAnchor = createActionButton(I18N.CONSTANTS.probesSendReport());
		sendReportAnchor.addStyleName(ENABLE_DISABLE_ACTION_STYLE);
		rootPanel.add(sendReportAnchor);		
		return rootPanel;
	}	
	
	 private InlineLabel createVariable(String value) {
        final InlineLabel label = new InlineLabel(value);
        label.setStyleName(VARIABLE_STYLE);
        return label;
    }
	  private Anchor createActionButton(String label) {
        final Anchor anchor = new Anchor(label);
        anchor.setStyleName(ACTION_STYLE);
        return anchor;
    }
	  public InlineLabel getDateActivationModeLabel() {
		return dateActivationModeLabel;
	}

	public void setDateActivationModeLabel(InlineLabel dateActivationModeLabel) {
		this.dateActivationModeLabel = dateActivationModeLabel;
	}

	public Anchor getActiveDesactiveModeAnchor() {
		return activeDesactiveModeAnchor;
	}

	public void setActiveDesactiveModeAnchor(Anchor activeDesactiveModeAnchor) {
		this.activeDesactiveModeAnchor = activeDesactiveModeAnchor;
	}

	public Anchor getSendReportAnchor() {
		return sendReportAnchor;
	}

	public void setSendReportAnchor(Anchor sendReportAnchor) {
		this.sendReportAnchor = sendReportAnchor;
	}
}
