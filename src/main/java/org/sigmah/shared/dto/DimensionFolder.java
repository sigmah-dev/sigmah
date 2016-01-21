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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.referential.DimensionType;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Dimension Folder.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DimensionFolder extends BaseModelData {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9212781626829843237L;

	private DimensionType type;
	private int depth;
	private int id;

	public DimensionFolder(String name, DimensionType type, int depth, int id) {
		this.type = type;
		this.depth = depth;
		this.id = id;
		set("caption", name);
		set("id", "folder_" + type + "_" + depth + "_" + id);
		set("name", name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("id", getId());
		builder.append("type", getType());
		builder.append("depth", getDepth());
		return builder.toString();
	}

	public DimensionType getType() {
		return this.type;
	}

	public void setType(DimensionType type) {
		this.type = type;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
