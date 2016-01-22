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

/**
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 */
public class AttributeGroupDimension extends Dimension {
	
	private int attributeGroupId;

    public AttributeGroupDimension() {
	}

    public AttributeGroupDimension(int groupId) {
        super(DimensionType.AttributeGroup);
        this.attributeGroupId = groupId;
    }
	
	public AttributeGroupDimension(String caption, int groupId)  {
		super(DimensionType.AttributeGroup);
		this.attributeGroupId = groupId;
		set("caption", caption);
		set("id", "attrs_dim" + groupId);
    }

    public int getAttributeGroupId() {
		return attributeGroupId;
	}

	public void setAttributeGroupId(int attributeGroupId) {
		this.attributeGroupId = attributeGroupId;
	}

	@Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof AttributeGroupDimension)) {
            return false;
        }
        AttributeGroupDimension that = (AttributeGroupDimension)other;
        if (this.attributeGroupId == that.attributeGroupId) {
        	return true;
        } else {
        	return false;
        }
    }
}
