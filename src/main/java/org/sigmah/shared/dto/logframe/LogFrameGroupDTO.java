package org.sigmah.shared.dto.logframe;

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.LogFrameGroupType;

/**
 * DTO mapping class for entity logframe.LogFrameGroup.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LogFrameGroupDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2559578621723205905L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "logframe.LogFrameGroup";
	}

	/**
	 * Gets the client-side id for this entity. If this entity has a server-id id, it's returned. Otherwise, a temporary
	 * id is generated and returned.
	 * 
	 * @return The client-side id.
	 */
	public int getClientSideId() {

		// Server-side id.
		Integer id = (Integer) get("id");

		if (id == null) {

			// Client-side id.
			id = (Integer) get("tmpid");

			// Generates the client-side id once.
			if (id == null) {
				id = generateClientSideId();
			}
		}

		return id;
	}

	/**
	 * Generate a client-side unique id for this entity and stores it in the <code>temporaryId</code> attribute.
	 */
	private int generateClientSideId() {
		final int id = (int) new Date().getTime();
		set("tmpid", id);
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("type", getType());
		builder.append("clientSideId", getClientSideId());
	}

	// Group id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Group label.
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Group type.
	public LogFrameGroupType getType() {
		return get("type");
	}

	public void setType(LogFrameGroupType type) {
		set("type", type);
	}

	// Objective parent log frame.
	public LogFrameDTO getParentLogFrame() {
		return get("parentLogFrame");
	}

	public void setParentLogFrame(LogFrameDTO parentLogFrameDTO) {
		set("parentLogFrame", parentLogFrameDTO);
	}

}
