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


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.CONTACT_ORG_UNIT_LINK_TABLE)
public class ContactUnit implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1465948408315226091L;

	@Id
	@Column(name = EntityConstants.CONTACT_COLUMN_ID, nullable = false)
	private Integer idContact;

	@Id
	@Column(name = EntityConstants.CONTACT_ORG_UNIT_COLUMN_ORG_UNIT, nullable = false)
	private Integer idOrgUnit;

	public Integer getIdContact() {
		return idContact;
	}

	public void setIdContact(Integer idContact) {
		this.idContact = idContact;
	}

	public Integer getIdOrgUnit() {
		return idOrgUnit;
	}

	public void setIdOrgUnit(Integer idOrgUnit) {
		this.idOrgUnit = idOrgUnit;
	}
}
