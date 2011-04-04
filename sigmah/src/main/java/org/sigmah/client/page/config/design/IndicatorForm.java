/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;


import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.widget.MappingComboBox;
import org.sigmah.client.page.common.widget.MappingComboBoxBinding;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.Converter;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;

class IndicatorForm extends AbstractDesignForm {

    private FormBinding binding;
	private NumberField idField;
	private TextField<String> categoryField;
	private TextField<String> unitsField;
	private AdapterField labelsField;
	private RadioGroup typeGroup;

    public IndicatorForm()  {
        super();
        binding = new FormBinding(this);

        setScrollMode(Scroll.AUTOY);
        
        this.setLabelWidth(150);
        this.setFieldWidth(200);

        idField = new NumberField();
        idField.setFieldLabel("ID");
        idField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(idField, "id"));
        add(idField);
        
        TextField<String> codeField = new TextField<String>();
        codeField.setFieldLabel(I18N.CONSTANTS.indicatorCode());
        binding.addFieldBinding(new FieldBinding(codeField,"code"));
        this.add(codeField);

        TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel(I18N.CONSTANTS.name());
        nameField.setAllowBlank(false);
        nameField.setMaxLength(128);
        binding.addFieldBinding(new FieldBinding(nameField, "name"));
        this.add(nameField);

        categoryField = new TextField<String>();
        categoryField.setName("category");
        categoryField.setFieldLabel(I18N.CONSTANTS.group());
        categoryField.setMaxLength(50);
        categoryField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(categoryField, "category"));
        this.add(categoryField);
        
        NumberField objectiveField = new NumberField();
        objectiveField.setName("objective");
        objectiveField.setFieldLabel(I18N.CONSTANTS.objecive());
        binding.addFieldBinding(new FieldBinding(objectiveField, "objective"));
        this.add(objectiveField);
       
        typeGroup = new RadioGroup("aggregation");
        typeGroup.setFieldLabel(I18N.CONSTANTS.type());
        typeGroup.setOrientation(Orientation.VERTICAL);
        typeGroup.add(newRadio(IndicatorDTO.AGGREGATE_SUM, I18N.CONSTANTS.sum()));
        typeGroup.add(newRadio(IndicatorDTO.AGGREGATE_AVG, I18N.CONSTANTS.average()));
        typeGroup.add(newRadio(IndicatorDTO.AGGREGATE_SITE_COUNT, I18N.CONSTANTS.siteCount()));
        typeGroup.add(newRadio(IndicatorDTO.AGGREGATE_MULTINOMIAL, "Qualitative"));
        binding.addFieldBinding(new AggregationFieldBinding(typeGroup, "aggregation"));
        this.add(typeGroup);
        
        labelsField = new AdapterField(new ValueLabelGrid());
        labelsField.setFieldLabel("Value Labels");
        this.add(labelsField);

        unitsField = new TextField<String>();
        unitsField.setName("units");
        unitsField.setFieldLabel(I18N.CONSTANTS.units());
        unitsField.setAllowBlank(false);
        unitsField.setMaxLength(15);
        binding.addFieldBinding(new FieldBinding(unitsField, "units"));
        this.add(unitsField);
        
        typeGroup.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				updateFormLayout();
			}
		});
        
        TextArea descField = new TextArea();
        descField.setFieldLabel(I18N.CONSTANTS.description());
        binding.addFieldBinding(new FieldBinding(descField, "description"));
        this.add(descField);
    }
    
    private Radio newRadio(int value, String label) {
    	Radio radio = new Radio();
    	radio.setBoxLabel(label);
    	radio.setData("type", value);
    	return radio;
    }
    
    public void setIdVisible(boolean visible) {
    	idField.setVisible(visible);
    }
    
    public void setCategoryVisible(boolean visible) {
    	categoryField.setVisible(visible);
    }

    @Override
    public FormBinding getBinding() {
        return binding;
    }
    
    private void updateFormLayout() {
    	if(typeGroup.getValue() != null) {
			int type = (Integer)typeGroup.getValue().getData("type");
	
			labelsField.setVisible(type == IndicatorDTO.AGGREGATE_MULTINOMIAL);
			
			boolean requiresUnit = type != IndicatorDTO.AGGREGATE_MULTINOMIAL && type != IndicatorDTO.AGGREGATE_SITE_COUNT;
			unitsField.setVisible(requiresUnit);
			unitsField.setAllowBlank(!requiresUnit);
    	}
	}

	private static class AggregationFieldBinding extends FieldBinding {

		public AggregationFieldBinding(final Field field, String property) {
			super(field, property);
			 setConverter(new Converter() {
		            @Override
		            public Object convertModelValue(Object value) {
		            	for(Field radio : ((RadioGroup)field).getAll()) {
		            		if(radio.getData("type").equals(value)) {
		            			return radio;
		            		}
		            	}
		                return null;
		            }

		            @Override
		            public Object convertFieldValue(Object value) {
		                Radio radio = (Radio)value;
		                return radio == null ? null : (Integer)radio.getData("type");
		            }
		        });
		}
    	
    	
    }
}
