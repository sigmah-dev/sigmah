package org.sigmah.shared.dto.layout;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * LayoutDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LayoutDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "layout.Layout";

	// DTO attributes keys.
	public static final String ROWS_COUNT = "rowsCount";
	public static final String COLUMNS_COUNT = "columnsCount";
	public static final String GROUPS = "groups";

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
		builder.append(ROWS_COUNT, getRowsCount());
		builder.append(COLUMNS_COUNT, getColumnsCount());
	}

	// Rows count
	public Integer getRowsCount() {
		return (Integer) get(ROWS_COUNT);
	}

	public void setRowsCount(Integer rowsCount) {
		set(ROWS_COUNT, rowsCount);
	}

	// Columns count
	public Integer getColumnsCount() {
		return (Integer) get(COLUMNS_COUNT);
	}

	public void setColumnsCount(Integer columnsCount) {
		set(COLUMNS_COUNT, columnsCount);
	}

	// Reference to layout groups list
	public List<LayoutGroupDTO> getGroups() {
		return get(GROUPS);
	}

	public void setGroups(List<LayoutGroupDTO> groups) {
		set(GROUPS, groups);
	}

	public Widget getWidget() {
		return new Grid(getRowsCount(), getColumnsCount());
	}

}
