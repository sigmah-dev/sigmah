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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.client.ui.view.project.logframe.grid.Row.Positionable;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * Abstract LogFrameElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class LogFrameElementDTO extends AbstractModelDataEntityDTO<Integer> implements Positionable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6331498912726880880L;

	private List<IndicatorDTO> indicators;

	public LogFrameElementDTO() {
		indicators = new ArrayList<IndicatorDTO>();
	}

	/**
	 * @return formatted rendering of this element's code
	 */
	public abstract String getFormattedCode();

	/**
	 * @return localised, formatted label for this element
	 */
	public abstract String getLabel();

	/**
	 * @return a string that describes this element (either a title or 'intervention logic')
	 */
	public abstract String getDescription();

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
	 * addActivity( Generate a client-side unique id for this entity and stores it in the <code>temporaryId</code>
	 * attribute.
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
	public final int hashCode() {
		return getClientSideId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (!obj.getClass().equals(getClass())) {
			return false;
		}

		final LogFrameElementDTO other = (LogFrameElementDTO) obj;
		return getClientSideId() == other.getClientSideId();
	}

	// Objective id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Objective code.
	public Integer getCode() {
		return get("code");
	}

	public void setCode(Integer code) {
		set("code", code);
	}

	// Objective position in its group.
	public Integer getPosition() {
		return get("position");
	}

	@Override
	public void setPosition(Integer position) {
		set("position", position);
	}

	// Result risks and assumptions
	public String getRisksAndAssumptions() {
		return get("risksAndAssumptions");
	}

	public void setRisksAndAssumptions(String risksAndAssumptions) {
		set("risksAndAssumptions", risksAndAssumptions);
	}

	// Objective group.
	public LogFrameGroupDTO getGroup() {
		return get("group");
	}

	public void setGroup(LogFrameGroupDTO logFrameGroupDTO) {
		set("group", logFrameGroupDTO);
	}

	public List<IndicatorDTO> getIndicators() {
		return indicators;
	}

	public void setIndicators(List<IndicatorDTO> indicators) {
		this.indicators = indicators;
	}

}
