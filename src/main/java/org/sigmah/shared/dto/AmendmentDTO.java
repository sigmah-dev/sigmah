package org.sigmah.shared.dto;

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
import org.sigmah.server.domain.Amendment;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.CoreVersionAction;
import org.sigmah.shared.dto.referential.CoreVersionActionType;

/**
 * DTO mapping class for {@link Amendment}s.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AmendmentDTO extends AbstractModelDataEntityDTO<Integer> implements CoreVersionAction {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 5115812364091542385L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Amendment";

	// DTO 'base' attributes keys.
	public static final String VERSION = "version";
	public static final String REVISION = "revision";
	public static final String STATE = "state";
	public static final String LOG_FRAME = "logFrame";
	public static final String TEXT = "text";
	public static final String NAME = "name";
	public static final String DATE = "history_date";

	public AmendmentDTO() {
		// Serialization.
	}

	/**
	 * Creates a new {@link AmendmentDTO} using the values defined in the given project.<br>
	 * The initialized amendment has no {@code id} ({@code null} value).
	 * 
	 * @param projectDTO
	 *          The project DTO instance.
	 */
	public AmendmentDTO(final ProjectDTO projectDTO) {
		setId(null); // No ID for this type of amendment.
		setVersion(projectDTO.getAmendmentVersion());
		setRevision(projectDTO.getAmendmentRevision());
		setName("default amendment name");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(VERSION, getVersion());
		builder.append(REVISION, getRevision());
		builder.append(STATE, getState());
		builder.append(NAME, getName());
		builder.append(DATE, getDate());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public Integer getVersion() {
		return (Integer) get(VERSION);
	}

	public void setVersion(Integer version) {
		set(VERSION, version);
	}

	public Integer getRevision() {
		return (Integer) get(REVISION);
	}

	public void setRevision(Integer revision) {
		set(REVISION, revision);
	}

	public AmendmentState getState() {
		return get(STATE);
	}

	public void setState(AmendmentState state) {
		set(STATE, state);
	}

	public LogFrameDTO getLogFrame() {
		return get(LOG_FRAME);
	}

	public void setLogFrame(LogFrameDTO logFrame) {
		set(LOG_FRAME, logFrame);
	}

	public String getName() {
		return (String) get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public void setDate(Date date) {
		set(DATE, date);
	}

	public Date getDate() {
		return (Date) get(DATE);
	}

	@Override
	public CoreVersionActionType getType() {
		return CoreVersionActionType.CORE_VERSION;
	}

	/**
	 * Initializes the "{@code text}" property with the version, name , and date.
	 * 
	 * @see AmendmentDTO#TEXT
	 */
	public final void prepareName(String name) {
		set(TEXT, name);
	}
}
