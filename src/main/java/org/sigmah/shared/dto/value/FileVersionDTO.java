package org.sigmah.shared.dto.value;

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
}
