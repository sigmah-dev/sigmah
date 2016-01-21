package org.sigmah.server.domain.value;

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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * File Version domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.FILE_VERSION_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
																																																					EntityConstants.FILE_VERSION_COLUMN_ID_FILE,
																																																					EntityConstants.FILE_VERSION_COLUMN_VERSION_NUMBER
})
})
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.FILE_VERSION_HIDE_DELETED_CONDITION)
})
public class FileVersion extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1143785858180618602L;

	// Use an Integer as identifier to be compatible with the EntityDTO
	// interface and the command handlers.
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FILE_VERSION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_VERSION_NUMBER, nullable = false)
	@NotNull
	private Integer versionNumber;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_PATH, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String path;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.FILE_VERSION_COLUMN_ADDED_DATE, nullable = false)
	@NotNull
	private Date addedDate;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_SIZE, nullable = false)
	@NotNull
	private Long size;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_COMMENTS, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String comments;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_NAME, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String name;

	@Column(name = EntityConstants.FILE_VERSION_COLUMN_EXTENSION, nullable = true, length = EntityConstants.FILE_VERSION_EXTENSION_MAX_SIZE)
	@Size(max = EntityConstants.FILE_VERSION_EXTENSION_MAX_SIZE)
	private String extension;

	// Deletion informations.
	@Column(name = EntityConstants.FILE_VERSION_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.FILE_META_COLUMN_ID, nullable = false)
	@NotNull
	private File parentFile;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.FILE_VERSION_COLUMN_ID_AUTHOR, nullable = false)
	@NotNull
	private User author;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("versionNumber", versionNumber);
		builder.append("path", path);
		builder.append("addedDate", addedDate);
		builder.append("size", size);
		builder.append("comments", comments);
		builder.append("name", name);
		builder.append("extension", extension);
		builder.append("dateDeleted", dateDeleted);
	}

	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setParentFile(File parentFile) {
		this.parentFile = parentFile;
	}

	public File getParentFile() {
		return parentFile;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public User getAuthor() {
		return author;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getSize() {
		return size;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}
}
