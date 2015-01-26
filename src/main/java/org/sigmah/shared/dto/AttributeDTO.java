package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * One-to-One DTO for the {@link org.sigmah.shared.dto.AttributeDTO} domain object
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class AttributeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9170500205294367917L;

	public static final String PROPERTY_PREFIX = "ATTRIB";

	public AttributeDTO() {
		// Serialization.
	}

	public AttributeDTO(AttributeDTO model) {
		super(model.getProperties());

	}

	public AttributeDTO(int id, String name) {
		setId(id);
		setName(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "Attribute";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("name", getName());
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public void setName(String value) {
		set("name", value);
	}

	public String getName() {
		return get("name");
	}

	public static String getPropertyName(int attributeId) {
		return PROPERTY_PREFIX + attributeId;
	}

	public static String getPropertyName(AttributeDTO attribute) {
		return getPropertyName(attribute.getId());
	}

	public String getPropertyName() {
		return getPropertyName(getId());
	}

	public static int idForPropertyName(String property) {
		return Integer.parseInt(property.substring(PROPERTY_PREFIX.length()));
	}

}
