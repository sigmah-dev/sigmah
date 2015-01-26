package org.sigmah.shared.dto;

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
