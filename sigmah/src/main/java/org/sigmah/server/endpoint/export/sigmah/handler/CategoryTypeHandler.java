package org.sigmah.server.endpoint.export.sigmah.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;

/**
 * Exports and imports the category type elements.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr)
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
	public void exportModel(OutputStream outputStream, String identifier,
			EntityManager em) throws ExportException {
	    if(identifier != null) {
            final Integer categoryTypeId = Integer.parseInt(identifier);

            final CategoryType hibernateCategory = em.find(CategoryType.class, categoryTypeId);

            if(hibernateCategory == null)
                throw new ExportException("No category type is associated with the identifier '"+identifier+"'.");

            // Stripping hibernate proxies from the element.
            final CategoryType realCategory = Realizer.realize(hibernateCategory);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realCategory);
                
            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the category type "+categoryTypeId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }
	}

	@Override
	public void importModel(InputStream inputStream, EntityManager em,
			Authentication authentication) throws ExportException {
		ObjectInputStream objectInputStream;
    	em.getTransaction().begin();
    	
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			CategoryType categoryType = (CategoryType) objectInputStream
					.readObject();
			
			if (categoryType != null) {
				saveOrUpdateCategoryType(categoryType, em);
			}
			em.getTransaction().commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}    	
	}
	
	/**
	 * Save or update the imported category type. If the imported element
	 * doesn't exists in the database then it is saved as a new entry otherwise
	 * the existent instance is updated.
	 * 
	 * @param categoryType
	 *            the imported element
	 * @param em
	 *            the entity manager
	 */
	private void saveOrUpdateCategoryType(CategoryType categoryType, EntityManager em){
		//Add the category type in the list of elements that are being handled
		modelesImport.add(categoryType);
		CategoryType key = categoryType;
		List<CategoryElement> categoryElements = categoryType.getElements();
		//Save as a new entry the category type if it doesn't exist in the database
		//(without the category elements)
		if(em.find(CategoryType.class, categoryType.getId())==null){
			categoryType.setId(null);
			categoryType.setElements(null);
			em.persist(categoryType);
		}else{// Update it (without the category elements)
			categoryType.setElements(null);
			em.merge(categoryType);
		}
		if(categoryElements!=null){
			//Save or update the category elements
			for(CategoryElement element : categoryElements){
				element.setParentType(categoryType);
				saveOrUpdateCategoryElement(element, em);
			}
			categoryType.setElements(categoryElements);
			em.merge(categoryType);
		}
		//Add the category type in the map of elements has been handled
		modelesReset.put(key, categoryType);		
	}
	
	/**
	 * Save or update the  categories of the imported category type . If the element doesn't
	 * exists in the database then it is saved as a new entry otherwise the
	 * existent instance is updated.
	 * 
	 * @param categoryElement
	 *            the category of the imported element.
	 * @param em
	 *            the entity manager.
	 */
	private void saveOrUpdateCategoryElement(CategoryElement categoryElement,
			EntityManager em) {
		//Test if the category isn't being transformed
		if (!modelesImport.contains(categoryElement)) {
			modelesImport.add(categoryElement);
			//Test if the category hasn't been transformed
			if (!modelesReset.containsKey(categoryElement)) {
				CategoryElement key = categoryElement;
				//If the imported category doesn't exist in the database it's id is set to null
				if (em.find(CategoryElement.class, categoryElement.getId()) == null) {
					categoryElement.setId(null);
				}
				CategoryType parentType = categoryElement.getParentType();
				//Test if the category type isn't being transformed
				if (!modelesImport.contains(parentType)) {
					modelesImport.add(parentType);
					//Test if the category type  hasn't been transformed
					if (!modelesReset.containsKey(parentType)) {
						CategoryType parentKey = parentType;
						List<CategoryElement> elements = parentType.getElements();
						if (elements != null) {
							parentType.setElements(null);
							//If the category type doesn't exist in the database it's id is set to null
							//The category type is inserted in the database
							if (em.find(CategoryType.class, parentType.getId()) == null) {
								parentType.setId(null);
								em.persist(parentType);
							} else {//The existant category type is updated
								em.merge(parentType);
							}
							for (CategoryElement element : elements) {
								categoryElement.setParentType(parentType);
								saveOrUpdateCategoryElement(element, em);
							}
							parentType.setElements(elements);
							em.merge(parentType);
						} else {
							//If the category type doesn't exist in the database it's id is set to null
							//The category type is inserted in the database
							if (em.find(CategoryType.class, parentType.getId()) == null) {
								parentType.setId(null);
								em.persist(parentType);
							} else {//The existant category type is updated
								em.merge(parentType);
							}
						}
						modelesReset.put(parentKey, parentType);
					} else {
						parentType = (CategoryType) modelesReset.get(parentType);
					}
				}
				categoryElement.setParentType(parentType);
				
				if(categoryElement.getId()==null){
					em.persist(categoryElement);
				}else{
					em.merge(categoryElement);
				}
				modelesReset.put(key, categoryElement);
			} else {// The category element has already been transformed
				categoryElement = (CategoryElement) modelesReset
						.get(categoryElement);
			}
		}
	}
}
