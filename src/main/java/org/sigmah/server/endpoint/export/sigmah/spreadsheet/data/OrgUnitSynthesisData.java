package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.Locale;

import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;

import com.google.inject.Injector;

public class OrgUnitSynthesisData extends BaseSynthesisData{
	 
	private final OrgUnit orgUnit;
 	 	 
	public OrgUnitSynthesisData(			
			final Exporter exporter,
			final Integer orgUnitId,
			final Injector injector,
			final Locale locale) {
		super(exporter, injector,locale);
		orgUnit=entityManager.find(OrgUnit.class, orgUnitId);		 	
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
