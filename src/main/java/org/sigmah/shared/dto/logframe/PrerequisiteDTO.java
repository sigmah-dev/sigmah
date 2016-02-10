package org.sigmah.shared.dto.logframe;

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

import org.sigmah.client.ui.view.project.logframe.grid.Row.Positionable;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity logframe.Prerequisite.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PrerequisiteDTO extends AbstractModelDataEntityDTO<Integer> implements Positionable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2491895571720689312L;

	public PrerequisiteDTO() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "logframe.Prerequisite";
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
		builder.append("groupId", getGroup() != null ? getGroup().getId() != null ? getGroup().getId() : getGroup().getClientSideId() : null);
		builder.append("label", getLabel());
		builder.append("position", getPosition());
		builder.append("content", getContent());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getClientSideId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof PrerequisiteDTO)) {
			return false;
		}

		final PrerequisiteDTO other = (PrerequisiteDTO) obj;
		return getClientSideId() == other.getClientSideId();
	}

	public String getFormattedCode() {

		final StringBuilder sb = new StringBuilder();
		sb.append(getCode());
		sb.append(".");

		return sb.toString();
	}

	// Prerequisite id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Prerequisite code.
	public Integer getCode() {
		return get("code");
	}

	public void setCode(Integer code) {
		set("code", code);
	}

	// Prerequisite position in its group.
	public Integer getPosition() {
		return get("position");
	}

	@Override
	public void setPosition(Integer position) {
		set("position", position);
	}

	// Prerequisite content text.
	public String getContent() {
		return get("content");
	}

	public void setContent(String content) {
		set("content", content);
	}

	// Prerequisite parent log frame.
	public LogFrameDTO getParentLogFrame() {
		return get("parentLogFrame");
	}

	public void setParentLogFrame(LogFrameDTO parentLogFrameDTO) {
		set("parentLogFrame", parentLogFrameDTO);
	}

	// Prerequisite group.
	public LogFrameGroupDTO getGroup() {
		return get("group");
	}

	public void setGroup(LogFrameGroupDTO logFrameGroupDTO) {
		set("group", logFrameGroupDTO);
	}

	// Display label.
	/**
	 * Sets the attribute <code>label</code> to display this element in a selection window.
	 */
	public void setLabel(String label) {
		set("label", label);
	}

	public String getLabel() {
		return get("label");
	}

}
