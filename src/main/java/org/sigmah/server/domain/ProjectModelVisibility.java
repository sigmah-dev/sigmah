package org.sigmah.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * <p>
 * Project model visibility domain entity.
 * </p>
 * <p>
 * Sets the visibility of a project model for an organization and the type of this model for it.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_MODEL_VISIBILITY_TABLE)
public class ProjectModelVisibility extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6641538192146900691L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_MODEL_VISIBILITY_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PROJECT_MODEL_VISIBILITY_COLUMN_TYPE)
	@Enumerated(EnumType.STRING)
	private ProjectModelType type;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ID, nullable = false)
	@NotNull
	private Organization organization;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID, nullable = false)
	@NotNull
	private ProjectModel model;

	public ProjectModelVisibility() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("type", type);
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

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public ProjectModel getModel() {
		return model;
	}

	public void setModel(ProjectModel model) {
		this.model = model;
	}

	public ProjectModelType getType() {
		return type;
	}

	public void setType(ProjectModelType type) {
		this.type = type;
	}

}
