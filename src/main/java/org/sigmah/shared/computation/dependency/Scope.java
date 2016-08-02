package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.dto.ProjectModelDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class Scope {
	
	private Relation relation;
	private ProjectModelDTO projectModel;

	public Scope() {
	}

	public Scope(Relation relation, String modelName) {
		this.relation = relation;
		
		if (modelName != null) {
			this.projectModel = new ProjectModelDTO();
			projectModel.setName(modelName);
		}
	}

	public Relation getRelation() {
		return relation;
	}

	public ProjectModelDTO getProjectModel() {
		return projectModel;
	}
	
	public void setProjectModelName(String modelName) {
		this.projectModel = new ProjectModelDTO();
		projectModel.setName(modelName);
	}
	
}
