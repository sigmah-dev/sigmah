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
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.quality.QualityCriterion;
import org.sigmah.server.domain.quality.QualityFramework;
import org.sigmah.server.domain.report.KeyQuestion;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportModelSection;

/**
 * Exports and imports project report models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
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
	public void importModel(InputStream inputStream, EntityManager em, User user) throws Exception {
		ObjectInputStream objectInputStream;
		em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			ProjectReportModel projectReportModel = (ProjectReportModel) objectInputStream.readObject();

			projectReportModel.resetImport(modelesReset, modelesImport);
			saveProjectReportModelElement(projectReportModel, em);
			projectReportModel.setOrganization(user.getOrganization());
			em.persist(projectReportModel);
			em.getTransaction().commit();
		} catch (IOException e) {
			LOG.debug(e);
			throw e;
		}
	}

	@Override
	public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception {

		String name = "";

		if (identifier != null) {
			final Integer projectReportModelId = Integer.parseInt(identifier);

			final ProjectReportModel hibernateModel = em.find(ProjectReportModel.class, projectReportModelId);

			if (hibernateModel == null)
				throw new ExportException("No project report model is associated with the identifier '" + identifier + "'.");

			name = hibernateModel.getName();

			// Stripping hibernate proxies from the model.
			final ProjectReportModel realModel = Realizer.realize(hibernateModel);
			// Serialization
			try {
				final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(realModel);
			} catch (Exception ex) {
				throw new Exception("An error occured while serializing the project model " + projectReportModelId, ex);
			}

		} else {
			throw new Exception("The identifier is missing.");
		}

		return name;
	}

	/**
	 * Save elements of imported project report model
	 * 
	 * @param projectReportModel
	 *          the imported project report model
	 * @param em
	 *          the entity manager
	 */
	public static void saveProjectReportModelElement(ProjectReportModel projectReportModel, EntityManager em) {

		// Save the sections on the project report model
		List<ProjectReportModelSection> sections = projectReportModel.getSections();
		if (sections != null) {
			// Save the project report model without the sections;
			projectReportModel.setSections(null);
			em.persist(projectReportModel);
			// Save the project report sections with the parent project report (saved above)
			for (ProjectReportModelSection section : sections) {
				section.setProjectModelId(projectReportModel.getId());
				List<ProjectReportModelSection> subSections = section.getSubSections();
				List<KeyQuestion> keyQuestions = section.getKeyQuestions();
				if (subSections != null || keyQuestions != null) {
					// Save the section without the sub sections or the key questions
					section.setSubSections(null);
					section.setKeyQuestions(null);
					em.persist(section);
					// Save the sub sections and the key questions
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
	 *          the section to save
	 * @param subSections
	 *          the subsections to save.
	 * @param keyQuestions
	 *          the key question to save.
	 * @param em
	 *          the entity manager
	 */
	private static void saveSectionSubSectionKeyQuestions(ProjectReportModelSection section, List<ProjectReportModelSection> subSections,
			List<KeyQuestion> keyQuestions, EntityManager em) {
		if (keyQuestions != null) {
			saveSectionKeyQuestion(section, keyQuestions, em);
		}
		if (subSections != null) {
			for (ProjectReportModelSection subSection : subSections) {
				subSection.setParentSectionModelId(section.getId());
				List<ProjectReportModelSection> subSubSections = subSection.getSubSections();
				List<KeyQuestion> questions = subSection.getKeyQuestions();
				if (subSubSections != null || keyQuestions != null) {
					// Save sub section before its sub sections and its key questions
					subSection.setSubSections(null);
					subSection.setKeyQuestions(null);
					em.persist(subSection);
					// Save the sub sections and the key questions of the subsection
					saveSectionSubSectionKeyQuestions(subSection, subSubSections, questions, em);
					subSection.setSubSections(subSubSections);
					if (subSection != null) {
						em.merge(subSection);
					}
				} else {
					if (subSection != null) {
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
	 *          the section
	 * @param keyQuestions
	 *          the key questions of the sections
	 * @param em
	 *          the entity manager
	 */
	private static void saveSectionKeyQuestion(ProjectReportModelSection section, List<KeyQuestion> keyQuestions, EntityManager em) {
		for (KeyQuestion keyQuestion : keyQuestions) {
			keyQuestion.setSectionId(section.getId());
			if (keyQuestion.getQualityCriterion() != null) {
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
	 *          the quality criterion to save
	 * @param em
	 *          the entity manager
	 */
	private static void saveKeyQuestionQualityCriterion(QualityCriterion qualityCriterion, EntityManager em) {
		List<QualityCriterion> qualityCriterions = qualityCriterion.getSubCriteria();
		QualityFramework qualityFramework = qualityCriterion.getQualityFramework();
		if (qualityCriterions != null || qualityFramework != null) {
			qualityCriterion.setSubCriteria(null);
			qualityCriterion.setQualityFramework(null);
			em.persist(qualityCriterion);
			for (QualityCriterion criterion : qualityCriterions) {
				saveKeyQuestionQualityCriterion(criterion, em);
			}
			qualityCriterion.setSubCriteria(qualityCriterions);
			em.persist(qualityFramework);
			qualityCriterion.setQualityFramework(qualityFramework);
			em.merge(qualityCriterion);
		} else {
			em.persist(qualityCriterion);
		}
	}
}
