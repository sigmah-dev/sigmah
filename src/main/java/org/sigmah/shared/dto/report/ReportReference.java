package org.sigmah.shared.dto.report;

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
import org.sigmah.shared.dto.base.AbstractModelDataDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.dto.value.ListableValue;

/**
 * <p>
 * ReportReference.
 * </p>
 * <p>
 * This DTO represents either:
 * <ul>
 * <li>a {@link FileVersionDTO} instance.</li>
 * <li>a {@link ProjectReportDTO} instance.</li>
 * </ul>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportReference extends AbstractModelDataDTO implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1736989091550004973L;

	// DTO attributes keys.
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String PHASE_NAME = "phaseName";
	public static final String FLEXIBLE_ELEMENT_LABEL = "flexibleElementLabel";
	public static final String LAST_EDIT_DATE = "lastEditDate";
	public static final String EDITOR_NAME = "editorName";

	/**
	 * If the {@link ReportReference} represents an attached document, this attribute contains the corresponding file
	 * version data.
	 */
	private FileVersionDTO fileVersion;

	public ReportReference() {
		// Serialization.
	}

	public ReportReference(final FileVersionDTO fileVersion) {
		this.fileVersion = fileVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(ID, getId());
		builder.append(NAME, getName());
		builder.append(PHASE_NAME, getPhaseName());
		builder.append(FLEXIBLE_ELEMENT_LABEL, getFlexibleElementLabel());
		builder.append(LAST_EDIT_DATE, getLastEditDate());
		builder.append(EDITOR_NAME, getEditorName());
		builder.append("document", isDocument());
	}

	/**
	 * Returns if the current report reference represents a file version document.
	 * 
	 * @return {@code true} if the current report reference represents a file version document, {@code false} if it
	 *         represents a project report.
	 */
	public boolean isDocument() {
		return getFileVersion() != null;
	}

	/**
	 * Returns the current {@link ReportReference} file version data.
	 * 
	 * @return The current report reference file version data, or {@code null} if this {@link ReportReference} represents
	 *         a {@link ProjectReportDTO}.
	 */
	public FileVersionDTO getFileVersion() {
		return fileVersion;
	}

	public Integer getId() {
		return get(ID);
	}

	public void setId(Integer id) {
		this.set(ID, id);
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		this.set(NAME, name);
	}

	public String getPhaseName() {
		return get(PHASE_NAME);
	}

	public void setPhaseName(String phaseName) {
		this.set(PHASE_NAME, phaseName);
	}

	public String getFlexibleElementLabel() {
		return get(FLEXIBLE_ELEMENT_LABEL);
	}

	public void setFlexibleElementLabel(String label) {
		this.set(FLEXIBLE_ELEMENT_LABEL, label);
	}

	public Date getLastEditDate() {
		return get(LAST_EDIT_DATE);
	}

	public void setLastEditDate(Date date) {
		this.set(LAST_EDIT_DATE, date);
	}

	public String getEditorName() {
		return get(EDITOR_NAME);
	}

	public void setEditorName(String editorName) {
		this.set(EDITOR_NAME, editorName);
	}

}
