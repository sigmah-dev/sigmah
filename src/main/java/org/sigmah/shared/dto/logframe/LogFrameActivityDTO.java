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

import org.sigmah.client.util.ToStringBuilder;

/**
 * DTO mapping class for entity logframe.Activity.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LogFrameActivityDTO extends LogFrameElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6134012388369233491L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "logframe.Activity";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("code", getCode());
		builder.append("groupId", getGroup() != null ? getGroup().getId() != null ? getGroup().getId() : getGroup().getClientSideId() : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormattedCode() {

		final StringBuilder sb = new StringBuilder();

		if (getParentExpectedResult() != null) {
			sb.append(getParentExpectedResult().getFormattedCode());
		}

		sb.append(getCode());
		sb.append(".");

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return getTitle();
	}

	// Activity advancement
	public Integer getAdvancement() {
		return get("advancement");
	}

	public void setAdvancement(Integer advancement) {
		set("advancement", advancement);
	}

	// Activity title.
	public String getTitle() {
		return get("title");
	}

	public void setTitle(String title) {
		set("title", title);
	}

	// Activity start date.
	public Date getStartDate() {
		return get("startDate");
	}

	public void setStartDate(Date startDate) {
		set("startDate", startDate);
	}

	// Activity end date.
	public Date getEndDate() {
		return get("endDate");
	}

	public void setEndDate(Date endDate) {
		set("endDate", endDate);
	}

	// Activity parent result.
	public ExpectedResultDTO getParentExpectedResult() {
		return get("parentExpectedResult");
	}

	public void setParentExpectedResult(ExpectedResultDTO parentExpectedResultDTO) {
		set("parentExpectedResult", parentExpectedResultDTO);
	}

	// Display label.
	/**
	 * Sets the attribute <code>label</code> to display this element in a selection window.
	 */
	public void setLabel(String label) {
		set("label", label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return get("label");
	}

}
