package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Framework;
import org.sigmah.server.domain.FrameworkFulfillment;

public interface FrameworkFulfillmentDAO extends DAO<FrameworkFulfillment, Integer> {
	List<FrameworkFulfillment> findByProjectModelId(Integer projectModelId);
}
