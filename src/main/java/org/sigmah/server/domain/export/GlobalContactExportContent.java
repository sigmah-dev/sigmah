package org.sigmah.server.domain.export;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.GLOBAL_CONTACT_EXPORT_CONTENT_TABLE)
public class GlobalContactExportContent extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8895472045739369629L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_CONTACT_MODEL_NAME, nullable = false, length = EntityConstants.GLOBAL_EXPORT_CONTENT_PROJECT_MODEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.GLOBAL_EXPORT_CONTENT_PROJECT_MODEL_NAME_MAX_LENGTH)
	private String contactModelName;

	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_CSV_CONTENT, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String csvContent;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_GLOBAL_EXPORT_ID, nullable = false)
	@NotNull
	private GlobalContactExport globalContactExport;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("contactModelName", contactModelName);
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

	public String getContactModelName() {
		return contactModelName;
	}

	public void setContactModelName(String contactModelName) {
		this.contactModelName = contactModelName;
	}

	public String getCsvContent() {
		return csvContent;
	}

	public void setCsvContent(String csvContent) {
		this.csvContent = csvContent;
	}

	public GlobalContactExport getGlobalContactExport() {
		return globalContactExport;
	}

	public void setGlobalContactExport(GlobalContactExport globalContactExport) {
		this.globalContactExport = globalContactExport;
	}

}
