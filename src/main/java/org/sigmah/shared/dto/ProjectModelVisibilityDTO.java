package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * DTO mapping class for entity ProjectModelVisibility.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectModelVisibilityDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4517698536716727232L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "ProjectModelVisibility";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("type", getType());
		builder.append("organizationId", getOrganizationId());
	}

	// Visibility id.
	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	// Visibility type.
	public ProjectModelType getType() {
		return get("type");
	}

	public void setType(ProjectModelType type) {
		set("type", type);
	}

	// Visibility organization id.
	public Integer getOrganizationId() {
		return get("organizationId");
	}

	public void setOrganizationId(Integer organizationId) {
		set("organizationId", organizationId);
	}
}
