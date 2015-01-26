package org.sigmah.server.report.model.adapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.sigmah.shared.dto.pivot.content.DimensionCategory;
import org.sigmah.shared.dto.pivot.content.EntityCategory;

/**
 * @author Alex Bertram (v1.3)
 */
public class CategoryAdapter extends XmlAdapter<CategoryAdapter.Category, DimensionCategory> {

	public static class Category {

		@XmlAttribute
		public Integer id;
	}

	@Override
	public DimensionCategory unmarshal(Category category) throws Exception {
		if (category.id != null) {
			return new EntityCategory(category.id);
		}
		return null;
	}

	@Override
	public Category marshal(DimensionCategory v) throws Exception {
		return null;
	}
}
