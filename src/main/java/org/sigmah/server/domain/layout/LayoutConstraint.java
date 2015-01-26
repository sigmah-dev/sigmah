package org.sigmah.server.domain.layout;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Layout Constraint domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LAYOUT_CONSTRAINT_TABLE)
public class LayoutConstraint extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5150783265586227961L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LAYOUT_CONSTRAINT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LAYOUT_CONSTRAINT_COLUMN_SORT_ORDER, nullable = true)
	private Integer sortOrder;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LAYOUT_GROUP_COLUMN_ID, nullable = false)
	@NotNull
	private LayoutGroup parentLayoutGroup;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_flexible_element", nullable = false)
	@NotNull
	private FlexibleElement element;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param parentLayoutGroup
	 *          the parent LayoutGroup
	 */
	public void resetImport(LayoutGroup parentLayoutGroup) {
		this.id = null;
		this.parentLayoutGroup = parentLayoutGroup;

		if (this.element != null) {
			this.element.resetImport();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("sortOrder", sortOrder);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public void setParentLayoutGroup(LayoutGroup parentLayoutGroup) {
		this.parentLayoutGroup = parentLayoutGroup;
	}

	public LayoutGroup getParentLayoutGroup() {
		return parentLayoutGroup;
	}

	public void setElement(FlexibleElement element) {
		this.element = element;
	}

	public FlexibleElement getElement() {
		return element;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

}
