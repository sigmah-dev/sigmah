package org.sigmah.client.ui.view.project.projectcore;

import java.util.Collection;
import java.util.Map;

import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.data.ModelData;

public class ProjectCoreDiffLigne implements ModelData {

	private FlexibleElementDTO field;
	private Object value1;
	private Object value2;

	public ProjectCoreDiffLigne() {

		value1 = "";
		value2 = "";

	}

	public FlexibleElementDTO getField() {
		return field;
	}

	public void setField(FlexibleElementDTO field) {
		this.field = field;
	}

	public Object getValue1() {
		return value1;
	}

	public void setValue1(Object value1) {
		this.value1 = value1;
	}

	public Object getValue2() {
		return value2;
	}

	public void setValue2(Object value2) {
		this.value2 = value2;
	}

	@Override
	public <X> X get(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getPropertyNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X remove(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X> X set(String property, X value) {
		// TODO Auto-generated method stub
		return null;
	}

}
