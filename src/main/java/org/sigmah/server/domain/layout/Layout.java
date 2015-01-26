package org.sigmah.server.domain.layout;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Layout domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LAYOUT_TABLE)
public class Layout extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3567671639080023704L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.LAYOUT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.LAYOUT_COLUMN_ROWS_COUNT, nullable = false)
	@NotNull
	private Integer rowsCount;

	@Column(name = EntityConstants.LAYOUT_COLUMN_COLUMNS_COUT, nullable = false)
	@NotNull
	private Integer columnsCount;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "parentLayout", cascade = CascadeType.ALL)
	private List<LayoutGroup> groups = new ArrayList<LayoutGroup>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public Layout() {
		// Default empty constructor.
	}

	/**
	 * Creates a {@link LayoutGroup} for each cell generated from the given numbers of rows and columns.
	 * 
	 * @param rows
	 *          The number of rows in the layout.
	 * @param cols
	 *          The number of columns in the layout.
	 */
	public Layout(final int rows, final int cols) {
		rowsCount = rows;
		columnsCount = cols;

		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {

				final LayoutGroup group = new LayoutGroup();
				group.setRow(row);
				group.setColumn(col);
				group.setTitle("Group " + groups.size());

				group.setParentLayout(this);
				groups.add(group);
			}
		}
	}

	/**
	 * Adds a constraint to position an element in a current layout's group.
	 * 
	 * @param row
	 *          The row of the group.
	 * @param col
	 *          The column of the group
	 * @param elem
	 *          The element constrained.
	 * @param order
	 *          The constraint.
	 */
	public void addConstraint(int row, int col, FlexibleElement elem, int order) {

		// Checks cell index constraints.
		if (row < 0 || row > rowsCount || col < 0 || col > columnsCount) {
			return;
		}

		// Creates the constraint.
		final LayoutConstraint constraint = new LayoutConstraint();
		constraint.setElement(elem);
		constraint.setSortOrder(order);

		// Adds it to the correct group.
		for (final LayoutGroup group : groups) {
			if (group.getRow() == row && group.getColumn() == col) {
				group.addConstraint(constraint);
				return;
			}
		}
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
		if (this.groups != null) {
			for (LayoutGroup layoutGroup : groups) {
				if (layoutGroup != null)
					layoutGroup.resetImport(this);
			}
		}
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

	public void setRowsCount(Integer rowsCount) {
		this.rowsCount = rowsCount;
	}

	public Integer getRowsCount() {
		return rowsCount;
	}

	public void setColumnsCount(Integer columnsCount) {
		this.columnsCount = columnsCount;
	}

	public Integer getColumnsCount() {
		return columnsCount;
	}

	public void setGroups(List<LayoutGroup> groups) {
		this.groups = groups;
	}

	public List<LayoutGroup> getGroups() {
		return groups;
	}

}
