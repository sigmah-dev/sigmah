package org.sigmah.shared.dto.value;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity element.FileVersion.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FileVersionDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 831743691477321862L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.FileVersion";

	// DTO 'base' attributes keys.
	public static final String VERSION_NUMBER = "versionNumber";
	public static final String PATH = "path";
	public static final String ADDED_DATE = "addedDate";
	public static final String SIZE = "size";
	public static final String AUTHOR_NAME = "authorName";
	public static final String AUTHOR_FIRST_NAME = "authorFirstName";
	public static final String NAME = "name";
	public static final String EXTENSION = "extension";
	public static final String AVAILABLE = "available";

	public FileVersionDTO() {
		// Serialization.
	}

	public FileVersionDTO(final Integer id) {
		setId(id);
	}

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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(VERSION_NUMBER, getVersionNumber());
		builder.append(PATH, getPath());
		builder.append(ADDED_DATE, getAddedDate());
		builder.append(SIZE, getSize());
		builder.append(AUTHOR_NAME, getAuthorName());
		builder.append(AUTHOR_FIRST_NAME, getAuthorFirstName());
		builder.append(NAME, getName());
		builder.append(EXTENSION, getExtension());
	}

	// Version's number
	public int getVersionNumber() {
		return (Integer) get(VERSION_NUMBER);
	}

	public void setVersionNumber(int versionNumber) {
		set(VERSION_NUMBER, versionNumber);
	}

	// Version's path
	public String getPath() {
		return (String) get(PATH);
	}

	public void setPath(String path) {
		set(PATH, path);
	}

	// Version's added date
	public Date getAddedDate() {
		return (Date) get(ADDED_DATE);
	}

	public void setAddedDate(Date addedDate) {
		set(ADDED_DATE, addedDate);
	}

	// Version's added date
	public long getSize() {
		return (Long) get(SIZE);
	}

	public void setSize(long size) {
		set(SIZE, size);
	}

	// Version's author name
	public String getAuthorName() {
		return (String) get(AUTHOR_NAME);
	}

	public void setAuthorName(String authorName) {
		set(AUTHOR_NAME, authorName);
	}

	// Version's author first name
	public String getAuthorFirstName() {
		return (String) get(AUTHOR_FIRST_NAME);
	}

	public void setAuthorFirstName(String authorFirstName) {
		set(AUTHOR_FIRST_NAME, authorFirstName);
	}

	// Version's name.
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Version's extension.
	public String getExtension() {
		return get(EXTENSION);
	}

	public void setExtension(String extension) {
		set(EXTENSION, extension);
	}
	
	public boolean isAvailable() {
		final Boolean available = get(AVAILABLE);
		return available != null && available;
	}
	
	public void setAvailable(boolean available) {
		set(AVAILABLE, available);
	}
}
