package org.sigmah.shared.dto.layout;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.Widget;

/**
 * LayoutGroupDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LayoutGroupDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "layout.LayoutGroup";

	// DTO attributes keys.
	public static final String TITLE = "title";
	public static final String ROW = "row";
	public static final String COLUMN = "column";
	public static final String PARENT_LAYOUT = "parentLayout";
	public static final String HAS_ITERATIONS = "hasIterations";
	public static final String CONSTRAINTS = "constraints";

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
		builder.append(TITLE, getTitle());
		builder.append(ROW, getRow());
		builder.append(COLUMN, getColumn());
		builder.append(HAS_ITERATIONS, getHasIterations());
	}

	// Layout group title
	public String getTitle() {
		return get(TITLE);
	}

	public void setTitle(String title) {
		set(TITLE, title);
	}

	// Row index
	public Integer getRow() {
		return (Integer) get(ROW);
	}

	public void setRow(Integer row) {
		set(ROW, row);
	}

	// Column index
	public Integer getColumn() {
		return (Integer) get(COLUMN);
	}

	public void setColumn(Integer column) {
		set(COLUMN, column);
	}

	// Has iterations
	public Boolean getHasIterations() {
		return (Boolean) get(HAS_ITERATIONS);
	}

	public void setHasIterations(Boolean hasIterations) {
		set(HAS_ITERATIONS, hasIterations);
	}

	// Reference to layoutDTO
	public LayoutDTO getParentLayout() {
		return get(PARENT_LAYOUT);
	}

	public void setParentLayout(LayoutDTO parentLayout) {
		set(PARENT_LAYOUT, parentLayout);
	}

	// Reference to layout group constraints list
	public List<LayoutConstraintDTO> getConstraints() {
		return get(CONSTRAINTS);
	}

	public void setConstraints(List<LayoutConstraintDTO> constraints) {
		set(CONSTRAINTS, constraints);
	}

	public Widget getWidget() {
		final FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml(getTitle());
		fieldSet.setCollapsible(true);

		final FormLayout formLayout = new FormLayout();
		formLayout.setLabelWidth(250);

		fieldSet.setLayout(formLayout);

		return fieldSet;
	}

}
