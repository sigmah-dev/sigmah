package org.sigmah.shared.dto.report;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity report.ProjectReportModel
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportModelDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3300196624126690838L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "report.ProjectReportModel";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String SECTIONS = "sections";
	public static final String ORGANIZATION_ID = "organizationId";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
		builder.append("organizationId", getOrganizationId());
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		this.set(NAME, name);
	}

	public List<ProjectReportModelSectionDTO> getSections() {
		return get(SECTIONS);
	}

	public void setSections(List<ProjectReportModelSectionDTO> sections) {
		this.set(SECTIONS, sections);
	}

	public int getOrganizationId() {
		return (Integer) get(ORGANIZATION_ID);
	}

	public void setOrganizationId(int organizationId) {
		this.set(ORGANIZATION_ID, organizationId);
	}

}
