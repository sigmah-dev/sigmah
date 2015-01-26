package org.sigmah.client.page.project;

import org.sigmah.shared.dto.ProjectDTO;

public interface ProjectSubPresenter extends SubPresenter {
	
    
    /**
     * Instructs the sub-presenter to load the provided project
     */
    public void loadProject(ProjectDTO project);

}
