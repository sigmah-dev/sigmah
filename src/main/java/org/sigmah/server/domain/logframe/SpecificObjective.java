package org.sigmah.server.domain.logframe;

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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Specific objective domain entity.
 * </p>
 * <p>
 * Represents an item of the specific objectives of a log frame.<br/>
 * A specific objective contains a list of expected results.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_SPECIFIC_OBJ_TABLE)
public class SpecificObjective extends LogFrameElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7534655171979110984L;

	@Column(name = EntityConstants.LOGFRAME_SPECIFIC_OBJ_COLUMN_INTERVENTION_LOGIC, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String interventionLogic;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LOGFRAME_COLUMN_ID, nullable = false)
	@NotNull
	private LogFrame parentLogFrame;

	@OneToMany(mappedBy = "parentSpecificObjective", cascade = CascadeType.ALL, orphanRemoval = true)
	// Use @Sort instead of @OrderBy as hibernate biffs because the code field lives in the log_frame_element table.
	@org.hibernate.annotations.Sort
	private List<ExpectedResult> expectedResults = new ArrayList<ExpectedResult>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this objective (omits its ID).
	 * 
	 * @param parentLogFrame
	 *          Log frame that will contains this copy.
	 * @param context
	 *          Map of copied groups.
	 * @return A copy of this specific objective.
	 */
	public SpecificObjective copy(final LogFrame parentLogFrame, final LogFrameCopyContext context) {

		final SpecificObjective copy = new SpecificObjective();

		copy.code = this.code;
		copy.interventionLogic = this.interventionLogic;
		copy.risksAndAssumptions = this.risksAndAssumptions;
		copy.parentLogFrame = parentLogFrame;

		copy.expectedResults = new ArrayList<ExpectedResult>();

		for (final ExpectedResult result : this.expectedResults) {
			copy.expectedResults.add(result.copy(copy, context));
		}

		copy.group = context.getGroupCopy(this.group);
		copy.position = this.position;
		copy.indicators = this.copyIndicators(context);

		return copy;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public String getInterventionLogic() {
		return interventionLogic;
	}

	public void setInterventionLogic(String interventionLogic) {
		this.interventionLogic = interventionLogic;
	}

	public LogFrame getParentLogFrame() {
		return parentLogFrame;
	}

	public void setParentLogFrame(LogFrame parentLogFrame) {
		this.parentLogFrame = parentLogFrame;
	}

	public List<ExpectedResult> getExpectedResults() {
		return expectedResults;
	}

	public void setExpectedResults(List<ExpectedResult> expectedResults) {
		this.expectedResults = expectedResults;
	}

}
