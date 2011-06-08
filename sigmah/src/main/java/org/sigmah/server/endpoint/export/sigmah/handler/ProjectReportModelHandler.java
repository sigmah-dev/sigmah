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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.quality.QualityCriterion;
import org.sigmah.shared.domain.quality.QualityFramework;
import org.sigmah.shared.domain.report.KeyQuestion;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.domain.report.ProjectReportModelSection;

/**
 * Exports and imports project report models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectReportModelHandler implements ModelHandler {

    private final static Log LOG = LogFactory.getLog(ProjectReportModelHandler.class);
	
	/**
	 * The map of imported objects (original object, transformed object)
	 */
	public static HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();
	
	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	public static HashSet<Object> modelesImport = new HashSet<Object>();

    @Override
    public void importModel(InputStream inputStream, EntityManager em, Authentication authentication) throws ExportException {
    	ObjectInputStream objectInputStream;
    	em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			ProjectReportModel projectReportModel = (ProjectReportModel) objectInputStream.readObject();
			projectReportModel.resetImport(modelesReset, modelesImport);
			saveProjectReportModelElement(projectReportModel, em);
			projectReportModel.setOrganization(authentication.getUser().getOrganization());
			em.persist(projectReportModel);
			em.getTransaction().commit();
		} catch (IOException e) {
			LOG.debug(e);
		} catch (ClassNotFoundException e) {
			LOG.debug(e);
		}    	
    }

    @Override
    public String exportModel(OutputStream outputStream, String identifier,
            EntityManager em) throws ExportException {

        String name = "";

        if(identifier != null) {
            final Integer projectReportModelId = Integer.parseInt(identifier);

            final ProjectReportModel hibernateModel = em.find(ProjectReportModel.class, projectReportModelId);
            
            if(hibernateModel == null)
                throw new ExportException("No project report model is associated with the identifier '"+identifier+"'.");

            name = hibernateModel.getName();

            // Stripping hibernate proxies from the model.
            final ProjectReportModel realModel = Realizer.realize(hibernateModel);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realModel);

            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the project model "+projectReportModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }

        return name;
    }

	/**
	 * Save elements of imported project report model
	 * 
	 * @param projectReportModel
	 *            the imported project report model
	 * @param em
	 *            the entity manager
	 */
	private void saveProjectReportModelElement(
			ProjectReportModel projectReportModel, EntityManager em) {

		//Save the sections on the project report model
		List<ProjectReportModelSection> sections = projectReportModel
				.getSections();
		if (sections != null) {
			// Save the project report model without the sections;
			projectReportModel.setSections(null);
			em.persist(projectReportModel);
			// Save the project report sections with the parent project report (saved above)
			for (ProjectReportModelSection section : sections) {
				section.setProjectModelId(projectReportModel.getId());
				List<ProjectReportModelSection> subSections = section
						.getSubSections();
				List<KeyQuestion> keyQuestions = section.getKeyQuestions();
				if (subSections != null || keyQuestions!=null) {
					//Save the section without the sub sections or the key questions
					section.setSubSections(null);
					section.setKeyQuestions(null);
					em.persist(section);
					//Save the sub sections and the key questions
					saveSectionSubSectionKeyQuestions(section, subSections, keyQuestions, em);
					section.setSubSections(subSections);
					
					em.merge(section);
				} else {
					em.persist(section);
				}
			}
			// Set the sections saved above to the project report model
			projectReportModel.setSections(sections);
		}
	}
	
	/**
	 * Save the section whith its sub sections and key questions
	 * 
	 * @param section
	 *            the section to save
	 * @param subSections
	 *            the subsections to save.
	 * @param keyQuestions
	 *            the key question to save.         
	 * @param em
	 *            the entity manager
	 */
	private void saveSectionSubSectionKeyQuestions(ProjectReportModelSection section,
			List<ProjectReportModelSection> subSections, List<KeyQuestion> keyQuestions, EntityManager em) {
		if(keyQuestions!=null){
			saveSectionKeyQuestion(section, keyQuestions, em);
		}
		if(subSections!=null){
			for (ProjectReportModelSection subSection : subSections) {
				subSection.setParentSectionModelId(section.getId());
				List<ProjectReportModelSection> subSubSections = subSection
						.getSubSections();
				List<KeyQuestion> questions = subSection.getKeyQuestions();
				if (subSubSections != null || keyQuestions!=null) {
					// Save sub section before its sub sections and its key questions
					subSection.setSubSections(null);					
					subSection.setKeyQuestions(null);
					em.persist(subSection);
					//Save the sub sections and the key questions of the subsection
					saveSectionSubSectionKeyQuestions(subSection, subSubSections, questions, em);
					subSection.setSubSections(subSubSections);
					if(subSection!=null){
						em.merge(subSection);
					}
				} else {
					if(subSection!=null){
						em.persist(subSection);
					}
				}
			}
		}
		
	}
	
	/**
	 * Save the key questions of a section
	 * 
	 * @param section
	 *            the section
	 * @param keyQuestions
	 *            the key questions of the sections
	 * @param em
	 *            the entity manager
	 */
	private void saveSectionKeyQuestion(ProjectReportModelSection section,List<KeyQuestion> keyQuestions, EntityManager em){
		for(KeyQuestion keyQuestion : keyQuestions){
			keyQuestion.setSectionId(section.getId());
			if(keyQuestion.getQualityCriterion()!=null){
				saveKeyQuestionQualityCriterion(keyQuestion.getQualityCriterion(), em);
			}
			em.persist(keyQuestion);
		}
		section.setKeyQuestions(keyQuestions);
	}
	
	/**
	 * Save the quality criterion passed in argument.
	 * 
	 * @param qualityCriterion
	 *            the quality criterion to save
	 * @param em
	 *            the entity manager
	 */
	private void saveKeyQuestionQualityCriterion(QualityCriterion qualityCriterion, EntityManager em){
		List<QualityCriterion> qualityCriterions = qualityCriterion.getSubCriteria();
		QualityFramework qualityFramework = qualityCriterion.getQualityFramework();
		if(qualityCriterions !=null || qualityFramework!=null){
			qualityCriterion.setSubCriteria(null);
			qualityCriterion.setQualityFramework(null);
			em.persist(qualityCriterion);
			for(QualityCriterion criterion: qualityCriterions){
				saveKeyQuestionQualityCriterion(criterion, em);
			}
			qualityCriterion.setSubCriteria(qualityCriterions);
			em.persist(qualityFramework);
			qualityCriterion.setQualityFramework(qualityFramework);
			em.merge(qualityCriterion);
		}else{
			em.persist(qualityCriterion);
		}
	}	
}
