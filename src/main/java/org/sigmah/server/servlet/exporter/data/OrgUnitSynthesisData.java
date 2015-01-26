package org.sigmah.server.servlet.exporter.data;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.servlet.exporter.base.Exporter;

import com.google.inject.Injector;

public class OrgUnitSynthesisData extends BaseSynthesisData {

	private final OrgUnit orgUnit;

	public OrgUnitSynthesisData(final Exporter exporter, final Integer orgUnitId, final Injector injector) {
		super(exporter, injector);
		orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
	}

	@Override
	public OrgUnit getOrgUnit() {
		return orgUnit;
	}

	@Override
	public Project getProject() {
		return null;
	}

}
