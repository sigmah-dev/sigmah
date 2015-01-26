package org.sigmah.offline.js;

import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class QuestionChoiceElementJS extends JavaScriptObject {
	
	protected QuestionChoiceElementJS() {
	}
	
	public static QuestionChoiceElementJS toJavaScript(QuestionChoiceElementDTO questionChoiceElementDTO) {
		final QuestionChoiceElementJS questionChoiceElementJS = Values.createJavaScriptObject(QuestionChoiceElementJS.class);
		
		questionChoiceElementJS.setId(questionChoiceElementDTO.getId());
		questionChoiceElementJS.setLabel(questionChoiceElementDTO.getLabel());
		questionChoiceElementJS.setSortOrder(questionChoiceElementDTO.getSortOrder());
		questionChoiceElementJS.setParentQuestion(questionChoiceElementDTO.getParentQuestion());
		questionChoiceElementJS.setCategoryElement(questionChoiceElementDTO.getCategoryElement());
		
		return questionChoiceElementJS;
	}
	
	public QuestionChoiceElementDTO toDTO() {
		final QuestionChoiceElementDTO questionChoiceElementDTO = new QuestionChoiceElementDTO();
		
		questionChoiceElementDTO.setId(getId());
		questionChoiceElementDTO.setLabel(getLabel());
		questionChoiceElementDTO.setSortOrder(getSortOrder());
		// FIXME: Find a way to fill the missing fields (CategoryElement)
		
		return questionChoiceElementDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native int getSortOrder() /*-{
		return this.sortOrder;
	}-*/;

	public native void setSortOrder(int sortOrder) /*-{
		this.sortOrder = sortOrder;
	}-*/;

	public native int getParentQuestion() /*-{
		return this.parentQuestion;
	}-*/;

	public void setParentQuestion(QuestionElementDTO parentQuestion) {
		if(parentQuestion != null) {
			setParentQuestion(parentQuestion.getId());
		}
	}
	
	public native void setParentQuestion(int parentQuestion) /*-{
		this.parentQuestion = parentQuestion;
	}-*/;

	public native int getCategoryElement() /*-{
		return this.categoryElement;
	}-*/;

	public void setCategoryElement(CategoryElementDTO categoryElement) {
		if(categoryElement != null) {
			setCategoryElement(categoryElement.getId());
		}
	}
	
	public native void setCategoryElement(int categoryElement) /*-{
		this.categoryElement = categoryElement;
	}-*/;
}
