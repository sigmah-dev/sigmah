package org.sigmah.server.domain.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Project Report Model domain entity.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_REPORT_MODEL_TABLE)
public class ProjectReportModel extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6595694881605806219L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_COLUMN_NAME)
	@Size(max = EntityConstants.PROJECT_REPORT_MODEL_NAME_MAX_LENGTH)
	private String name;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "projectModelId", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("index ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private List<ProjectReportModelSection> sections;

	@ManyToOne
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Organization organization;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("name", name);
	}

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param modelesReset
	 *          the map of the reseted objects (original object, transformed object).
	 * @param modelesImport
	 *          the list of object that have been transformed or are being transformed.
	 */
	public void resetImport(HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		if (sections != null) {
			for (ProjectReportModelSection section : sections) {
				section.resetImport(this.id, null, modelesReset, modelesImport);
			}
		}
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProjectReportModelSection> getSections() {
		return sections;
	}

	public void setSections(List<ProjectReportModelSection> sections) {
		this.sections = sections;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
