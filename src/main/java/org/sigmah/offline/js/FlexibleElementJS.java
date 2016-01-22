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

import org.sigmah.shared.dto.element.CheckboxElementDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.CoreVersionElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.IndicatorsListElementDTO;
import org.sigmah.shared.dto.element.MessageElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.element.TripletsListElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

/**
 * Parent class for the JavaScript version of the flexible elements.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class FlexibleElementJS extends JavaScriptObject {
	
	protected FlexibleElementJS() {
	}
	
	public static FlexibleElementJS toJavaScript(FlexibleElementDTO flexibleElementDTO) {
		final FlexibleElementJS flexibleElementJS;
		
		switch(flexibleElementDTO.getElementType()) {
			case CHECKBOX:
				flexibleElementJS = CheckboxElementJS.toJavaScript((CheckboxElementDTO)flexibleElementDTO);
				break;
			case COMPUTATION:
				flexibleElementJS = ComputationElementJS.toJavaScript((ComputationElementDTO)flexibleElementDTO);
				break;
			case CORE_VERSION:
				flexibleElementJS = Values.createJavaScriptObject(FlexibleElementJS.class);
				break;
			case DEFAULT:
				flexibleElementJS = DefaultFlexibleElementJS.toJavaScript((DefaultFlexibleElementDTO)flexibleElementDTO);
				break;
			case FILES_LIST:
				flexibleElementJS = FilesListElementJS.toJavaScript((FilesListElementDTO)flexibleElementDTO);
				break;
			case INDICATORS:
				flexibleElementJS = Values.createJavaScriptObject(FlexibleElementJS.class);
				break;
			case MESSAGE:
				flexibleElementJS = MessageElementJS.toJavaScript((MessageElementDTO)flexibleElementDTO);
				break;
			case QUESTION:
				flexibleElementJS = QuestionElementJS.toJavaScript((QuestionElementDTO)flexibleElementDTO);
				break;
			case REPORT:
				flexibleElementJS = ReportElementJS.toJavaScript((ReportElementDTO)flexibleElementDTO);
				break;
			case REPORT_LIST:
				flexibleElementJS = ReportListElementJS.toJavaScript((ReportListElementDTO)flexibleElementDTO);
				break;
			case TEXT_AREA:
				flexibleElementJS = TextAreaElementJS.toJavaScript((TextAreaElementDTO)flexibleElementDTO);
				break;
			case TRIPLETS:
				flexibleElementJS = TripletsListElementJS.toJavaScript((TripletsListElementDTO)flexibleElementDTO);
				break;
			default:
				throw new UnsupportedOperationException("Given flexible element type is not supported yet: " + flexibleElementDTO.getElementType());
		}
		
		flexibleElementJS.setId(flexibleElementDTO.getId());
		flexibleElementJS.setElementType(flexibleElementDTO.getElementType());
		flexibleElementJS.setLabel(flexibleElementDTO.getLabel());
		flexibleElementJS.setValidates(flexibleElementDTO.getValidates());
//		flexibleElementJS.setFilledIn(flexibleElementDTO.isFilledIn());
		flexibleElementJS.setAmendable(flexibleElementDTO.getAmendable());
		flexibleElementJS.setExportable(flexibleElementDTO.getExportable());
		flexibleElementJS.setGloballyExportable(flexibleElementDTO.getGloballyExportable());
		flexibleElementJS.setHistorable(flexibleElementDTO.isHistorable());
		flexibleElementJS.setPrivacyGroup(flexibleElementDTO.getPrivacyGroup());
		flexibleElementJS.setGroup(flexibleElementDTO.getGroup());
		flexibleElementJS.setConstraint(flexibleElementDTO.getConstraint());
		flexibleElementJS.setBannerConstraint(flexibleElementDTO.getBannerConstraint());
		
		return flexibleElementJS;
	}
	
	public final FlexibleElementDTO toDTO() {
		final FlexibleElementDTO flexibleElementDTO;
		
		switch(getElementTypeEnum()) {
			case CHECKBOX:
				flexibleElementDTO = ((CheckboxElementJS)this).toCheckboxElementDTO();
				break;
			case COMPUTATION:
				flexibleElementDTO = ((ComputationElementJS)this).toComputationElementDTO();
				break;
			case CORE_VERSION:
				flexibleElementDTO = new CoreVersionElementDTO();
				break;
			case DEFAULT:
				flexibleElementDTO = ((DefaultFlexibleElementJS)this).toDefaultFlexibleElementDTO();
				break;
			case FILES_LIST:
				flexibleElementDTO = ((FilesListElementJS)this).toFilesListElementDTO();
				break;
			case INDICATORS:
				flexibleElementDTO = new IndicatorsListElementDTO();
				break;
			case MESSAGE:
				flexibleElementDTO = ((MessageElementJS)this).toMessageElementDTO();
				break;
			case QUESTION:
				flexibleElementDTO = ((QuestionElementJS)this).toQuestionElementDTO();
				break;
			case REPORT:
				flexibleElementDTO = ((ReportElementJS)this).toReportElementDTO();
				break;
			case REPORT_LIST:
				flexibleElementDTO = ((ReportListElementJS)this).toReportListElementDTO();
				break;
			case TEXT_AREA:
				flexibleElementDTO = ((TextAreaElementJS)this).toTextAreaElementDTO();
				break;
			case TRIPLETS:
				flexibleElementDTO = ((TripletsListElementJS)this).toTripletsListElementDTO();
				break;
			default:
				throw new UnsupportedOperationException("Given flexible element type is not supported yet: " + getElementType());
		}
		
		flexibleElementDTO.setId(getId());
		flexibleElementDTO.setLabel(getLabel());
		flexibleElementDTO.setValidates(isValidates());
//		dto.setFilledIn(isFilledIn());
		flexibleElementDTO.setAmendable(isAmendable());
		flexibleElementDTO.setExportable(isExportable());
		flexibleElementDTO.setGloballyExportable(isGloballyExportable());
		flexibleElementDTO.setHistorable(isHistorable());
		flexibleElementDTO.setPrivacyGroup(getPrivacyGroupDTO());
		
		return flexibleElementDTO;
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public final native String getLabel() /*-{
		return this.label;
	}-*/;

	public final native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public final native boolean isValidates() /*-{
		return this.validates;
	}-*/;

	public final native void setValidates(boolean validates) /*-{
		this.validates = validates;
	}-*/;

	public final native boolean isFilledIn() /*-{
		return this.filledIn;
	}-*/;

	public final native void setFilledIn(boolean filledIn) /*-{
		this.filledIn = filledIn;
	}-*/;

	public final native boolean isAmendable() /*-{
		return this.amendable;
	}-*/;

	public final native void setAmendable(boolean amendable) /*-{
		this.amendable = amendable;
	}-*/;

	public final native boolean isExportable() /*-{
		return this.exportable;
	}-*/;

	public final native void setExportable(boolean exportable) /*-{
		this.exportable = exportable;
	}-*/;

	public final native boolean isGloballyExportable() /*-{
		return this.globallyExportable;
	}-*/;

	public final native void setGloballyExportable(boolean globallyExportable) /*-{
		this.globallyExportable = globallyExportable;
	}-*/;

	public final native boolean isHistorable() /*-{
		return this.historable;
	}-*/;

	public final native void setHistorable(boolean historable) /*-{
		this.historable = historable;
	}-*/;

	public final native PrivacyGroupJS getPrivacyGroup() /*-{
		return this.privacyGroup;
	}-*/;

	public final native void setPrivacyGroup(PrivacyGroupJS privacyGroup) /*-{
		this.privacyGroup = privacyGroup;
	}-*/;

	public final PrivacyGroupDTO getPrivacyGroupDTO() {
		if(getPrivacyGroup() != null) {
			return getPrivacyGroup().toDTO();
		}
		return null;
	}
	
	public final void setPrivacyGroup(PrivacyGroupDTO privacyGroup) {
		if(privacyGroup != null) {
			setPrivacyGroup(PrivacyGroupJS.toJavaScript(privacyGroup));
		}
	}

	public final native String getElementType() /*-{
		return this.elementType;
	}-*/;

	public final void setElementType(ElementTypeEnum elementType) {
		if(elementType != null) {
			setElementType(elementType.name());
		}
	}
	
	public final native void setElementType(String elementType) /*-{
		this.elementType = elementType;
	}-*/;
	
	public final ElementTypeEnum getElementTypeEnum() {
		if(getElementType() != null) {
			return ElementTypeEnum.valueOf(getElementType());
		} else {
			return null;
		}
	}

	public final native int getGroup() /*-{
		return this.group;
	}-*/;

	public final void setGroup(LayoutGroupDTO group) {
		if(group != null) {
			setGroup(group.getId());
		}
	}
	
	public final native void setGroup(int group) /*-{
		this.group = group;
	}-*/;

	public final Integer getConstraint() {
		return Values.getInteger(this, "constraint");
	}
	
	public final void setConstraint(LayoutConstraintDTO constraint) {
		if(constraint != null) {
			Values.setInteger(this, "constraint", constraint.getId());
		}
	}
	
	public final Integer getBannerConstraint() {
		return Values.getInteger(this, "bannerConstraint");
	}
	
	public final void setBannerConstraint(LayoutConstraintDTO constraint) {
		if(constraint != null) {
			Values.setInteger(this, "bannerConstraint", constraint.getId());
		}
	}
}
