package org.sigmah.shared.dto.pivot.model;

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

import org.sigmah.shared.dto.referential.DimensionType;

public class AdminDimension extends Dimension {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -806780774183203617L;

	private int levelId;

	@SuppressWarnings("unused")
	private AdminDimension() {
		super(DimensionType.AdminLevel);
	}

	public AdminDimension(int levelId) {
		super(DimensionType.AdminLevel);
		this.levelId = levelId;
	}

	public AdminDimension(String caption, int levelId) {
		super(DimensionType.AdminLevel);
		this.set("caption", caption);
		this.set("id", "admin_dim_" + levelId);
		this.levelId = levelId;
	}

	public int getLevelId() {
		return this.levelId;
	}

	@SuppressWarnings("unused")
	private void setLevelId(int levelId) {
		this.levelId = levelId;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof AdminDimension)) {
			return false;
		}

		AdminDimension that = (AdminDimension) other;

		return this.levelId == that.levelId;
	}

}
