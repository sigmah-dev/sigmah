/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;


import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.BindingEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.*;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.IndicatorDTO;

public class IndicatorForm extends AbstractDesignForm {

	private final Dispatcher dispatcher;
	
    private FormBinding binding;
	private NumberField idField;
	private IndicatorGroupCombo groupField;
	private TextField<String> unitsField;
	private ValueLabelField labelsField;
	private RadioGroup typeGroup;
	private HiddenField<Integer> aggregation;
	
	private Radio quantRadio;
	private Radio qualRadio;
	
	private Radio sumRadio;
	private Radio avgRadio;
	private RadioGroup aggregationGroup;
	private NumberField objectiveField;
	private DatasourceField datasourceField;

    public IndicatorForm(Dispatcher dispatcher)  {
        super();
        this.dispatcher = dispatcher;
        binding = new FormBinding(this);

        setScrollMode(Scroll.AUTOY);
        
        this.setLabelWidth(150);
        this.setFieldWidth(250);

        idField = new NumberField();
        idField.setFieldLabel("ID");
        idField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(idField, "id"));
        add(idField);
        
        TextField<String> codeField = new TextField<String>();
        codeField.setFieldLabel(I18N.CONSTANTS.indicatorCode());
        codeField.setAllowBlank(false);
        binding.addFieldBinding(new FieldBinding(codeField,"code"));
        this.add(codeField);

        TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel(I18N.CONSTANTS.name());
        nameField.setAllowBlank(false);
        nameField.setMaxLength(1024);
        binding.addFieldBinding(new FieldBinding(nameField, "name"));
        this.add(nameField);

        groupField = new IndicatorGroupCombo(dispatcher);
        groupField.setFieldLabel(I18N.CONSTANTS.group());
        groupField.setMaxLength(50);
        binding.addFieldBinding(groupField.newBinding("groupId"));
        this.add(groupField);
        
        aggregation = new HiddenField<Integer>();
        aggregation.setFireChangeEventOnSetValue(true);
        binding.addFieldBinding(new FieldBinding(aggregation, "aggregation"));
        
        typeGroup = new RadioGroup("type");
        typeGroup.setFieldLabel(I18N.CONSTANTS.type());
        typeGroup.setOrientation(Orientation.HORIZONTAL);
        typeGroup.add(quantRadio = newRadio(I18N.CONSTANTS.quantitative()));
        typeGroup.add(qualRadio = newRadio(I18N.CONSTANTS.qualitative()));
        this.add(typeGroup);
        
        typeGroup.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if(typeGroup.getValue() == qualRadio) {
					aggregation.setValue(IndicatorDTO.AGGREGATE_MULTINOMIAL);
				} else {
					aggregation.setValue(quantAggSelection());
				}
				updateFormLayout();
			}
		});
        
        
        labelsField = new ValueLabelField();
        labelsField.setFieldLabel(I18N.CONSTANTS.possibleValues());
        binding.addFieldBinding(new FieldBinding(labelsField, "labels"));
        this.add(labelsField);
        
        aggregationGroup = new RadioGroup("quantitativeAggregation");
        aggregationGroup.setFieldLabel(I18N.CONSTANTS.aggregationMethod());
        aggregationGroup.add(sumRadio = newRadio(I18N.CONSTANTS.sum()));
        aggregationGroup.add(avgRadio = newRadio(I18N.CONSTANTS.average()));
        this.add(aggregationGroup);
        
        aggregationGroup.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				aggregation.setValue(quantAggSelection());
			}
        });
        
        unitsField = new TextField<String>();
        unitsField.setFieldLabel(I18N.CONSTANTS.units());
        unitsField.setAllowBlank(false);
        unitsField.setMaxLength(15);
        binding.addFieldBinding(new FieldBinding(unitsField, "units"));
        this.add(unitsField);
        
        objectiveField = new NumberField();
        objectiveField.setFieldLabel(I18N.CONSTANTS.targetValue());
        binding.addFieldBinding(new FieldBinding(objectiveField, "objective"));
        this.add(objectiveField);
        
        TextArea verification = new TextArea();
        verification.setFieldLabel(I18N.CONSTANTS.sourceOfVerification());
        binding.addFieldBinding(new FieldBinding(verification, "sourceOfVerification"));
        this.add(verification);
        
        TextArea descField = new TextArea();
        descField.setFieldLabel(I18N.CONSTANTS.indicatorComments());
        binding.addFieldBinding(new FieldBinding(descField, "description"));
        this.add(descField);
                
        datasourceField = new DatasourceField(dispatcher);
        datasourceField.setFieldLabel("Datasources");
        binding.addFieldBinding(new FieldBinding(datasourceField, "dataSourceIds"));
        binding.addFieldBinding(new FieldBinding(datasourceField.getDirectBox(), "directDataEntryEnabled"));
        this.add(datasourceField);
        
        binding.addListener(Events.Bind, new Listener<BindingEvent>() {

			@Override
			public void handleEvent(BindingEvent be) {
				if(aggregation.getValue() == IndicatorDTO.AGGREGATE_MULTINOMIAL) {
					typeGroup.setValue(qualRadio);
				} else {
					typeGroup.setValue(quantRadio);
					aggregationGroup.setValue(aggregation.getValue() == IndicatorDTO.AGGREGATE_AVG ? avgRadio : sumRadio);
				}
                IndicatorDTO indicator = (IndicatorDTO) be.getModel();
                updateDatasources(indicator);
				updateFormLayout();
                groupField.loadGroups(indicator);
			}
		});
    }
    
    protected void updateDatasources(IndicatorDTO indicator) {
		datasourceField.load(indicator);
	}

	private Radio newRadio(String label) {
    	Radio radio = new Radio();
    	radio.setBoxLabel(label);
    	return radio;
    }
    
    public void setIdVisible(boolean visible) {
    	idField.setVisible(visible);
    }
    
    public void setGroupVisible(boolean visible) {
    	groupField.setVisible(visible);
    }
    
    private int quantAggSelection() {
    	if(aggregationGroup.getValue() == avgRadio) {
    		return IndicatorDTO.AGGREGATE_AVG;
    	} else {
    		return IndicatorDTO.AGGREGATE_SUM;
    	}
    }

    @Override
    public FormBinding getBinding() {
        return binding;
    }
    
    private void updateFormLayout() {
    	labelsField.setVisible(typeGroup.getValue() == qualRadio);
		unitsField.setVisible(typeGroup.getValue() != qualRadio);
		unitsField.setAllowBlank(typeGroup.getValue() == qualRadio);
		aggregationGroup.setVisible(typeGroup.getValue() != qualRadio);
		objectiveField.setVisible(typeGroup.getValue() != qualRadio);
	}

    
    
	@Override
	public int getPreferredDialogWidth() {
		return 500;
	}

	@Override
	public int getPreferredDialogHeight() {
		return 450;
	}
}
