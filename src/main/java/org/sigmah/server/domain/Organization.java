package org.sigmah.server.domain;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
	
	@Column(name = EntityConstants.ORGANIZATION_SOLRCORE_URL, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String solrcore_url;

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

	public String getSolrcore_url() {
		return solrcore_url;
	}

	public void setSolrcore_url(String solrcore_url) {
		this.solrcore_url = solrcore_url;
	}
	
	

}
