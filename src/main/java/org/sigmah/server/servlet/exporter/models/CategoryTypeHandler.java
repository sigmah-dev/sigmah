package org.sigmah.server.servlet.exporter.models;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.h2.util.StringUtils;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.shared.dto.referential.CategoryIcon;

/**
 * Exports and imports the category type elements.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr) v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public class CategoryTypeHandler implements ModelHandler {

	/**
	 * The map of imported objects (original object, transformed object)
	 */
	public static HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();

	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	public static HashSet<Object> modelesImport = new HashSet<Object>();

	@Override
	public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws IOException {
		String name = "";

		if (identifier != null) {
			final Integer categoryTypeId = Integer.parseInt(identifier);

			final CategoryType hibernateCategory = em.find(CategoryType.class, categoryTypeId);

			if (hibernateCategory == null) {
				throw new IllegalArgumentException("No category type is associated with the identifier '" + identifier + "'.");
			}

			name = hibernateCategory.getLabel();

			// Stripping hibernate proxies from the element.

			final CategoryType realCategory = Realizer.realize(hibernateCategory);

			// Serialization
			final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(realCategory);

		} else {
			throw new IllegalArgumentException("The identifier is missing.");
		}

		return name;
	}

	@Override
	public void importModel(InputStream inputStream, EntityManager em, User user) throws IOException, ClassNotFoundException {

		ObjectInputStream objectInputStream;
		em.getTransaction().begin();

		objectInputStream = new ObjectInputStream(inputStream);

		CategoryType categoryType = (CategoryType) objectInputStream.readObject();

		if (categoryType != null) {
			saveOrUpdateCategoryType(categoryType, em, user);
		}
		em.getTransaction().commit();
	}

	/**
	 * Save or update the imported category type. If the imported element doesn't exists in the database then it is saved
	 * as a new entry otherwise the existent instance is updated.
	 * 
	 * @param categoryType
	 *          the imported element
	 * @param em
	 *          the entity manager
	 */
	private void saveOrUpdateCategoryType(CategoryType categoryType, EntityManager em, User user) {
		// Add the category type in the list of elements that are being handled
		modelesImport.add(categoryType);
		CategoryType key = categoryType;
		List<CategoryElement> categoryElements = categoryType.getElements();

		// Sets the organization.
		categoryType.setOrganization(user.getOrganization());

		// Find the database category corresponding to the imported one
		CategoryType hibernateCategoryType = em.find(CategoryType.class, categoryType.getId());

		// Compare the two category types
		if (isEqualsCategoryTypes(categoryType, hibernateCategoryType)) {
			// Update it (without the category elements)
			categoryType.setElements(null);
			em.merge(categoryType);
			
		} else {
			// Save the new category type (without the category elements)
			categoryType.setId(null);
			categoryType.setElements(null);
			em.persist(categoryType);
		}

		// Save or update the category elements
		if (categoryElements != null) {
			for (CategoryElement element : categoryElements) {
				element.setParentType(categoryType);
				saveOrUpdateCategoryElement(element, em, user);
			}
			categoryType.setElements(categoryElements);
			em.merge(categoryType);
		}

		// Add the category type in the map of elements has been handled
		modelesReset.put(key, categoryType);
	}

	/**
	 * Save or update the categories of the imported category type . If the element doesn't exists in the database then it
	 * is saved as a new entry otherwise the existent instance is updated.
	 * 
	 * @param categoryElement
	 *          the category of the imported element.
	 * @param em
	 *          the entity manager.
	 */
	private void saveOrUpdateCategoryElement(CategoryElement categoryElement, EntityManager em, User user) {
		// Test if the category isn't being transformed
		if (!modelesImport.contains(categoryElement)) {
			modelesImport.add(categoryElement);

			// Test if the category hasn't been transformed
			if (!modelesReset.containsKey(categoryElement)) {
				CategoryElement key = categoryElement;

				CategoryType parentType = categoryElement.getParentType();
				// Test if the category type isn't being transformed
				if (!modelesImport.contains(parentType)) {
					modelesImport.add(parentType);

					// Test if the category type hasn't been transformed
					if (!modelesReset.containsKey(parentType)) {
						saveOrUpdateCategoryType(parentType, em, user);
					} else {
						parentType = (CategoryType) modelesReset.get(parentType);
					}
				}
				categoryElement.setParentType(parentType);

				// Sets the organization.
				categoryElement.setOrganization(user.getOrganization());

				// Find the database category element corresponding to the
				// imported one
				CategoryElement hibernateCategoryElement = em.find(CategoryElement.class, categoryElement.getId());

				// Compare the two category types
				if (isEqualCategoryElements(categoryElement, hibernateCategoryElement)) {
					em.merge(categoryElement);
				} else {
					categoryElement.setId(null);
					em.persist(categoryElement);
				}

				modelesReset.put(key, categoryElement);

			} else {
				// The category element has already been transformed
				categoryElement = (CategoryElement) modelesReset.get(categoryElement);
			}
		}
	}

	/**
	 * Copare two catagory types.
	 * 
	 * @param mine
	 *          the imported category type.
	 * @param other
	 *          the database category type which has the same id with the imported category type.
	 * @return true if the category types are equals, otherwise false
	 */
	private boolean isEqualsCategoryTypes(CategoryType mine, CategoryType other) {
		if(mine == null && other == null) {
			return true;
			
		} else if (mine == null || other == null) {
			return false;
			
		} else if (!StringUtils.equals(mine.getLabel(), other.getLabel())) {
			// Compare the labels
			return false;
		}

		// Compare the icons
		CategoryIcon myIcon = mine.getIcon();
		CategoryIcon otherIcon = other.getIcon();
		if(myIcon == null && otherIcon == null) {
			return true;
			
		} else if(myIcon == null || otherIcon == null) {
			return false;
			
		} else {
			return myIcon.equals(otherIcon);
		}
	}

	/**
	 * Compare two category elements.
	 * 
	 * @param mine
	 *          the imported category element.
	 * @param other
	 *          the database category element which has the same id with the imported category element.
	 * @return true if the two category elements are equals, orherwise false
	 */
	private boolean isEqualCategoryElements(CategoryElement mine, CategoryElement other) {
		if(mine == null && other == null) {
			return true;
			
		} else if (mine == null || other == null) {
			return false;
			
		} else if (!StringUtils.equals(mine.getLabel(), other.getLabel())) {
			// compare the labels
			return false;
			
		} else if (!StringUtils.equals(mine.getColor(), other.getColor())) {
			// compare the colors
			return false;
			
		} else {
			return true;
		}
	}
}
