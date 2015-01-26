package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

/**
 * <p>
 * Data transfer object for datasources of Indicators.
 * </p>
 * <p>
 * This is a projection of the {@link org.sigmah.server.domain.Indicator} and
 * {@link org.sigmah.server.domain.UserDatabase} entities.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorDataSourceDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1257058710718562119L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("indicatorId", getIndicatorId());
		builder.append("databaseId", getDatabaseId());
		builder.append("databaseName", getDatabaseName());
		builder.append("indicatorName", getIndicatorName());
		builder.append("indicatorCode", getIndicatorCode());
	}

	public int getIndicatorId() {
		return (Integer) get("indicatorId");
	}

	public void setIndicatorId(int id) {
		set("indicatorId", id);
	}

	public int getDatabaseId() {
		return (Integer) get("databaseId");
	}

	public void setDatabaseId(int id) {
		set("databaseId", id);
	}

	public String getDatabaseName() {
		return get("databaseName");
	}

	public void setDatabaseName(String name) {
		set("databaseName", name);
	}

	public String getIndicatorName() {
		return get("indicatorName");
	}

	public void setIndicatorName(String name) {
		set("indicatorName", name);
	}

	public String getIndicatorCode() {
		return get("indicatorCode");
	}

	public void setIndicatorCode(String code) {
		set("indicatorCode", code);
	}

}
