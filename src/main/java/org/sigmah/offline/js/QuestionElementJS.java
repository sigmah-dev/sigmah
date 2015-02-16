package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.quality.QualityCriterionDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class QuestionElementJS extends FlexibleElementJS {
	
	protected QuestionElementJS() {
	}

	public static QuestionElementJS toJavaScript(QuestionElementDTO questionElementDTO) {
		final QuestionElementJS questionElementJS = Values.createJavaScriptObject(QuestionElementJS.class);
		
		questionElementJS.setChoices(questionElementDTO.getChoices());
		questionElementJS.setMultiple(ClientUtils.isTrue(questionElementDTO.getMultiple()));
		questionElementJS.setCategoryType(questionElementDTO.getCategoryType());
		questionElementJS.setQualityCriterion(questionElementDTO.getQualityCriterion());
		
		return questionElementJS;
	}
	
	public QuestionElementDTO toQuestionElementDTO() {
		final QuestionElementDTO questionElementDTO = new QuestionElementDTO();
		
		final JsArray<QuestionChoiceElementJS> choices = getChoices();
		if(choices != null) {
			final ArrayList<QuestionChoiceElementDTO> dtos = new ArrayList<QuestionChoiceElementDTO>();
			
			for(int index = 0; index < choices.length(); index++) {
				final QuestionChoiceElementDTO questionChoiceElementDTO = choices.get(index).toDTO();
				questionChoiceElementDTO.setParentQuestion(questionElementDTO);
				dtos.add(questionChoiceElementDTO);
			}
			
			questionElementDTO.setChoices(dtos);
		}
		
		questionElementDTO.setMultiple(isMultiple());
		
		if(getCategoryType() != null) {
			questionElementDTO.setCategoryType(getCategoryType().toDTO());
		}
		
		if(getQualityCriterion() != null) {
			questionElementDTO.setQualityCriterion(getQualityCriterion().toDTO());
		}
		
		return questionElementDTO;
	}

	public native JsArray<QuestionChoiceElementJS> getChoices() /*-{
		return this.choices;
	}-*/;

	public void setChoices(List<QuestionChoiceElementDTO> choices) {
		if(choices != null) {
			final JsArray<QuestionChoiceElementJS> array = (JsArray<QuestionChoiceElementJS>) JavaScriptObject.createArray();
			
			for(final QuestionChoiceElementDTO choice : choices) {
				array.push(QuestionChoiceElementJS.toJavaScript(choice));
			}
			
			setChoices(array);
		}
	}
	
	public native void setChoices(JsArray<QuestionChoiceElementJS> choices) /*-{
		this.choices = choices;
	}-*/;

	public native boolean isMultiple() /*-{
		return this.multiple;
	}-*/;

	public native void setMultiple(boolean multiple) /*-{
		this.multiple = multiple;
	}-*/;

	public native CategoryTypeJS getCategoryType() /*-{
		return this.categoryType;
	}-*/;

	public void setCategoryType(CategoryTypeDTO categoryType) {
		if(categoryType != null) {
			setCategoryType(CategoryTypeJS.toJavaScript(categoryType));
		}
	}
	
	public native CategoryTypeJS setCategoryType(CategoryTypeJS categoryType) /*-{
		this.categoryType = categoryType;
	}-*/;

	public native QualityCriterionJS getQualityCriterion() /*-{
		return this.qualityCriterion;
	}-*/;

	public void setQualityCriterion(QualityCriterionDTO qualityCriterion) {
		if(qualityCriterion != null) {
			setQualityCriterion(QualityCriterionJS.toJavaScript(qualityCriterion));
		}
	}
	
	public native void setQualityCriterion(QualityCriterionJS qualityCriterion) /*-{
		this.qualityCriterion = qualityCriterion;
	}-*/;
}
