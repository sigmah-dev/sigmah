package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * One-to-one DTO for the {@link org.sigmah.server.domain.AttributeGroup AttributeGroup} domain object.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class AttributeGroupDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1370895037462680014L;

	private List<AttributeDTO> attributes = new ArrayList<AttributeDTO>(0);

	public AttributeGroupDTO() {
		// Serialization.
	}

	/**
	 * Creates a shallow clone
	 * 
	 * @param model
	 */
	public AttributeGroupDTO(AttributeGroupDTO model) {
		super(model.getProperties());
		setAttributes(model.getAttributes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
		builder.append("multipleAllowed", isMultipleAllowed());
	}

	public boolean isEmpty() {
		if (this.attributes == null) {
			return true;
		}
		if (this.attributes.size() < 1) {
			return true;
		}
		return false;
	}

	public AttributeGroupDTO(int id) {
		this.setId(id);
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getName() {
		return get("name");
	}

	public List<AttributeDTO> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeDTO> attributes) {
		this.attributes = attributes;
	}

	public boolean isMultipleAllowed() {
		return (Boolean) get("multipleAllowed");
	}

	public void setMultipleAllowed(boolean allowed) {
		set("multipleAllowed", allowed);
	}

	@Override
	public String getEntityName() {
		return "AttributeGroup";
	}
}
