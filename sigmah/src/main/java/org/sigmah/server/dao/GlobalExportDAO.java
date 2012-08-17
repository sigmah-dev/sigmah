package org.sigmah.server.dao;

import java.util.Date;
import java.util.List;

import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.domain.export.GlobalExportSettings;

/*
 * DAO interface 
 * 
 * @author sherzod
 */
public interface GlobalExportDAO {
	
	public List<ProjectModel> getProjectModelsByOrganization(Organization organization);
	public List<GlobalExportSettings> getGlobalExportSettings() ;
	public List<Project> getProjects(List<ProjectModel> pmodels);
	public List<GlobalExport> getGlobalExports(Date from,Date to);
	public GlobalExportSettings getGlobalExportSettingsByOrganization(Integer id);
	public List<GlobalExport> getOlderExports(Date oldDate,Organization organization);
}
