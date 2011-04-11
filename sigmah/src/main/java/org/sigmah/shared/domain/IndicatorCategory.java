package org.sigmah.shared.domain;

import java.io.Serializable;

public class IndicatorCategory implements Serializable {

	private int indicatorId;
	private double value;
	private String label;
	
	public int getIndicatorId() {
		return indicatorId;
	}
	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	
	

}
