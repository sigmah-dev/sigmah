package org.sigmah.server.dao;

import java.util.Date;
import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportSettings;

/**
 * Global Export DAO interface.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface GlobalExportDAO extends DAO<GlobalExport, Integer> {

	List<ProjectModel> getProjectModelsByOrganization(Organization organization);

	List<GlobalExport> getGlobalExports(Date from, Date to);

	List<GlobalExport> getOlderExports(Date oldDate, Organization organization);

	List<GlobalExportSettings> getGlobalExportSettings();

}
