package org.sigmah.server.domain.importation;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

/**
 * <p>
 * Importation scheme domain entity.
 * </p>
 * <p>
 * Describes an importation schema.
 * </p>
 * 
 * @author gjb
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.IMPORTATION_SCHEME_TABLE)
@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.IMPORTATION_SCHEME_HIDE_DELETED_CONDITION)
public class ImportationScheme extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9215969897646019755L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_ID, unique = true, nullable = false)
	private Integer id;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_NAME, nullable = false)
	@NotNull
	private String name;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_FILE_FORMAT, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ImportationSchemeFileFormat fileFormat;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_IMPORT_TYPE, nullable = false)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ImportationSchemeImportType importType;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_FIRST_ROW)
	private Integer firstRow;

	@Column(name = EntityConstants.IMPORTATION_SCHEME_COLUMN_SHEET_NAME)
	private String sheetName;

	@Column(name = EntityConstants.COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "importationScheme", cascade = CascadeType.ALL)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.IMPORTATION_SCHEME_HIDE_DELETED_CONDITION)
	private List<Variable> variables = new ArrayList<Variable>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("fileFormat", fileFormat);
		builder.append("importType", importType);
		builder.append("firstRow", firstRow);
		builder.append("sheetName", sheetName);
		builder.append("dateDeleted", dateDeleted);
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

	public ImportationSchemeFileFormat getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(ImportationSchemeFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public ImportationSchemeImportType getImportType() {
		return importType;
	}

	public void setImportType(ImportationSchemeImportType importType) {
		this.importType = importType;
	}

	public Integer getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

}
