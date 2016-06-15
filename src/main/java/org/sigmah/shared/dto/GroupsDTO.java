package org.sigmah.shared.dto;

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
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.user.client.ui.Widget;

public class GroupsDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 21L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "Groups";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String POSITION = "position";
	public static final String CONTAINER = "container";
	public static final String LAYOUT = "layout";
	public static final String PARENT_PROJECT_MODEL = "parentProjectModel";
	
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
		builder.append(NAME, getName());
		builder.append(POSITION, getPosition());
		builder.append(CONTAINER, getContainer());
	}

	

	public Widget getWidget() {
		return getLayout().getWidget();
	}

	// Group Name
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	// Reference to parent project model DTO
	public ProjectModelDTO getParentProjectModel() {
		return get(PARENT_PROJECT_MODEL);
	}

	public void setParentProjectModel(ProjectModelDTO parentProjectModel) {
		set(PARENT_PROJECT_MODEL, parentProjectModel);
	}

	// Reference to layout
	public LayoutDTO getLayout() {
		return get(LAYOUT);
	}

	public void setLayout(LayoutDTO layout) {
		set(LAYOUT, layout);
	}

	// Container
	public String getContainer() {
		return get(CONTAINER);
	}
	
	public void setContainer(String container) {
		set(CONTAINER, container);
	}

	// Vertical Position
	public Integer getPosition() {
		return (Integer) get(POSITION);
	}

	public void setPosition(Integer position) {
		set(POSITION, position);
	}
}
