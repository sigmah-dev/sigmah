package org.sigmah.client.util;

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

import java.io.Serializable;

/**
 * A model data object to store information of deleting error.
 *
 * @author HUZHE (zhe.hu32@gmail.com) v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public class DeletionError implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 4432885089366403876L;

	private String categoryTypeName;
	private String projectModelName;
	private String fieldName;

	/**
	 * @param categoryTypeName
	 *          The name of category
	 * @param projectModelName
	 *          The name of project model
	 * @param fieldName
	 *          The name of the field in project model
	 */
	public DeletionError(String categoryTypeName, String projectModelName, String fieldName) {
		this.categoryTypeName = categoryTypeName;
		this.projectModelName = projectModelName;
		this.fieldName = fieldName;

	}

	/**
	 * @return the categoryTypeName
	 */
	public String getCategoryTypeName() {
		return categoryTypeName;
	}

	/**
	 * @param categoryTypeName
	 *          the categoryTypeName to set
	 */
	public void setCategoryTypeName(String categoryTypeName) {
		this.categoryTypeName = categoryTypeName;
	}

	/**
	 * @return the projectModelName
	 */
	public String getProjectModelName() {
		return projectModelName;
	}

	/**
	 * @param projectModelName
	 *          the projectModelName to set
	 */
	public void setProjectModelName(String projectModelName) {
		this.projectModelName = projectModelName;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *          the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
