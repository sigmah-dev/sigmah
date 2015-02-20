package org.sigmah.shared.dto.value;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * ValueDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ValueDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.Value";

	// DTO attributes keys.
	public static final String VALUE = "value";

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
		builder.append(VALUE, getValue());
	}

	// Value's inner value
	public String getValue() {
		return get(VALUE);
	}

	public void setValue(String value) {
		set(VALUE, value);
	}

}
