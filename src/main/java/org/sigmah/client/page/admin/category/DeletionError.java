
package org.sigmah.client.page.admin.category;

import java.io.Serializable;

/**
 * 
 *A model data object to store information of deleting error.
 *
 *@author HUZHE (zhe.hu32@gmail.com)
 */
public class DeletionError implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String categoryTypeName;
	private String projectModelName;
	private String fieldName;
	

	/**
	 * @param categoryTypeName
	 *           The name of category
	 *           
	 * @param projectModelName
	 *           The name of project model
	 *           
	 * @param fieldName
	 *           The name of the field in project model
	 * 
	 */
	public DeletionError(String categoryTypeName,String projectModelName,String fieldName)
	{
       this.categoryTypeName= categoryTypeName;
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
	 * @param categoryTypeName the categoryTypeName to set
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
	 * @param projectModelName the projectModelName to set
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
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
	
}
