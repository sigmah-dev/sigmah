package org.sigmah.shared.dto.report;

import java.util.Date;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * Represents a project report.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectReportDTO implements EntityDTO<Integer> {
	
	/**
	 * Project report draft fake entity name.
	 */
	public static final String ENTITY_NAME_DRAFT = "ProjectReportDraft";

	/**
	 * Entity name.
	 */
	public static final String ENTITY_NAME = "report.ProjectReport";

	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String VERSION_ID = "versionId";
	public static final String PROJECT_ID = "projectId";
	public static final String ORGUNIT_ID = "orgUnitId";
	public static final String PHASE_NAME = "phaseName";
	public static final String DRAFT = "draft";
	public static final String REPORT_MODEL_ID = "reportModelId";
	public static final String FLEXIBLE_ELEMENT_ID = "flexibleElementId";
	public static final String CONTAINER_ID = "containerId";
	public static final String MULTIPLE = "multiple";
	public static final String REPORT_ID = "reportId";
	public static final String CURRENT_PHASE = "currentPhase";

	private Integer id;
	private Integer versionId;
	private Integer projectId;
	private Integer orgUnitId;
	private String name;
	private String phaseName;
	private List<ProjectReportSectionDTO> sections;

	private boolean draft;

	private Date lastEditDate;
	private String editorName;

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
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append(ID, getId());
		builder.append(NAME, getName());
		builder.append(VERSION_ID, getVersionId());
		builder.append(PHASE_NAME, getPhaseName());
		builder.append(ORGUNIT_ID, getOrgUnitId());
		builder.append(PROJECT_ID, getProjectId());
		builder.append(DRAFT, isDraft());

		return builder.toString();
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public Integer getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(Integer orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public List<ProjectReportSectionDTO> getSections() {
		return sections;
	}

	public void setSections(List<ProjectReportSectionDTO> sections) {
		this.sections = sections;
	}

	public boolean isDraft() {
		return draft;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public Date getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}
}
