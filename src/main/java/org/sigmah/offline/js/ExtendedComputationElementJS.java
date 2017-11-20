package org.sigmah.offline.js;

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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import java.util.Collections;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Extended version of the <code>ComputationElementJS</code>.
 * <p>
 * Designed to simplify the search with IndexedDB and to allow extended 
 * computations (defined by mantis issue #866) to work offline.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public final class ExtendedComputationElementJS extends JavaScriptObject {
	
	/**
	 * Creates a new <code>ComputationJS</code> object with the given element.
	 * 
	 * @param computationElement
	 *			Computation element to map.
	 * @return A new <code>ComputationJS</code>.
	 */
	public static ExtendedComputationElementJS toJavaScript(final ComputationElementDTO computationElement) {
		
		final JsArrayString dependencies = Values.createJavaScriptArray(JsArrayString.class);
		int contribution = 0;
		
		final Computation computation = Computations.parse(computationElement.getRule(), computationElement.getGroup().getId(), Collections.<FlexibleElementDTO>emptyList());
		for (final Dependency dependency : computation.getDependencies()) {
			if (dependency instanceof CollectionDependency) {
				dependencies.push(((CollectionDependency) dependency).flexibleElementString());
			}
			
			if (dependency instanceof ContributionDependency) {
				contribution = 1;
			}
		}
		
		final ExtendedComputationElementJS computationJS = Values.createJavaScriptObject();
		computationJS.setId(computationElement.getId());
		computationJS.setLabel(computationElement.getLabel());
		computationJS.setCode(computationElement.getCode());
		computationJS.setRule(computationElement.getRule());
		computationJS.setContribution(contribution);
		computationJS.setDependencies(dependencies);
		computationJS.setProjectModel(computationElement.getProjectModel());
		
		return computationJS;
	}
	
	/**
	 * Maps this JavaScript object to a new {@link ComputationElementDTO}.
	 * 
	 * @return A new ComputationElementDTO.
	 */
	public ComputationElementDTO toDTO() {
		final ComputationElementDTO dto = new ComputationElementDTO();
		dto.setId(getId());
		dto.setLabel(getLabel());
		dto.setCode(getCode());
		dto.setRule(getRule());
		dto.setMinimumValue(getMinimumValue());
		dto.setMaximumValue(getMaximumValue());
		dto.setProjectModel(getProjectModel());
		return dto;
	}
	
	/**
	 * Empty protected constructor. Required for subclasses of JavaScriptObject.
	 */
	protected ExtendedComputationElementJS() {
		// Empty.
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public final native String getLabel() /*-{
		return this.label;
	}-*/;

	public final native void setLabel(String label) /*-{
		this.label = label;
	}-*/;
	
	public final native String getCode() /*-{
		return this.code;
	}-*/;

	public final native void setCode(String code) /*-{
		this.code = code;
	}-*/;

	public native String getRule() /*-{
		return this.rule;
	}-*/;

	public native void setRule(String rule) /*-{
		this.rule = rule;
	}-*/;
	
	public native String getMinimumValue() /*-{
		return this.minimumValue;
	}-*/;

	public native void setMinimumValue(String minimumValue) /*-{
		this.minimumValue = minimumValue;
	}-*/;
	
	public native String getMaximumValue() /*-{
		return this.maximumValue;
	}-*/;

	public native void setMaximumValue(String maximumValue) /*-{
		this.maximumValue = maximumValue;
	}-*/;

	public native int getContribution() /*-{
		return this.contribution;
	}-*/;

	public native void setContribution(int contribution) /*-{
		this.contribution = contribution;
	}-*/;

	public native JsArrayString getDependencies() /*-{
		return this.dependencies;
	}-*/;

	public native void setDependencies(JsArrayString dependencies) /*-{
		this.dependencies = dependencies;
	}-*/;
	
	public ProjectModelDTO getProjectModel() {
		final ProjectModelJS projectModelJS = Values.getJavaScriptObject(this, ComputationElementDTO.PROJECT_MODEL);
		if (projectModelJS != null) {
			return projectModelJS.toDTO();
		} else {
			return null;
		}
	}

	public void setProjectModel(ProjectModelDTO projectModel) {
		Values.setJavaScriptObject(this, ComputationElementDTO.PROJECT_MODEL, ProjectModelJS.toJavaScript(projectModel, true));
	}
	
}
