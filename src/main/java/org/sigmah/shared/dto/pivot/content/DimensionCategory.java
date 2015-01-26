package org.sigmah.shared.dto.pivot.content;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.sigmah.server.report.model.adapter.CategoryAdapter;

/**
 * @author Alex Bertram (akbertram@gmail.com) (v1.3)
 */
@XmlJavaTypeAdapter(CategoryAdapter.class)
public interface DimensionCategory extends Serializable {

	/**
	 * @return The value by which to sort this category
	 */
	Comparable getSortKey();

	/**
	 * @return the parent category
	 */
	DimensionCategory getParent();

	/**
	 * @return true if this category has a parent
	 */
	boolean hasParent();

}
