package org.sigmah.shared.dto.pivot.model;

import java.util.HashSet;
import java.util.Set;
import org.sigmah.shared.dto.pivot.content.PivotContent;

import org.sigmah.shared.dto.referential.DimensionType;

/**
 * Abstract base class that exposes properties common to the PivotTable and PivotChart elements
 * 
 * @author Alex Bertram (v1.3)
 */
public abstract class PivotElement<ContentT extends PivotContent> extends ReportElement<ContentT> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4325435264779198578L;

	private boolean showEmptyCells = false;

	/**
	 * Returns the set of all dimensions that figure in the pivot table/chart, whether in the row, column, horizantal
	 * axis, etc.
	 * 
	 * @return The set of all dimensions.
	 */
	public abstract Set<Dimension> allDimensions();

	/**
	 * Returns the set of all dimension <i>types</i> that figures in the pivot table/chart.
	 * 
	 * @return The set of all {@link DimensionType}.
	 * @see #allDimensions()
	 */
	public Set<DimensionType> allDimensionTypes() {
		Set<DimensionType> set = new HashSet<DimensionType>();

		for (Dimension dimension : allDimensions()) {
			set.add(dimension.getType());
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentT getContent() {
		return content;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContent(ContentT content) {
		this.content = content;
	}

	public boolean isShowEmptyCells() {
		return showEmptyCells;
	}

	public void setShowEmptyCells(boolean showEmptyCells) {
		this.showEmptyCells = showEmptyCells;
	}

}
