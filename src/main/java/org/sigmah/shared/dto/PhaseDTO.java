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
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * PhaseDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhaseDTO extends AbstractModelDataEntityDTO<Integer> implements Comparable<PhaseDTO> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Phase";

	// DTO attributes keys.
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String PARENT_PROJECT = "parentProject";
	public static final String PHASE_MODEL = "phaseModel";

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
		builder.append(START_DATE, getStartDate());
		builder.append(END_DATE, getEndDate());
	}

	public Date getStartDate() {
		return get(START_DATE);
	}

	public void setStartDate(Date startDate) {
		set(START_DATE, startDate);
	}

	public Date getEndDate() {
		return get(END_DATE);
	}

	public void setEndDate(Date endDate) {
		set(END_DATE, endDate);
	}

	public ProjectDTO getParentProject() {
		return get(PARENT_PROJECT);
	}

	public void setParentProject(ProjectDTO parentProject) {
		set(PARENT_PROJECT, parentProject);
	}

	public PhaseModelDTO getPhaseModel() {
		return get(PHASE_MODEL);
	}

	public void setPhaseModel(PhaseModelDTO phaseModel) {
		set(PHASE_MODEL, phaseModel);
	}

	/**
	 * Returns if the phase id ended.
	 */
	public boolean isEnded() {
		return getEndDate() != null;
	}

	/**
	 * Returns if this phase is a successor of the given phase.
	 * 
	 * @param phase
	 *          The phase.
	 * @return If this phase is a successor of the given phase.
	 */
	public boolean isSuccessor(PhaseDTO phase) {

		if (phase == null) {
			return false;
		}

		final List<PhaseModelDTO> successors = phase.getPhaseModel().getSuccessors();
		if (successors != null) {
			for (final PhaseModelDTO successor : successors) {
				final PhaseDTO p = getParentProject().getPhaseFromModel(successor);
				if (this.equals(p)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(PhaseDTO o) {
		if (getPhaseModel() != null && o.getPhaseModel() != null) {
			if (getPhaseModel().getDisplayOrder() == o.getPhaseModel().getDisplayOrder()) {
				return 0;
			} else if (getPhaseModel().getDisplayOrder() > o.getPhaseModel().getDisplayOrder()) {
				return 1;
			} else {
				return -1;
			}
		}
		return 0;
	}

}
