package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.shared.dto.report.KeyQuestionDTO;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;
import org.sigmah.shared.dto.report.RichTextElementDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class ProjectReportContentJS extends JavaScriptObject {
	
	public static enum Type {
		KEY_QUESTION,
		RICH_TEXT,
		SECTION;
	}
	
	protected ProjectReportContentJS() {
	}
	
	public static ProjectReportContentJS toJavaScript(ProjectReportContent projectReportContent) {
		final ProjectReportContentJS projectReportContentJS;
		
		if(projectReportContent instanceof KeyQuestionDTO) {
			projectReportContentJS = KeyQuestionJS.toJavaScript((KeyQuestionDTO)projectReportContent);
			projectReportContentJS.setProjectReportContentType(Type.KEY_QUESTION);
			
		} else if(projectReportContent instanceof RichTextElementDTO) {
			projectReportContentJS = RichTextElementJS.toJavaScript((RichTextElementDTO)projectReportContent);
			projectReportContentJS.setProjectReportContentType(Type.RICH_TEXT);
			
		} else if(projectReportContent instanceof ProjectReportSectionDTO) {
			projectReportContentJS = ProjectReportSectionJS.toJavaScript((ProjectReportSectionDTO)projectReportContent);
			projectReportContentJS.setProjectReportContentType(Type.SECTION);
			
		} else {
			// REM: May be null, will throw a NullPointerException.
			throw new UnsupportedOperationException("Type not supported: '" + projectReportContent.getClass() + "'.");
		}
		
		return projectReportContentJS;
	}
	
	public final ProjectReportContent toDTO() {
		switch(getProjectReportContentType()) {
			case KEY_QUESTION:
				return ((KeyQuestionJS)this).createDTO();
				
			case RICH_TEXT:
				return ((RichTextElementJS)this).createDTO();
				
			case SECTION:
				return ((ProjectReportSectionJS)this).createDTO();
		}
		return null;
	}
	
	private Type getProjectReportContentType() {
		return Values.getEnum(this, "projectReportContentType", Type.class);
	}
	
	private void setProjectReportContentType(Type type) {
		Values.setEnum(this, "projectReportContentType", type);
	}
}
