package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

/**
 * One-to-One DTO for the {@code org.sigmah.shared.report.model.MapIcon} report class.
 *
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class MapIconDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -98374293830234259L;

	public MapIconDTO() {
		// Serialization.
	}

	public MapIconDTO(final String id) {
		setId(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		// Nothing to append.
	}

	public void setId(String name) {
		set("id", name);
	}

	public String getId() {
		return get("id");
	}

}
