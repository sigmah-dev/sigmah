package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Framework;

public interface FrameworkDAO extends DAO<Framework, Integer> {
	List<Framework> findAvailableFrameworksForOrganizationId(Integer organizationId);

	long countNotImplementedElementsByProjectModelId(Integer projectModelId);
}
