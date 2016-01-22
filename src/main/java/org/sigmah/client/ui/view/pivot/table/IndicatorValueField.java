package org.sigmah.client.ui.view.pivot.table;

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


import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.i18n.client.NumberFormat;
import org.sigmah.client.ui.presenter.project.indicator.IndicatorNumberFormats;

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
		this.format = IndicatorNumberFormats.getNumberFormat(indicator);
		
		store.removeAll();
		
		
		if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_MULTINOMIAL) {
			setEditable(false);
			setForceSelection(true);
			setHideTrigger(false);
			setDisplayField("label");
			populateLabels(indicator);
		} else {
			setEditable(true);
			setForceSelection(false);
			setHideTrigger(true);
			setDisplayField("value");
		}
	}

	private void populateLabels(IndicatorDTO indicator) {
		List<IndicatorValue> list = new ArrayList<IndicatorValue>();
		List<String> labels = indicator.getLabels();
		if(labels != null) {
			for(int i=0;i!=labels.size();++i) {
				list.add(new IndicatorValue(i+1, labels.get(i)));
			}
		}
		list.add(new IndicatorValue(null, "---" + I18N.CONSTANTS.empty() + "---"));
		store.add(list);
	}
	
	@Override
	public IndicatorValue getValue() {
		if(indicator != null) {
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
		} else {
			return new IndicatorValue(null);
		}
	}

	private class ValueValidator implements Validator {

		@Override
		public String validate(Field<?> field, String value) {
			if(indicator != null && indicator.getAggregation() != IndicatorDTO.AGGREGATE_MULTINOMIAL) {
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
				return IndicatorNumberFormats.getNumberFormat(indicator).format(value.getValue());
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
