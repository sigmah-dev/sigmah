package org.sigmah.server.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Organization domain entity.
 * </p>
 * <p>
 * Represents a NGO.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORGANIZATION_TABLE)
public class Organization extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8394175229744679675L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORGANIZATION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.ORGANIZATION_COLUMN_NAME, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String name;

	@Column(name = EntityConstants.ORGANIZATION_COLUMN_LOGO, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String logo;

	// constructeur

	public Organization() {
	}

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// Trick: using '@ManyToOne' to avoid automatic load of the object (see '@OneToOne' lazy issue).
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.ORGANIZATION_COLUMN_ROOT_ORG_UNIT, nullable = true)
	private OrgUnit root;

	@OneToMany(mappedBy = "organization", cascade = CascadeType.ALL)
	private List<OrgUnit> orgUnit;

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
		builder.append("name", name);
		builder.append("logo", logo);
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public OrgUnit getRoot() {
		return root;
	}

	public void setRoot(OrgUnit root) {
		this.root = root;
	}

	public List<OrgUnit> getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(List<OrgUnit> orgUnit) {
		this.orgUnit = orgUnit;
	}

}
