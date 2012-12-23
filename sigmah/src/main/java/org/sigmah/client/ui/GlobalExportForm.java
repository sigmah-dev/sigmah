/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.ui;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.command.GetGlobalExports;
import org.sigmah.shared.command.result.GlobalExportListResult;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.GlobalExportDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;


/*
 * Provides button to export or configure
 * global exports (projects list)
 * 
 * @author sherzod
 */
public class GlobalExportForm {

	private final Button button;
	private final FormPanel exportForm;
   
	public GlobalExportForm(final Integer organizationId,final Dispatcher dispatcher) {
		 
		button = new Button(I18N.CONSTANTS.exportAll(),
				IconImageBundle.ICONS.excel());
		exportForm = new FormPanel();
		exportForm.setBodyBorder(false);
		exportForm.setHeaderVisible(false);
		exportForm.setPadding(0);
		exportForm.setEncoding(Encoding.URLENCODED);
		exportForm.setMethod(Method.POST);
		exportForm.setAction(GWT.getModuleBaseURL() + "export");
		
		
		final Map<String,String> fieldMap=new HashMap<String, String>();
		fieldMap.put(ExportUtils.PARAM_EXPORT_TYPE,
 				ExportUtils.ExportType.GLOBAL_EXPORT.name());
		fieldMap.put(ExportUtils.PARAM_EXPORT_ORGANIZATION_ID,
				String.valueOf(organizationId));
				
		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				final Window w = new Window();
				w.setPlain(true);
				w.setModal(true);
				w.setBlinkModal(true);
				w.setLayout(new FitLayout());
				w.setSize(420,150);
				w.setHeading(I18N.CONSTANTS.globalExport());

				final FormPanel panel=new FormPanel();
				panel.setHeaderVisible(false);
				FormLayout layout = new FormLayout();
		        layout.setLabelWidth(220);
		        panel.setLayout(layout);
		        panel.setScrollMode(Scroll.AUTOY);
		        
				//version
				final Radio liveChoice = new Radio();
				liveChoice.setBoxLabel(I18N.CONSTANTS.liveData());
				liveChoice.setName("version");
				liveChoice.setValue(true);

				final Radio backupChoice = new Radio();
 				backupChoice.setBoxLabel(I18N.CONSTANTS.backedUpData());
				backupChoice.setName("version");

				RadioGroup radioGroup = new RadioGroup();
				radioGroup.setOrientation(Orientation.VERTICAL);
				radioGroup.setFieldLabel(I18N.CONSTANTS.versionOfDataToExport());
				radioGroup.add(liveChoice);
				radioGroup.add(backupChoice);
				panel.add(radioGroup);

				// period				
			    final DateField fromDate=getDateField();
 		        final DateField toDate=getDateField();		 
 		        toDate.setValue(new Date());
 		        final Button search = new Button(I18N.CONSTANTS.search());
 		       
		        
		        final ListStore<GlobalExportDTO> periodsStore = new ListStore<GlobalExportDTO>();
		        final ComboBox<GlobalExportDTO> periods=new ComboBox<GlobalExportDTO>();			    
 		        periods.setWidth(180);
		        periods.setStore(periodsStore);
		        periods.setDisplayField("date");
		        periods.setValueField("id");
		        periods.setEditable(false);
		        periods.setTriggerAction(TriggerAction.ALL);
		        periods.setHideLabel(false); 		        
		      
		        final FlexTable periodTable=new FlexTable();		        
				periodTable.setHTML(0, 0, "<b>"+ I18N.CONSTANTS.exportBackSelection() + "</b>");
				periodTable.getFlexCellFormatter().setWidth(0, 0, "220px");
				periodTable.setHTML(1, 0, I18N.CONSTANTS.specifyPeriodForBackup());
 				periodTable.setWidget(1, 1, fromDate);
				periodTable.setWidget(1, 2, new LabelField(" -"));
				periodTable.setWidget(1, 3, toDate);
				periodTable.setWidget(1, 4, search);
				periodTable.getFlexCellFormatter().setHeight(1, 0, "30px");
				
				periodTable.setHTML(2, 0, I18N.CONSTANTS.selectBackupToExport());				
				periodTable.setWidget(2, 1, periods);
				periodTable.getFlexCellFormatter().setColSpan(2, 1, 4);
				panel.add(periodTable);
				periodTable.setVisible(false);
				backupChoice.addListener(Events.OnClick, new Listener<FieldEvent>() {
			            public void handleEvent(FieldEvent fe) {			            	 
					        periods.setAllowBlank(false);
					        periodTable.setVisible(true);
					        w.setSize(500,220);
					        
			           }
			     });
				liveChoice.addListener(Events.OnClick, new Listener<FieldEvent>() {
		            public void handleEvent(FieldEvent fe) {
		            	fromDate.clear();		            	 
				        toDate.clear();				        
				        periods.setAllowBlank(true);
				        periods.clear();
				        periodTable.setVisible(false);
				        w.setSize(420,150);
		           }
		     });
			 
				search.addSelectionListener(new SelectionListener<ButtonEvent>() {
						
						@Override
						public void componentSelected(ButtonEvent ce) {							
							if(fromDate.isValid() && toDate.isValid()){
								periods.setEmptyText("");
								dispatcher.execute(
										new GetGlobalExports(fromDate.getValue(), toDate.getValue()),
										null, 
										new AsyncCallback<GlobalExportListResult>() {

											@Override
											public void onFailure(Throwable caught) {
												periods.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
											}

											@Override
											public void onSuccess(GlobalExportListResult result) {
 									                periodsStore.removeAll();
									                if (result != null) {
									                	if(result.getList().size()>0){
									                		periods.setEmptyText(I18N.CONSTANTS.createProjectTypeFundingSelect());
									                	}
									                	periodsStore.add(result.getList());
									                	periodsStore.commitChanges();
									                }
											}
										});
							}
						}
					});
			    
			    
				final Button exportButton = new Button(I18N.CONSTANTS.export());
				final Button settingsButton = new Button(I18N.CONSTANTS.changeConfiguration());
				panel.getButtonBar().add(exportButton);
		 		panel.getButtonBar().add(settingsButton);		 	 
		 		
		 		exportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// Clears the form.
						exportForm.removeAll();
						
						// set version
						if (liveChoice.getValue()) {							
							fieldMap.put(ExportUtils.PARAM_EXPORT_DATA_VERSION,
									ExportUtils.ExportDataVersion.LIVE_DATA.name());
						} else {
							if(!periods.validate()) return; 
								
							fieldMap.put(ExportUtils.PARAM_EXPORT_DATA_VERSION,
									ExportUtils.ExportDataVersion.BACKED_UP_DATA.name());	
							fieldMap.put(ExportUtils.PARAM_EXPORT_GLOBAL_EXPORT_ID,
									String.valueOf(periods.getValue().getId()));	
						}
						
						 	
						// Adds parameters.
						for (String name : fieldMap.keySet()) {
							final HiddenField<String> hiddenField = new HiddenField<String>();
							hiddenField.setName(name);
							hiddenField.setValue(fieldMap.get(name));
							exportForm.add(hiddenField);
						}

						exportForm.layout();

						// Submits the form.
						exportForm.submit();

						w.hide();
					}
				});		
		 		
		 		settingsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					
					@Override
					public void componentSelected(ButtonEvent ce) {
						new GlobalExportSettingsForm(organizationId, dispatcher);
					}
				});
				
			 
				
				w.add(panel);
				w.show();
				
			}
		});
 	}
	
	private DateField getDateField(){
		final DateTimeFormat DATE_FORMAT = DateUtils.DATE_SHORT;
		final DateField dateField = new DateField();
		dateField.setWidth(85);
		dateField.getPropertyEditor().setFormat(DATE_FORMAT);
		dateField.setAllowBlank(false);
		return dateField;
	}
	

	public FormPanel getExportForm() {
		return exportForm;
	}


	public Button getButton() {
		return button;
	}
 
}
