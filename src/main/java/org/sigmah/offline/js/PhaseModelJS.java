package org.sigmah.offline.js;

import java.util.List;

import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.PhaseModelDefinitionDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PhaseModelJS extends JavaScriptObject {

	protected PhaseModelJS() {
	}

	public static PhaseModelJS toJavaScript(PhaseModelDTO phaseModelDTO) {
		final PhaseModelJS phaseModelJS = Values.createJavaScriptObject(PhaseModelJS.class);

		phaseModelJS.setId(phaseModelDTO.getId());
		phaseModelJS.setName(phaseModelDTO.getName());
		phaseModelJS.setParentProjectModel(phaseModelDTO.getParentProjectModel());
		phaseModelJS.setLayout(phaseModelDTO.getLayout());
		phaseModelJS.setSuccessors(phaseModelDTO.getSuccessors());
		phaseModelJS.setDisplayOrder(phaseModelDTO.getDisplayOrder());
		phaseModelJS.setDefinition(phaseModelDTO.getDefinition());
		phaseModelJS.setGuide(phaseModelDTO.getGuide());
		phaseModelJS.setRoot(phaseModelDTO.getRoot());

		return phaseModelJS;
	}

	public PhaseModelDTO toDTO() {
		final PhaseModelDTO phaseModelDTO = new PhaseModelDTO();

		phaseModelDTO.setId(getId());
		phaseModelDTO.setName(getName());
		phaseModelDTO.setLayout(getLayoutDTO());
		phaseModelDTO.setDisplayOrder(getDisplayOrder());
		phaseModelDTO.setDefinition(getDefinitionDTO());
		phaseModelDTO.setGuide(getGuide());

		if (hasRoot()) {
			phaseModelDTO.setRoot(isRoot());
		}

		return phaseModelDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native int getParentProjectModel() /*-{
		return this.parentProjectModel;
	}-*/;

	public void setParentProjectModel(ProjectModelDTO projectModelDTO) {
		if (projectModelDTO != null) {
			setParentProjectModel(projectModelDTO.getId());
		}
	}

	public native void setParentProjectModel(int parentProjectModel) /*-{
		this.parentProjectModel = parentProjectModel;
	}-*/;

	public native LayoutJS getLayout() /*-{
		return this.layout;
	}-*/;

	public LayoutDTO getLayoutDTO() {
		if (getLayout() != null) {
			return getLayout().toDTO();
		}
		return null;
	}

	public void setLayout(LayoutDTO layoutDTO) {
		if (layoutDTO != null) {
			setLayout(LayoutJS.toJavaScript(layoutDTO));
		}
	}

	public native void setLayout(LayoutJS layout) /*-{
		this.layout = layout;
	}-*/;

	public native JsArrayInteger getSuccessors() /*-{
		return this.successors;
	}-*/;

	public void setSuccessors(List<PhaseModelDTO> successors) {
		if (successors != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();

			for (final PhaseModelDTO phaseModelDTO : successors) {
				array.push(phaseModelDTO.getId());
			}

			setSuccessors(array);
		}
	}

	public native void setSuccessors(JsArrayInteger successors) /*-{
		this.successors = successors;
	}-*/;

	public native int getDisplayOrder() /*-{
		return this.displayOrder;
	}-*/;

	public native void setDisplayOrder(int displayOrder) /*-{
		this.displayOrder = displayOrder;
	}-*/;

	public native boolean hasDefinition() /*-{
		return typeof this.definition != 'undefined';
	}-*/;

	public PhaseModelDefinitionDTO getDefinitionDTO() {
		if (hasDefinition()) {
			final PhaseModelDefinitionDTO definitionDTO = new PhaseModelDefinitionDTO();
			definitionDTO.setId(getDefinition());
			return definitionDTO;
		}
		return null;
	}

	public native int getDefinition() /*-{
		return this.definition;
	}-*/;

	public void setDefinition(PhaseModelDefinitionDTO definition) {
		if (definition != null) {
			setDefinition(definition.getId());
		}
	}

	public native void setDefinition(int definition) /*-{
		this.definition = definition;
	}-*/;

	public native String getGuide() /*-{
		return this.guide;
	}-*/;

	public native void setGuide(String guide) /*-{
		this.guide = guide;
	}-*/;

	public native boolean hasRoot() /*-{
		return typeof this.root != 'undefined';
	}-*/;

	public native boolean isRoot() /*-{
		return this.root;
	}-*/;

	public void setRoot(Boolean root) {
		if (root != null) {
			setRoot(root.booleanValue());
		}
	}

	public native void setRoot(boolean root) /*-{
		this.root = root;
	}-*/;
}
