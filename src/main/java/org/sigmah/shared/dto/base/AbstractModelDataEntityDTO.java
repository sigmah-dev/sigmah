package org.sigmah.shared.dto.base;

import java.io.Serializable;
import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.mapping.HasMappingMode;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.Mappings;
import org.sigmah.shared.dto.base.mapping.UnavailableMappingField;

import com.google.gwt.core.client.GWT;

/**
 * <p>
 * Abstract layer for <em>model data</em> entity DTO (with id).<br/>
 * Implements default {@code equals()} and {@code hashCode()} methods based on entity id.
 * </p>
 * <p>
 * Inherits {@link com.extjs.gxt.ui.client.data.BaseModelData}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <K>
 *          EntityDTO id type.
 * @see com.extjs.gxt.ui.client.data.ModelData
 */
public abstract class AbstractModelDataEntityDTO<K extends Serializable> extends AbstractModelDataDTO implements EntityDTO<K>, HasMappingMode {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7916718419424337314L;

	/**
	 * Creates a new model data instance.
	 */
	public AbstractModelDataEntityDTO() {
		super();
	}

	/**
	 * Creates a new model with the given properties.
	 * 
	 * @param properties
	 *          the initial properties.
	 */
	public AbstractModelDataEntityDTO(final Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public K getId() {
		return get(ID);
	}

	/**
	 * Sets DTO entity id.
	 * 
	 * @param id
	 *          The new id.
	 */
	public void setId(K id) {
		set(ID, id);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <em><b>EntityDTO {@code id} property has already been appended to the {@code builder}.</b></em>
	 * </p>
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		// Default implementation does nothing. Override this method to append specific properties.
	}

	/**
	 * <p>
	 * <b><em>Default {@code hashCode} method only relies on {@code id} property.</em></b>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	/**
	 * <p>
	 * <b><em>Default {@code equals} method only relies on {@code id} property.</em></b>
	 * </p>
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
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		AbstractModelDataEntityDTO<?> other = (AbstractModelDataEntityDTO<?>) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	// --
	// MAPPING
	// --

	/**
	 * The current mapping mode.
	 */
	private IsMappingMode currentMappingMode;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IsMappingMode getCurrentMappingMode() {
		return currentMappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setCurrentMappingMode(IsMappingMode currentMappingMode) {
		this.currentMappingMode = currentMappingMode;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws UnavailableMappingField
	 *           if the given property isn't available in the current mapping mode.
	 */
	@Override
	public final <X> X get(String property) {

		if (!GWT.isProdMode()) {
			Mappings.controlPropertyAccess(property, currentMappingMode, getClass());
		}

		return super.get(property);
	}

}
