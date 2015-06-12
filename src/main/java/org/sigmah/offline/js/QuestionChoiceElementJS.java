package org.sigmah.offline.js;

import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

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
		
		if(getCategoryElementJS() != null) {
			final CategoryElementDTO categoryElement = getCategoryElementJS().toDTO();
			final CategoryTypeDTO categoryType = getCategoryTypeJS().toDTO();
			
			categoryElement.setParentCategoryDTO(categoryType);
			questionChoiceElementDTO.setCategoryElement(categoryElement);
		}
		
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
			
			// BUGFIX #704: Saving a local copy of the category and the category type in JS form.
			setCategoryElementJS(CategoryElementJS.toJavaScript(categoryElement));
			setCategoryTypeJS(CategoryTypeJS.toJavaScript(categoryElement.getParentCategoryDTO()));
		}
	}
	
	public native void setCategoryElement(int categoryElement) /*-{
		this.categoryElement = categoryElement;
	}-*/;
	
	public native CategoryElementJS getCategoryElementJS() /*-{
		return this.categoryElementJS;
	}-*/;
	
	public native void setCategoryElementJS(CategoryElementJS categoryElementJS) /*-{
		this.categoryElementJS = categoryElementJS;
	}-*/;
	
	public native CategoryTypeJS getCategoryTypeJS() /*-{
		return this.categoryTypeJS;
	}-*/;
	
	public native void setCategoryTypeJS(CategoryTypeJS categoryTypeJS) /*-{
		this.categoryTypeJS = categoryTypeJS;
	}-*/;
}
