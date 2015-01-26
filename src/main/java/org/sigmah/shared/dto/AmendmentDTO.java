package org.sigmah.shared.dto;

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.server.domain.Amendment;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;

/**
 * DTO mapping class for {@link Amendment}s.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AmendmentDTO extends AbstractModelDataEntityDTO<Integer> {

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

	/**
	 * Initializes the "{@code text}" property with the version, name , and date.
	 * 
	 * @see AmendmentDTO#TEXT
	 */
	public final void prepareName(String name) {
		set(TEXT, name);
	}
}
