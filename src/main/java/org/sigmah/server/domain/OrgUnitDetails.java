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


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Org unit details domain entity.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.ORG_UNIT_DETAILS_TABLE)
public class OrgUnitDetails extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8741155131127565295L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.ORG_UNIT_DETAILS_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToOne
	@JoinColumn(name = EntityConstants.ORG_UNIT_DETAILS_COLUMN_ORG_UNIT_MODEL)
	private OrgUnitModel orgUnitModel;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = EntityConstants.LAYOUT_COLUMN_ID, nullable = false)
	@NotNull
	private Layout layout;

	public OrgUnitDetails() {
	}

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param orgUnitModel
	 *          the org-unit model.
	 */
	public void resetImport(final OrgUnitModel orgUnitModel, boolean keepPrivacyGroups) {
		this.id = null;
		this.orgUnitModel = orgUnitModel;
		if (this.layout != null) {
			this.layout.resetImport(keepPrivacyGroups);
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

	public OrgUnitModel getOrgUnitModel() {
		return orgUnitModel;
	}

	public void setOrgUnitModel(OrgUnitModel orgUnitModel) {
		this.orgUnitModel = orgUnitModel;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

}
