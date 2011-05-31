package org.sigmah.client.page.table;


import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.entry.IndicatorNumberFormats;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndicatorValueField extends ComboBox<IndicatorValue> {
	
	private IndicatorDTO indicator;
	private NumberFormat format;

	public IndicatorValueField() {
		
		setStore( new ListStore<IndicatorValue>());
		
		setPropertyEditor(new ValuePropertyEditor());
		setSimpleTemplate("{label}");
		setTriggerAction(TriggerAction.ALL);
		setValidator(new ValueValidator());
	}
	
	public void setIndicator(IndicatorDTO indicator) {
		this.indicator = indicator;
		this.format = IndicatorNumberFormats.forIndicator(indicator);
		
		List<IndicatorValue> list = new ArrayList<IndicatorValue>();
		List<String> labels = indicator.getLabels();
		if(labels != null) {
			for(int i=0;i!=labels.size();++i) {
				list.add(new IndicatorValue(i+1, labels.get(i)));
			}
		}
		list.add(new IndicatorValue(null, "---" + I18N.CONSTANTS.empty() + "---"));
		store.removeAll();
		store.add(list);
		
		if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_MULTINOMIAL) {
			setEditable(false);
			setForceSelection(true);
			setHideTrigger(false);
			setDisplayField("label");
		} else {
			setEditable(true);
			setForceSelection(false);
			setHideTrigger(true);
			setDisplayField("value");
		}
	}
	
	@Override
	public IndicatorValue getValue() {
		if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_MULTINOMIAL) {
			return super.getValue();
		} else {
			String rawValue = getRawValue();
			if(rawValue.length() == 0) {
				return new IndicatorValue(null);
			} else {
				return new IndicatorValue(format.parse(rawValue));
			}
		}
	}

	private class ValueValidator implements Validator {

		@Override
		public String validate(Field<?> field, String value) {
			if(indicator.getAggregation() != IndicatorDTO.AGGREGATE_MULTINOMIAL) {
				try {
					format.parse(value);
				
				} catch(NumberFormatException e) {
					return GXT.MESSAGES.numberField_nanText(value);
				}
			}
			return null;
		}
		
	}


	private class ValuePropertyEditor extends ListModelPropertyEditor<IndicatorValue> {

		@Override
		public String getStringValue(IndicatorValue value) {
			if(value.getLabel() != null) {
				return value.getLabel();
			} else {
				return IndicatorNumberFormats.forIndicator(indicator).format(value.getValue());
			}
		}

		@Override
		public IndicatorValue convertStringValue(String value) {
			try {
				Double dvalue = Double.parseDouble(value);
				for(IndicatorValue model : models) {
					if(model.getValue() == dvalue) {
						return model;
					}
				}
				return new IndicatorValue(dvalue);
			} catch(NumberFormatException e) {
				for(IndicatorValue model : models) {
					if(model.getLabel().equals(value)) {
						return model;
					}
				}
				return null;
			}
		}
		
	}
	
}
