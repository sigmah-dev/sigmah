package org.sigmah.shared.dto.pivot.model;

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
