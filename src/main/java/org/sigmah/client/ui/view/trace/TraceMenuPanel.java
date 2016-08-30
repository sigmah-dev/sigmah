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

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import java.util.Date;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.offline.sync.UpdateDates;

/**
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class TraceMenuPanel extends Composite{
	
	/**
	 * trace menu id.
	 */
	private static final String MENU_ID = "trace-menu";	
	/**
	 * trace menu text style.
	 */
	private  final String VARIABLE_STYLE = "trace-menu-variable";
	/**
	 * Enable trace button style.
	 */
	private  final String ENABLE_ACTION_STYLE = "enable-trace";
	/**
	 * Disable trace button style.
	 */
	private  final String DISABLE_ACTION_STYLE = "disable-trace";
	/**
	 * Send report button style.
	 */
	private  final String SEND_REPORT_ACTION_STYLE = "send-report";
	/**
	 * Style calss.
	 */
	private  final String ACTION_STYLE = "trace-action";
	/**
	 * Id of active desactive button.
	 */
	private  final String ACTIVE_DESACTIVE_BUTTON_ID = "activeDesactiveButtonId";
	
	private  final String SEND_REPORT_BUTTON_ID = "sendReportButtonId";
	
	/**
	 * Static lable for date.
	 */
	private InlineLabel dateActivationModeLabel;
	/**
	 * Label for date.
	 */
	private InlineLabel dateActivationModeVariable;	
	/**
	 * Enable/disable button objec.
	 */
	private Anchor activeDesactiveModeAnchor;
	/**
	 * Send report button objec.
	 */
	private Anchor sendReportAnchor;
	
	
	
	/**
	 * Creates a new menu.
	 */
	public TraceMenuPanel() {
		initWidget(createPanel());
	}
	/**
	 * Init Menu panel.
	 * @return 
	 */
	private Widget createPanel() {
		final FlowPanel rootPanel = new FlowPanel();
		rootPanel.getElement().setId(MENU_ID);
		Date dateActivation = UpdateDates.getSigmahActivationTraceDate();
		dateActivationModeVariable = createVariable(dateActivation != null?DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(dateActivation):null);		
		rootPanel.add(dateActivationModeVariable);
		dateActivationModeLabel = createVariable(I18N.CONSTANTS.probesDateTraceActivation());
        rootPanel.add(dateActivationModeLabel);	
		if(dateActivation == null){
			Profiler.INSTANCE.setActive(false);
			dateActivationModeLabel.setVisible(false);
			dateActivationModeVariable.setVisible(false);
			
		}else{			
			Profiler.INSTANCE.setActive(true);
			dateActivationModeLabel.setVisible(true);
			dateActivationModeVariable.setVisible(true);
		}

		//disable/enable
		activeDesactiveModeAnchor = Profiler.INSTANCE.isActive()? createActionButton(I18N.CONSTANTS.probesDisableTrace()):createActionButton(I18N.CONSTANTS.probesEnableTrace());
		activeDesactiveModeAnchor.addStyleName(Profiler.INSTANCE.isActive()?DISABLE_ACTION_STYLE:ENABLE_ACTION_STYLE);
		activeDesactiveModeAnchor.getElement().setId(ACTIVE_DESACTIVE_BUTTON_ID);
		rootPanel.add(activeDesactiveModeAnchor);
		
		// send report
		sendReportAnchor = createActionButton(I18N.CONSTANTS.probesSendReport());
		sendReportAnchor.getElement().setId(SEND_REPORT_BUTTON_ID);
		sendReportAnchor.addStyleName(SEND_REPORT_ACTION_STYLE);
		rootPanel.add(sendReportAnchor);		
		return rootPanel;
	}	
	/**
	 * Create label.
	 * @param value
	 * @return 
	 */
	 private InlineLabel createVariable(String value) {
        final InlineLabel label = new InlineLabel(value);
        label.setStyleName(VARIABLE_STYLE);
        return label;
    }
	 /**
	  * Create button.
	  * @param label
	  * @return 
	  */
	  private Anchor createActionButton(String label) {
        final Anchor anchor = new Anchor(label);
        anchor.setStyleName(ACTION_STYLE);
        return anchor;
    }

	public InlineLabel getDateActivationModeVariable() {
		return dateActivationModeVariable;
	}

	public void setDateActivationModeVariable(InlineLabel dateActivationModeVariable) {
		this.dateActivationModeVariable = dateActivationModeVariable;
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
	
	public String getENABLE_ACTION_STYLE() {
		return ENABLE_ACTION_STYLE;
	}

	public String getDISABLE_ACTION_STYLE() {
		return DISABLE_ACTION_STYLE;
	}
}
