/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.Locale;

import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;

import com.google.inject.Injector;


/*
 * Shared project data between calc/excel tempates
 * @author sherzod
 */
public class ProjectSynthesisData extends BaseSynthesisData{
 
 	 private final Project project;
  
 	 public ProjectSynthesisData(			
			final Exporter exporter,
			final Integer projectId,
			final Injector injector,
			final Locale locale) {
		super(exporter, injector,locale);		 
		 project=entityManager.find(Project.class, projectId);	
 				
   	}

 	@Override
	public Project getProject() {
		return project;
	}

	@Override
	public OrgUnit getOrgUnit() {
		return null;
	}
 	
 	

}
