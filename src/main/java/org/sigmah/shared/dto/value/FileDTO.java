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


import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.google.gwt.core.client.GWT;

/**
 * FileDTO.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class FileDTO extends AbstractModelDataEntityDTO<Integer> implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4655699567620520204L;

	/**
	 * File version(s) loading scopes.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum LoadingScope {

		// Enum values are used in backup archives file names, they should not be modified!

		/**
		 * Loads all versions a a file.
		 */// Enum values are used in backup archives file names, they should not be modified!

		/**
		 * Loads all versions a a file.
		 */
		ALL_VERSIONS,

		/**
		 * Loads only last version of a file.
		 */
		LAST_VERSION,
		
		/**
		 * Loads only the last version of the not deleted files.
		 */
		LAST_VERSION_FROM_NOT_DELETED_FILES;

		/**
		 * Returns the given {@code value} corresponding {@link LoadingScope}.
		 * 
		 * @param value
		 *          The loading scope string value.
		 * @return The given {@code value} corresponding {@link LoadingScope}, or {@code null}.
		 */
		public static LoadingScope fromString(final String value) {
			try {

				return LoadingScope.valueOf(value.toUpperCase());

			} catch (final Exception e) {
				return null;
			}
		}

		/**
		 * Returns the given {@code scope} corresponding i18n name.
		 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
		 * 
		 * @param scope
		 *          The loading scope.
		 * @return the given {@code scope} corresponding i18n name, or {@code null}.
		 */
		public static String getName(final LoadingScope scope) {

			if (scope == null) {
				return null;
			}

			if (!GWT.isClient()) {
				return scope.name();
			}

			switch (scope) {
				case ALL_VERSIONS:
					return I18N.CONSTANTS.backupManagementAllVersion();
				case LAST_VERSION:
					return I18N.CONSTANTS.backupManagementOneVersion();
				default:
					return scope.name();
			}
		}
	}

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.File";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String VERSIONS = "versions";
	
	public static final String DATE = "date";
	public static final String AUTHOR = "author";
	public static final String VERSION = "version";

	private FileVersionDTO lastVersion;

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
		builder.append(NAME, getName());
	}

	/**
	 * Returns the version with the given number or <code>null</code> if there isn't a version with this number.
	 * 
	 * @return The version with the given number, <code>null</code> otherwise.
	 */
	public FileVersionDTO getVersion(int versionNumber) {

		final List<FileVersionDTO> versions = getVersions();

		if (versions == null || versions.isEmpty() || versionNumber <= 0) {
			return null;
		}

		// Searches the version number.
		for (final FileVersionDTO version : versions) {
			if (version.getVersionNumber() == versionNumber) {
				return version;
			}
		}

		return null;
	}

	/**
	 * Returns the last version (with the higher version number).
	 * 
	 * @return the last version.
	 */
	public FileVersionDTO getLastVersion() {

		if (lastVersion == null) {

			final List<FileVersionDTO> versions = getVersions();

			if (versions == null || versions.isEmpty()) {
				lastVersion = null;
			}

			// Searches the max version number which identifies the last
			// version.
			int index = 0;
			int maxVersionNumber = versions.get(index).getVersionNumber();
			for (int i = 1; i < versions.size(); i++) {
				if (versions.get(i).getVersionNumber() > maxVersionNumber) {
					index = i;
				}
			}

			lastVersion = versions.get(index);
		}

		return lastVersion;
	}

	// File's name
	public String getName() {
		return (String) get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Reference to file's versions list
	public List<FileVersionDTO> getVersions() {
		return get(VERSIONS);
	}

	public void setVersions(List<FileVersionDTO> versions) {
		set(VERSIONS, versions);
	}

}
