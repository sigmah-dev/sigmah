package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.quality.QualityCriterionDTO;
import org.sigmah.shared.dto.quality.QualityFrameworkDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class QualityCriterionJS extends JavaScriptObject {
	
	protected QualityCriterionJS() {
	}
	
	public static QualityCriterionJS toJavaScript(QualityCriterionDTO qualityCriterionDTO) {
		final QualityCriterionJS qualityCriterionJS = Values.createJavaScriptObject(QualityCriterionJS.class);
		
		qualityCriterionJS.setId(qualityCriterionDTO.getId());
		qualityCriterionJS.setCode(qualityCriterionDTO.getCode());
		qualityCriterionJS.setLabel(qualityCriterionDTO.getLabel());
		qualityCriterionJS.setQualityFramework(qualityCriterionDTO.getQualityFramework());
		qualityCriterionJS.setSubCriteria(qualityCriterionDTO.getSubCriteria());
		
		return qualityCriterionJS;
	}
	
	public QualityCriterionDTO toDTO() {
		final QualityCriterionDTO qualityCriterionDTO = new QualityCriterionDTO();
		
		qualityCriterionDTO.setId(getId());
		qualityCriterionDTO.setCode(getCode());
		qualityCriterionDTO.setLabel(getLabel());
		
		final JsArray<QualityCriterionJS> subCriteria = getSubCriteria();
		if(subCriteria != null) {
			final ArrayList<QualityCriterionDTO> list = new ArrayList<QualityCriterionDTO>();
			
			for(int index = 0; index < subCriteria.length(); index++) {
				final QualityCriterionDTO dto = subCriteria.get(index).toDTO();
				dto.setParentCriterion(qualityCriterionDTO);
				list.add(dto);
			}
			
			qualityCriterionDTO.setSubCriteria(list);
		}
		
		return qualityCriterionDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getCode() /*-{
		return this.code;
	}-*/;

	public native void setCode(String code) /*-{
		this.code = code;
	}-*/;

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native int getQualityFramework() /*-{
		return this.qualityFramework;
	}-*/;

	public void setQualityFramework(QualityFrameworkDTO qualityFramework) {
		if(qualityFramework != null) {
			setQualityFramework(qualityFramework.getId());
		}
	}
	
	public native void setQualityFramework(int qualityFramework) /*-{
		this.qualityFramework = qualityFramework;
	}-*/;
	
	public native int getParentCriterion() /*-{
		return this.parentCriterion;
	}-*/;

	public void setParentCriterion(QualityCriterionDTO parentCriterion) {
		if(parentCriterion != null) {
			setParentCriterion(parentCriterion.getId());
		}
	}
	
	public native void setParentCriterion(int parentCriterion) /*-{
		this.parentCriterion = parentCriterion;
	}-*/;

	public native JsArray<QualityCriterionJS> getSubCriteria() /*-{
		return this.subCriteria;
	}-*/;

	public void setSubCriteria(List<QualityCriterionDTO> subCriteria) {
		if(subCriteria != null) {
			final JsArray<QualityCriterionJS> array = (JsArray<QualityCriterionJS>) JavaScriptObject.createArray();
			
			for(final QualityCriterionDTO qualityCriterionDTO : subCriteria) {
				array.push(QualityCriterionJS.toJavaScript(qualityCriterionDTO));
			}
			
			setSubCriteria(array);
		}
	}
	
	public native void setSubCriteria(JsArray<QualityCriterionJS> subCriteria) /*-{
		this.subCriteria = subCriteria;
	}-*/;
}
