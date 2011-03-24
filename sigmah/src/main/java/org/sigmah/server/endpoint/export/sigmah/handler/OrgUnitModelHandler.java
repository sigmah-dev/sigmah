/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.export.sigmah.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;

/**
 * Exports and imports organizational units models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OrgUnitModelHandler implements ModelHandler {
	private final static Log LOG = LogFactory.getLog(OrgUnitModelHandler.class);
    @Override
    public void importModel(InputStream inputStream, EntityManager em, Authentication authentication) throws ExportException {
        ObjectInputStream objectInputStream;
        em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			OrgUnitModel orgUnitModel = (OrgUnitModel)objectInputStream.readObject();
			orgUnitModel.resetImport();
			saveOrgUnitFlexibleElement(orgUnitModel, em);
			em.persist(orgUnitModel);
			em.getTransaction().commit();
		} catch (IOException e) {
			LOG.error("Model import error.", e);
		} catch (ClassNotFoundException e) {
			LOG.error("Model import error.", e);
		}
    }

    @Override
    public void exportModel(OutputStream outputStream, String identifier,
            EntityManager em) throws ExportException {
        
        if(identifier != null) {
            final Integer orgUnitModelId = Integer.parseInt(identifier);

            final OrgUnitModel hibernateModel = em.find(OrgUnitModel.class, orgUnitModelId);

            if(hibernateModel == null)
                throw new ExportException("No orgUnit model is associated with the identifier '"+identifier+"'.");

            // Stripping hibernate proxies from the model.
            final OrgUnitModel realModel = Realizer.realize(hibernateModel);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realModel);

            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the orgUnit model "+orgUnitModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }
    }

	/**
	 * Save the flexible elements of imported organizational unit model
	 * 
	 * @param orgUnitModel
	 *            the imported organizational unit model
	 * @param em
	 *            the entity manager
	 */
	private void saveOrgUnitFlexibleElement(OrgUnitModel orgUnitModel,
			EntityManager em) {
		// OrgUnitModel --> Banner --> Layout --> Groups --> Constraints
		if (orgUnitModel.getBanner() != null
				&& orgUnitModel.getBanner().getLayout() != null) {
			List<LayoutGroup> bannerLayoutGroups = orgUnitModel.getBanner()
					.getLayout().getGroups();
			if (bannerLayoutGroups != null) {
				for (LayoutGroup layoutGroup : bannerLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup
							.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
											.getElement()).getChoices();
									if (questionChoiceElements != null) {
										// Save parent QuestionElement like
										// FlexibleElement
										FlexibleElement parent = (FlexibleElement) layoutConstraint
												.getElement();
										((QuestionElement) parent)
												.setChoices(null);
										em.persist(parent);
										// Save QuestionChoiceElement with their
										// QuestionElement parent(saved above)
										for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
											if (questionChoiceElement != null) {
												questionChoiceElement
														.setId(null);
												questionChoiceElement
														.setParentQuestion((QuestionElement) parent);
												em
														.persist(questionChoiceElement);
											}
										}
										// Set saved QuestionChoiceElement to
										// QuestionElement parent and update it
										((QuestionElement) parent)
												.setChoices(questionChoiceElements);
										em.merge(parent);
									} else {
										em.persist(layoutConstraint
												.getElement());
									}
								} else {
									em.persist(layoutConstraint.getElement());
								}
							}
						}
					}
				}
			}
		}
		// OrgUnitModel --> Detail --> Layout --> Groups --> Constraints
		if (orgUnitModel.getDetails() != null
				&& orgUnitModel.getDetails().getLayout() != null) {
			List<LayoutGroup> detailLayoutGroups = orgUnitModel.getDetails()
					.getLayout().getGroups();
			if (detailLayoutGroups != null) {
				for (LayoutGroup layoutGroup : detailLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup
							.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
											.getElement()).getChoices();
									if (questionChoiceElements != null) {
										// Save parent QuestionElement like
										// FlexibleElement
										FlexibleElement parent = (FlexibleElement) layoutConstraint
												.getElement();
										((QuestionElement) parent)
												.setChoices(null);
										em.persist(parent);
										// Save QuestionChoiceElement with their
										// QuestionElement parent(saved above)
										for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
											if (questionChoiceElement != null) {
												questionChoiceElement
														.setId(null);
												questionChoiceElement
														.setParentQuestion((QuestionElement) parent);
												em
														.persist(questionChoiceElement);
											}
										}
										// Set saved QuestionChoiceElement to
										// QuestionElement parent and update it
										((QuestionElement) parent)
												.setChoices(questionChoiceElements);
										em.merge(parent);
									} else {
										em.persist(layoutConstraint
												.getElement());
									}
								} else {
									em.persist(layoutConstraint.getElement());
								}
							}
						}
					}
				}
			}
		}
	}
}
