package org.sigmah.shared.dto.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.mapping.HasMappingMode;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.Mappings;
import org.sigmah.shared.dto.base.mapping.UnavailableMappingField;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSupport;
import com.extjs.gxt.ui.client.data.ChangeListener;

/**
 * <p>
 * {@link EntityDTO} implementation inheriting the {@link BaseTreeModel} layer in order to be used into tree structures.
 * <br>
 * Should be very similar to {@link AbstractModelDataEntityDTO} implementation.
 * </p>
 * <p>
 * Fixes {@link BaseTreeModel} so that it can be used with {@code RPC}. {@code RPC} does not call the class constructor,
 * so subsequent calls to BaseTreeModel methods that assume {@code changeEventSupport} has been initialized will fail.
 * </p>
 *
 * @param <K>
 *          Entity DTO id type.
 * @author alexander (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public abstract class AbstractTreeModelEntityDTO<K extends Serializable> extends BaseTreeModel implements EntityDTO<K>, HasMappingMode {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 886248274413453049L;

	/**
	 * <p>
	 * In a tree grid rendering, elements should not be compared by their id.<br>
	 * Indeed, parent and child projects reference each other and GXT's TreeGrid widget ends up totally lost in such
	 * structure.
	 * </p>
	 * <p>
	 * Therefore, this flag disables common {@link #equals(Object)} method logic to <b>only</b> return {@code true} when
	 * the compared object is {@code this}. This fix seems to solve the problem.
	 * </p>
	 * <p>
	 * <em>Default value is {@code false}.</em>
	 * </p>
	 */
	private boolean treeRendering;

	/**
	 * Creates a new tree model data instance.
	 */
	public AbstractTreeModelEntityDTO() {
		super();
	}

	/**
	 * Creates a new tree model with the given properties.
	 * 
	 * @param properties
	 *          the initial properties.
	 */
	public AbstractTreeModelEntityDTO(final Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * <p>
	 * Sets the {@link #treeRendering} flag value.
	 * </p>
	 * <p>
	 * <em>Default value is {@code false}.</em>
	 * </p>
	 * 
	 * @param treeRendering
	 *          {@code true} if the DTO is destined to be rendered into a tree structure.
	 */
	public final void setTreeRendering(boolean treeRendering) {
		this.treeRendering = treeRendering;
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
	 */
	@Override
	public final String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("entity", getEntityName());
		builder.append(ID, getId());

		appendToString(builder); // Appends child entity specific properties.

		return builder.toString();
	}

	/**
	 * <p>
	 * Appends specific properties to the given {@code toString} {@code builder}.
	 * </p>
	 * <p>
	 * <em><b>EntityDTO {@code id} property has already been appended to the {@code builder}.</b></em>
	 * </p>
	 */
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

		if (treeRendering) {
			// Special case avoiding an issue in tree grid widget.
			return this == obj;
		}

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EntityDTO<?> other = (EntityDTO<?>) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	// ---------------------------------------------------------------------------------------------
	//
	// Overridden methods relying on 'getChangeEventSupport()' in order to make the class RPC safe.
	//
	// ---------------------------------------------------------------------------------------------

	/**
	 * Ensures that {@code BaseModel.changeEventSupport} is properly initialized before using it.
	 * 
	 * @return The initialized {@code BaseModel.changeEventSupport} instance.
	 */
	protected final ChangeEventSupport getChangeEventSupport() {
		if (changeEventSupport == null) {
			changeEventSupport = new ChangeEventSupport();
		}
		return changeEventSupport;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChangeListener(final ChangeListener... listener) {
		getChangeEventSupport().addChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addChangeListener(final List<ChangeListener> listeners) {
		for (final ChangeListener listener : listeners) {
			getChangeEventSupport().addChangeListener(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSilent() {
		return getChangeEventSupport().isSilent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notify(final ChangeEvent evt) {
		getChangeEventSupport().notify(evt);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeChangeListener(final ChangeListener... listener) {
		getChangeEventSupport().removeChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeChangeListeners() {
		getChangeEventSupport().removeChangeListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSilent(final boolean silent) {
		getChangeEventSupport().setSilent(silent);
	}

	// ---------------------------------------------------------------------------------------------
	//
	// MAPPING.
	//
	// ---------------------------------------------------------------------------------------------

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

		Mappings.controlPropertyAccess(property, currentMappingMode, getClass());

		return super.get(property);
	}

}
