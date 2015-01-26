package org.sigmah.shared.dto.logframe;

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
