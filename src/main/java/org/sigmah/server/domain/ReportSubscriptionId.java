package org.sigmah.server.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.EmbeddableEntity;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * ReportSubscriptionId composite id (primary key).<br/>
 * Not an entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Embeddable
public class ReportSubscriptionId implements EmbeddableEntity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3646548590143937659L;

	@Column(name = EntityConstants.REPORT_DEFINITION_COLUMN_ID, nullable = false)
	@NotNull
	private int reportTemplateId;

	@Column(name = EntityConstants.USER_COLUMN_ID, nullable = false)
	@NotNull
	private int userId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public ReportSubscriptionId() {
		// Required empty constructor.
	}

	public ReportSubscriptionId(final int reportTemplateId, final int userId) {
		this.reportTemplateId = reportTemplateId;
		this.userId = userId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("reportTemplateId", reportTemplateId);
		builder.append("userId", userId);

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + reportTemplateId;
		result = prime * result + userId;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ReportSubscriptionId)) {
			return false;
		}
		ReportSubscriptionId other = (ReportSubscriptionId) obj;
		if (reportTemplateId != other.reportTemplateId) {
			return false;
		}
		if (userId != other.userId) {
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public int getReportTemplateId() {
		return reportTemplateId;
	}

	public void setReportTemplateId(int reportTemplateId) {
		this.reportTemplateId = reportTemplateId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
