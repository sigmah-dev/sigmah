package org.sigmah.shared.dto.base;

import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * <p>
 * Abstract layer for <em>model data</em> DTO (without id).
 * </p>
 * <p>
 * Inherits {@link com.extjs.gxt.ui.client.data.BaseModelData}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see com.extjs.gxt.ui.client.data.ModelData
 */
public abstract class AbstractModelDataDTO extends BaseModelData implements ModelData, DTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7586213402175592079L;

	/**
	 * Creates a new model data instance.
	 */
	public AbstractModelDataDTO() {
		super();
	}

	/**
	 * Creates a new model with the given properties.
	 * 
	 * @param properties
	 *          the initial properties.
	 */
	public AbstractModelDataDTO(final Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		if (this instanceof EntityDTO) {
			final EntityDTO<?> entityDTO = (EntityDTO<?>) this;
			builder.append("entity", entityDTO.getEntityName());
			builder.append(EntityDTO.ID, entityDTO.getId());
		}

		appendToString(builder); // Appends child entity specific properties.

		return builder.toString();
	}

	/**
	 * <p>
	 * Appends specific properties to the given {@code toString} {@code builder}.
	 * </p>
	 * 
	 * @param builder
	 *          The {@code toString} client builder.
	 */
	protected abstract void appendToString(final ToStringBuilder builder);

}
