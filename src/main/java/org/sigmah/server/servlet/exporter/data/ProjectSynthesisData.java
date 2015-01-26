package org.sigmah.server.servlet.exporter.data;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.exporter.base.Exporter;

import com.google.inject.Injector;

/**
 * Shared project data between calc/excel tempates
 * 
 * @author sherzod (v1.3)
 */
public class ProjectSynthesisData extends BaseSynthesisData {

	private final Project project;

	public ProjectSynthesisData(final Exporter exporter, final Integer projectId, final Injector injector) {

		super(exporter, injector);
		project = entityManager.find(Project.class, projectId);

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
