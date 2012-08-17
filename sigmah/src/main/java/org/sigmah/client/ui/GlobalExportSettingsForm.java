package org.sigmah.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.export.GlobalExportFormat;
import org.sigmah.shared.dto.GlobalExportDTO;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UpdateGlobalExportSettings;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/*
 * Form to configure global export settings
 * 
 * @author sherzod
 */
public class GlobalExportSettingsForm {
  
	static class SimpleComboBoxData extends BaseModelData{
		
		public SimpleComboBoxData(Integer value,String label){
			setValue(value);
			setLabel(label);
		}
		public Integer getValue() {
			return get("value");
		}

		public void setValue(Integer value) {
			set("value", value);
		}

		public String getLabel() {
			return get("label");
		}

		public void setLabel(String label) {
			set("label", label);
		}		 				
	}	
	private final ListStore<FlexibleElementDTO> fieldsStore;
	private final ListStore<ProjectModelDTO> modelsStore;
	private final Integer organizationId;
	private final Map<Integer,Boolean> fieldsMap;
	private final List<SimpleComboBoxData> autoExportSchedules;
	private final List<SimpleComboBoxData> autoDeleteSchedules;
	private final static Map<Integer,SimpleComboBoxData> exportScheduleMap;
	private final static Map<Integer,SimpleComboBoxData> deleteScheduleMap;
	static{
		exportScheduleMap=new HashMap<Integer,SimpleComboBoxData>();
		exportScheduleMap.put(0,new SimpleComboBoxData(0, I18N.CONSTANTS.notScheduled()));
		exportScheduleMap.put(1,new SimpleComboBoxData(1, I18N.CONSTANTS.daily()));
		exportScheduleMap.put(3,new SimpleComboBoxData(3, I18N.MESSAGES.everyXDays("3")));
		exportScheduleMap.put(9,new SimpleComboBoxData(9, I18N.MESSAGES.everyXDays("9")));
		exportScheduleMap.put(15,new SimpleComboBoxData(15, I18N.MESSAGES.everyXDays("15")));
		
		deleteScheduleMap=new HashMap<Integer, SimpleComboBoxData>();
		deleteScheduleMap.put(0,new SimpleComboBoxData(0, I18N.CONSTANTS.notScheduled()));
		deleteScheduleMap.put(1,new SimpleComboBoxData(1,  I18N.MESSAGES.olderThanXMonths("1")));
		deleteScheduleMap.put(3,new SimpleComboBoxData(3, I18N.MESSAGES.olderThanXMonths("3")));
		deleteScheduleMap.put(6,new SimpleComboBoxData(6, I18N.MESSAGES.olderThanXMonths("6")));
		deleteScheduleMap.put(12,new SimpleComboBoxData(12, I18N.MESSAGES.olderThanXMonths("12")));
	}
	
	public GlobalExportSettingsForm(final Integer organizationId,final Dispatcher dispatcher){
		
		modelsStore=new ListStore<ProjectModelDTO>();
		fieldsStore = new ListStore<FlexibleElementDTO>();	
		this.organizationId=organizationId;
		fieldsMap=new HashMap<Integer, Boolean>();
	
		autoExportSchedules=new ArrayList<SimpleComboBoxData>();
		for(Integer value:exportScheduleMap.keySet()){
			autoExportSchedules.add(exportScheduleMap.get(value));
		}
		autoDeleteSchedules=new ArrayList<SimpleComboBoxData>();
		for(Integer value:deleteScheduleMap.keySet()){
			autoDeleteSchedules.add(deleteScheduleMap.get(value));
		}
		
		final Window w = new Window();
		w.setPlain(true);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setLayout(new FitLayout());
		w.setSize(600,400);
		w.setHeading(I18N.CONSTANTS.globalExportConfiguration());

		final FormPanel panel=new FormPanel();
		panel.setHeaderVisible(false);
 		FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);
       
        panel.setLayout(layout);
        // file format
        final Radio calcChoice = new Radio();
		calcChoice.setBoxLabel(I18N.CONSTANTS.openDocumentSpreadsheet());
		calcChoice.setName("type");

		final Radio excelChoice = new Radio();
		excelChoice.setValue(true);
		excelChoice.setBoxLabel(I18N.CONSTANTS.msExcel());
		excelChoice.setName("type");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setOrientation(Orientation.VERTICAL);
		radioGroup.setFieldLabel(I18N.CONSTANTS.chooseFileType());
		radioGroup.add(excelChoice);
		radioGroup.add(calcChoice);
		panel.add(radioGroup);
		
		//configure fields
		//project model
		final Grid<ProjectModelDTO> pModelsGrid=getModelsGrid();
		//flex elements
		final Grid<FlexibleElementDTO> elementsGrid=getElementsGrid();
		
		final FlexTable fieldsTable=new FlexTable();
		fieldsTable.setCellPadding(0);
		fieldsTable.setCellSpacing(0);
		fieldsTable.setWidget(0, 0, new LabelField(I18N.CONSTANTS.configureFieldsToExport() + ":"));
		fieldsTable.getFlexCellFormatter().setWidth(0, 0, "170px");
		fieldsTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		fieldsTable.setWidget(0, 1, pModelsGrid);
 		fieldsTable.setWidget(0, 2, elementsGrid);
		panel.add(fieldsTable);
				
		// auto export schedule
		final ListStore<SimpleComboBoxData> exportScheduleStore = new ListStore<SimpleComboBoxData>();		
        final ComboBox<SimpleComboBoxData> exportSchedulesBox=new ComboBox<SimpleComboBoxData>();			    
          exportSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoExportSchedule());
        exportSchedulesBox.setStore(exportScheduleStore);
        exportSchedulesBox.setDisplayField("label");
        exportSchedulesBox.setValueField("value");
        exportSchedulesBox.setEditable(false);
        exportSchedulesBox.setTriggerAction(TriggerAction.ALL);
        exportScheduleStore.add(autoExportSchedules);
		exportScheduleStore.commitChanges(); 	
		exportSchedulesBox.setValue(exportScheduleMap.get(0));
		panel.add(exportSchedulesBox);  
		
		// auto delete schedule
		final ListStore<SimpleComboBoxData> deleteScheduleStore = new ListStore<SimpleComboBoxData>();		
        final ComboBox<SimpleComboBoxData> deleteSchedulesBox=new ComboBox<SimpleComboBoxData>();			    
         deleteSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoCleanupSchedule());
        deleteSchedulesBox.setStore(deleteScheduleStore);
        deleteSchedulesBox.setDisplayField("label");
        deleteSchedulesBox.setValueField("value");
        deleteSchedulesBox.setEditable(false);
        deleteSchedulesBox.setTriggerAction(TriggerAction.ALL);
        deleteScheduleStore.add(autoDeleteSchedules);
        deleteScheduleStore.commitChanges(); 	
        deleteSchedulesBox.setValue(deleteScheduleMap.get(0));  
 		panel.add(deleteSchedulesBox);
		
		
		//button
		final Button saveButton = new Button(I18N.CONSTANTS.saveExportConfiguration());
		panel.getButtonBar().add(saveButton);
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				UpdateGlobalExportSettings settings= new UpdateGlobalExportSettings(fieldsMap);
				if(excelChoice.getValue()){
					settings.setExportFormat(GlobalExportFormat.XLS);
				}else{
					settings.setExportFormat(GlobalExportFormat.ODS);
				}
				if(exportSchedulesBox.getValue()!=null){					
					if(exportSchedulesBox.getValue().getValue()==0){
						settings.setAutoExportFrequency(null);
					}else{
						settings.setAutoExportFrequency(exportSchedulesBox.getValue().getValue());
					}
				}
				if(deleteSchedulesBox.getValue()!=null){
					if(deleteSchedulesBox.getValue().getValue()==0){
						settings.setAutoDeleteFrequency(null);
					}else{
						settings.setAutoDeleteFrequency(deleteSchedulesBox.getValue().getValue());
					}
				}
					
				settings.setOrganizationId(organizationId);
				 dispatcher.execute(settings, 
						 null, 
						 new AsyncCallback<VoidResult>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(VoidResult result) {
								w.hide();
							}
						});
			}
		});
		
		w.add(panel);
		w.show();
		
		
		//action 
		final GetGlobalExportSettings settingsCommand=new GetGlobalExportSettings(organizationId);
		dispatcher.execute(settingsCommand, 
				new MaskingAsyncMonitor(panel,I18N.CONSTANTS.loading()),
				new AsyncCallback<GlobalExportSettingsDTO>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(GlobalExportSettingsDTO result) {
						// set export format
						switch (result.getExportFormat()) {
						case XLS:
							excelChoice.setValue(true);
							break;

						case ODS:
							calcChoice.setValue(true);
							break;
						}

						// set pmodels
						modelsStore.add(result.getProjectModelsDTO());
						modelsStore.commitChanges();

						// auto export schdule
						if (exportScheduleMap.get(result.getAutoExportFrequency()) != null)
							exportSchedulesBox.setValue(exportScheduleMap.get(result.getAutoExportFrequency()));
						// auto delete schedule
						if (deleteScheduleMap.get(result.getAutoDeleteFrequency()) != null) {
							deleteSchedulesBox.setValue(deleteScheduleMap.get(result.getAutoDeleteFrequency()));
						}
					}
				});
		
		
	}
	
	private Grid<ProjectModelDTO> getModelsGrid(){
		 List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	        ColumnConfig column = new ColumnConfig("name", I18N.CONSTANTS.adminProjectModelsName(), 150);
	        column.setRenderer(new GridCellRenderer<ProjectModelDTO>() {

	            @Override
	            public Object render(final ProjectModelDTO model, String property, ColumnData config, int rowIndex,
	                    int colIndex, ListStore<ProjectModelDTO> store, Grid<ProjectModelDTO> grid) {
 

	            	final ToggleAnchor anchor = new ToggleAnchor(model.getName());
		            anchor.setAnchorMode(true);

		            anchor.addClickHandler(new ClickHandler() {

		                @Override
		                public void onClick(ClickEvent event) {
		                	fieldsStore.removeAll();
		                	fieldsStore.add(model.getGlobalExportElements());
		                	fieldsStore.commitChanges();		                	
						}
						
					});
					return anchor;
	            }
	        });
	        configs.add(column);


	        ColumnModel cm = new ColumnModel(configs);

	        Grid<ProjectModelDTO> grid = new Grid<ProjectModelDTO>(modelsStore, cm);
	        grid.setStyleName("global-export-fields-table");
 	        grid.setSize(160, 200);
	        grid.getView().setForceFit(true);
	        return grid;
	}
	
	private Grid<FlexibleElementDTO> getElementsGrid(){
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  	 
 		
		//checkbox
		ColumnConfig column = new ColumnConfig("globallyExportable",I18N.CONSTANTS.globalExport(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(final FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				final CheckBox gExportable = AdminUtil.createCheckBox("globallyExportable", null);
				gExportable.setValue(model.getGloballyExportable());
				gExportable.addListener(Events.OnClick, new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {
						fieldsMap.put(model.getId(),  gExportable.getValue());						
					}
				});
 				return gExportable;
			}	    	
	    });
		configs.add(column);
		
		//name
		 column = new ColumnConfig("label",I18N.CONSTANTS.adminFlexibleName(), 200);   
        column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
 				String title=null;
 				if(ElementTypeEnum.DEFAULT.equals(model.getElementType()))
					title=DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)model).getType());
				else
					title=model.getLabel();	
				final Text text=AdminUtil.createGridText(title);
				text.setTitle(title);
				 return text;
			}
	    	
	    });
        configs.add(column);        
        ColumnModel cm = new ColumnModel(configs);
        Grid<FlexibleElementDTO> grid = new Grid<FlexibleElementDTO>(fieldsStore, cm); 
        grid.setStyleName("global-export-fields-table");
         grid.setSize(250, 200);
        grid.getView().setForceFit(true);
        return grid;
	}
}
