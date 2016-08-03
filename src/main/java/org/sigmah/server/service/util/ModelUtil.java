package org.sigmah.server.service.util;

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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.element.ReportListElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class ModelUtil {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ModelUtil.class);

	@SuppressWarnings("unchecked")
	public static void persistFlexibleElement(final EntityManager em, final Mapper mapper, final PropertyMap changes, final Object model) {

		if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) == null) {
			return;
		}

		// Common attributes
		final String name = changes.get(AdminUtil.PROP_FX_NAME);
		final String code = changes.get(AdminUtil.PROP_FX_CODE);
		final ElementTypeEnum type = changes.get(AdminUtil.PROP_FX_TYPE);
		final Boolean isCompulsory = changes.get(AdminUtil.PROP_FX_IS_COMPULSARY);
		final PrivacyGroupDTO pg = changes.get(AdminUtil.PROP_FX_PRIVACY_GROUP);
		final Boolean amend = changes.get(AdminUtil.PROP_FX_AMENDABLE);
		final Boolean exportable = changes.get(AdminUtil.PROP_FX_EXPORTABLE);

		// Position
		final LayoutGroupDTO group = changes.get(AdminUtil.PROP_FX_GROUP);
		final Integer order = changes.get(AdminUtil.PROP_FX_ORDER_IN_GROUP);
		final Boolean inBanner = changes.get(AdminUtil.PROP_FX_IN_BANNER);
		final Integer posB;
		if (changes.get(AdminUtil.PROP_FX_POS_IN_BANNER) instanceof Integer) {
			posB = ((Integer) changes.get(AdminUtil.PROP_FX_POS_IN_BANNER)) - 1;
		} else {
			posB = null;
		}

		final Map<String, Object> oldLayoutFields = (Map<String, Object>) changes.get(AdminUtil.PROP_FX_OLD_FIELDS);
		final LayoutConstraintDTO oldLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields.get(AdminUtil.PROP_FX_LC);
		final LayoutConstraintDTO oldBannerLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields.get(AdminUtil.PROP_FX_LC_BANNER);
		final ElementTypeEnum oldType = (ElementTypeEnum) oldLayoutFields.get(AdminUtil.PROP_FX_TYPE);
		final Integer oldOrder = (Integer) oldLayoutFields.get(AdminUtil.PROP_FX_ORDER_IN_GROUP);

		// Specific attributes
		final Character textType = changes.get(AdminUtil.PROP_FX_TEXT_TYPE);
		final Number maxLimit = changes.get(AdminUtil.PROP_FX_MAX_LIMIT);
		final Number minLimit = changes.get(AdminUtil.PROP_FX_MIN_LIMIT);
		final Integer length = changes.get(AdminUtil.PROP_FX_LENGTH);
		final Boolean decimal = changes.get(AdminUtil.PROP_FX_DECIMAL);
		final ReportModelDTO reportModel = changes.get(AdminUtil.PROP_FX_REPORT_MODEL);
		final Boolean isMultiple = changes.get(AdminUtil.PROP_FX_Q_MULTIPLE);
		final CategoryTypeDTO category = changes.get(AdminUtil.PROP_FX_Q_CATEGORY);
		final List<String> qChoices = changes.get(AdminUtil.PROP_FX_Q_CHOICES);
		Set<String> qChoicesDisabled = changes.get(AdminUtil.PROP_FX_Q_CHOICES_DISABLED);
		final List<BudgetSubFieldDTO> bSubFields = changes.get(AdminUtil.PROP_FX_B_BUDGETSUBFIELDS);
		final BudgetSubFieldDTO ratioDividend = changes.get(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVIDEND);
		final BudgetSubFieldDTO ratioDivisor = changes.get(AdminUtil.PROP_FX_B_BUDGET_RATIO_DIVISOR);
		final String computationRule = changes.get(AdminUtil.PROP_FX_COMPUTATION_RULE);

		final FlexibleElementDTO flexibleEltDTO = changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT);

		FlexibleElement flexibleElt = null;
		if (flexibleEltDTO.getId() != null && flexibleEltDTO.getId() > 0) {
			flexibleElt = em.find(FlexibleElement.class, flexibleEltDTO.getId());
		} else {
			flexibleElt = (FlexibleElement) createNewFlexibleElement(em, oldType, type, flexibleElt);
		}
		
		if(qChoicesDisabled == null) {
			qChoicesDisabled = Collections.emptySet();
		}

		LOG.debug("Saving : (name {}, type {}, group {}, order {}, inBanner {}, posB {}, isCompulsory {}, pg {}, amend {}, exportable {})",
			type, group, order, inBanner, posB, isCompulsory, pg, amend, exportable);
		LOG.debug("Also Saving : (maxLimit {}, minLimit {}, textType {}, length {}, decimal {}, reportModel {})",
			maxLimit, minLimit, textType, length, decimal, reportModel);

		Boolean basicChanges = false;
		if (flexibleElt != null) {// update flexible element
			// //////////////// First, basic attributes
			if (name != null) {
				flexibleElt.setLabel(name);
				basicChanges = true;
			}
			if (code != null) {
				flexibleElt.setCode(code);
				basicChanges = true;
			}
			if (amend != null) {
				flexibleElt.setAmendable(amend);
				basicChanges = true;
			}
			if (exportable != null) {
				flexibleElt.setExportable(exportable);
				basicChanges = true;
			}
			if (isCompulsory != null) {
				flexibleElt.setValidates(isCompulsory);
				basicChanges = true;
			}
			if (pg != null) {
				PrivacyGroup pgToPersist = em.find(PrivacyGroup.class, pg.getId());
				if (pgToPersist != null) {
					flexibleElt.setPrivacyGroup(pgToPersist);
					basicChanges = true;
				}
			} else if(changes.containsKey(AdminUtil.PROP_FX_PRIVACY_GROUP)) {
				flexibleElt.setPrivacyGroup(null);
				basicChanges = true;
			}
			if (basicChanges && flexibleElt.getId() != null)
				flexibleElt = em.merge(flexibleElt);
			else
				em.persist(flexibleElt);
		}

		// ////////////////Position : Change layout_constraint, reorder
		// LayoutGroup parentLayoutGroup = em.find(LayoutGroup.class, new
		// Integer(oldGroup.getId()).longValue());
		if (group != null) { // group changed
			LayoutGroup parentLayoutGroup = em.find(LayoutGroup.class, group.getId());
			LayoutConstraint newLayoutConstraint = new LayoutConstraint();
			if (parentLayoutGroup != null) {
				newLayoutConstraint.setElement(flexibleElt);
				newLayoutConstraint.setParentLayoutGroup(parentLayoutGroup);
				if (oldOrder != null)
					newLayoutConstraint.setSortOrder(oldOrder);
				if (order != null)
					newLayoutConstraint.setSortOrder(order);
				if (order == null && oldOrder == null)
					newLayoutConstraint.setSortOrder(new Integer(parentLayoutGroup.getConstraints().size()));
				if (oldLayoutConstraintDTO != null) {// Merge
					newLayoutConstraint.setId(oldLayoutConstraintDTO.getId());
					newLayoutConstraint = em.merge(newLayoutConstraint);
				} else {// Persist
					em.persist(newLayoutConstraint);
				}
			}
		}

		// ////////////////Banner
		if (inBanner != null) {// Fact of being or not in banner has changed
			if (inBanner) {// New to banner
				if (model instanceof ProjectModel)
					changeBanner(em, posB, model, flexibleElt);
				else if (model instanceof OrgUnitModel)
					changeBanner(em, posB, model, flexibleElt);
			} else {// delete from banner
				if (oldBannerLayoutConstraintDTO != null) {
					LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO, new LayoutConstraint());
					oldBannerLayoutConstraint = em.find(LayoutConstraint.class, oldBannerLayoutConstraint.getId());
					em.remove(oldBannerLayoutConstraint);
				}
			}
		} else {// same state on banner
			if (posB != null) {// Position has changed means surely element
				// was already in banner so there's an old
				// banner layout constraint
				LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO, new LayoutConstraint());
				if (model instanceof ProjectModel)
					changePositionInBanner(em, posB, model, flexibleElt, oldBannerLayoutConstraint);
				else if (model instanceof OrgUnitModel)
					changePositionInBanner(em, posB, model, flexibleElt, oldBannerLayoutConstraint);
			}
		}

		// ////////////////Type
		if (oldType != null && type != null) {
			flexibleElt = (FlexibleElement) createNewFlexibleElement(em, oldType, type, flexibleElt);
			LOG.debug("Changed type: '{}'.", flexibleElt.getClass());
		}
		em.flush();
		em.clear();
		flexibleElt = em.find(FlexibleElement.class, flexibleElt.getId());
		// ////////////////Specific changes
		Boolean specificChanges = false;

		if ((ElementTypeEnum.DEFAULT.equals(oldType) && type == null) && DefaultFlexibleElementType.BUDGET.equals(((DefaultFlexibleElement) flexibleElt).getType())) {
			List<BudgetSubField> budgetFieldsToDelete = new ArrayList<BudgetSubField>();
			BudgetElement budgetElement = (BudgetElement) flexibleElt;
			budgetFieldsToDelete.addAll(budgetElement.getBudgetSubFields());
			budgetElement.getBudgetSubFields().clear();
			for (BudgetSubFieldDTO budgetFieldDTO : bSubFields) {
				if (budgetFieldDTO.getId() != null && budgetFieldDTO.getId() > 0) {
					BudgetSubField b = em.find(BudgetSubField.class, budgetFieldDTO.getId());
					if (b != null) {
						budgetFieldsToDelete.remove(b);
						b.setLabel(budgetFieldDTO.getLabel());
						b.setFieldOrder(budgetFieldDTO.getFieldOrder());
						b = em.merge(b);
						budgetElement.getBudgetSubFields().add(b);
					}
				} else {
					BudgetSubField budgetSubFieldToPersist = new BudgetSubField();
					budgetSubFieldToPersist.setLabel(budgetFieldDTO.getLabel());
					budgetSubFieldToPersist.setFieldOrder(budgetFieldDTO.getFieldOrder());
					budgetSubFieldToPersist.setBudgetElement(budgetElement);
					em.persist(budgetSubFieldToPersist);
					budgetElement.getBudgetSubFields().add(budgetSubFieldToPersist);
				}

			}
			for (BudgetSubField budgetFieldTODelete : budgetFieldsToDelete) {
				budgetFieldTODelete.setBudgetElement(null);
				em.remove(budgetFieldTODelete);

			}

			if (ratioDividend != null) {
				BudgetSubField budgetRatio = new BudgetSubField();
				budgetRatio.setId(ratioDividend.getId());
				budgetElement.setRatioDividend(budgetRatio);
			}
			if (ratioDivisor != null) {
				BudgetSubField budgetRatio = new BudgetSubField();
				budgetRatio.setId(ratioDivisor.getId());
				budgetElement.setRatioDivisor(budgetRatio);
			}

			flexibleElt = em.merge(budgetElement);
		} else if (ElementTypeEnum.FILES_LIST.equals(type) || (ElementTypeEnum.FILES_LIST.equals(oldType) && type == null)) {
			FilesListElement filesListElement = (FilesListElement) flexibleElt;
			// FilesListElement filesListElement =
			// em.find(FilesListElement.class, flexibleElt.getId());
			if (filesListElement != null) {
				if (maxLimit != null) {
					filesListElement.setLimit(maxLimit.intValue());
					specificChanges = true;
				}
				if (specificChanges) {
					filesListElement = em.merge(filesListElement);
					flexibleElt = filesListElement;
				}
			}
		} else if (ElementTypeEnum.TEXT_AREA.equals(type) || (ElementTypeEnum.TEXT_AREA.equals(oldType) && type == null)) {
			TextAreaElement textAreaElement = (TextAreaElement) flexibleElt;
			if (textAreaElement != null) {
				if (maxLimit != null) {
					((TextAreaElement) flexibleElt).setMaxValue(maxLimit.longValue());
					specificChanges = true;
				}
				if (minLimit != null) {
					((TextAreaElement) flexibleElt).setMinValue(minLimit.longValue());
					specificChanges = true;
				}
				if (length != null) {
					((TextAreaElement) flexibleElt).setLength(length);
					specificChanges = true;
				}
				if (decimal != null) {
					((TextAreaElement) flexibleElt).setIsDecimal(decimal);
					specificChanges = true;
				}
				if (textType != null) {
					((TextAreaElement) flexibleElt).setType(textType);
					specificChanges = true;
				}
				if (specificChanges) {
					flexibleElt = em.merge((TextAreaElement) flexibleElt);
				}
			}

		} else if (ElementTypeEnum.REPORT.equals(type) || (ElementTypeEnum.REPORT.equals(oldType) && type == null)) {
			ReportElement reportElement = em.find(ReportElement.class, flexibleElt.getId());
			if (reportElement != null) {
				if (reportModel != null && reportModel.getName() != null) {
					ProjectReportModel reportId = findReportModel(em, reportModel.getName());
					((ReportElement) flexibleElt).setModel(reportId);
					specificChanges = true;
				}

				if (specificChanges) {
					flexibleElt = em.merge((ReportElement) flexibleElt);
				}
			}
		} else if (ElementTypeEnum.REPORT_LIST.equals(type) || (ElementTypeEnum.REPORT_LIST.equals(oldType) && type == null)) {
			ReportListElement reportElement = em.find(ReportListElement.class, flexibleElt.getId());
			if (reportElement != null) {
				// BUGFIX #760: Verifiying that reportModel is not null before accessing its name.
				if (reportModel != null && reportModel.getName() != null) {
					ProjectReportModel reportId = findReportModel(em, reportModel.getName());
					((ReportListElement) flexibleElt).setModel(reportId);
					specificChanges = true;
				}

				if (specificChanges) {
					flexibleElt = em.merge((ReportListElement) flexibleElt);
				}
			}

		} else if (ElementTypeEnum.QUESTION.equals(type) || (ElementTypeEnum.QUESTION.equals(oldType) && type == null)) {

			QuestionElement questionElement = em.find(QuestionElement.class, flexibleElt.getId());
			if (questionElement != null) {

				if (isMultiple != null) {
					((QuestionElement) flexibleElt).setMultiple(isMultiple);
					specificChanges = true;
				}
				if (category != null) {

					for (QuestionChoiceElement choiceElt : ((QuestionElement) flexibleElt).getChoices()) {
						em.remove(choiceElt);
					}
					CategoryType categoryType = em.find(CategoryType.class, category.getId());
					if (categoryType != null) {
						((QuestionElement) flexibleElt).setCategoryType(categoryType);

						List<QuestionChoiceElement> choices = new ArrayList<QuestionChoiceElement>();
						int i = 0;
						for (CategoryElement catElt : categoryType.getElements()) {
							QuestionChoiceElement qChoice = new QuestionChoiceElement();
							qChoice.setLabel("");
							qChoice.setCategoryElement(catElt);
							qChoice.setParentQuestion(questionElement);
							qChoice.setSortOrder(i++);
							choices.add(qChoice);
						}
						((QuestionElement) flexibleElt).setChoices(choices);
						specificChanges = true;
					}
				} else if (qChoices != null && qChoices.size() > 0) {

					for (QuestionChoiceElement choiceElt : ((QuestionElement) flexibleElt).getChoices()) {
						em.remove(choiceElt);
					}
					((QuestionElement) flexibleElt).setCategoryType(null);
					List<QuestionChoiceElement> choices = new ArrayList<QuestionChoiceElement>();
					int i = 0;
					for (String choiceLabel : qChoices) {
						QuestionChoiceElement qChoice = new QuestionChoiceElement();
						qChoice.setLabel(choiceLabel);
						qChoice.setParentQuestion(questionElement);
						qChoice.setSortOrder(i++);
						qChoice.setDisabled(qChoicesDisabled.contains(choiceLabel));
						choices.add(qChoice);
					}
					((QuestionElement) flexibleElt).setChoices(choices);
					specificChanges = true;
				}

				if (specificChanges) {
					flexibleElt = em.merge((QuestionElement) flexibleElt);
				}
			}
			
		} else if (type == ElementTypeEnum.COMPUTATION || (type == null && oldType == ElementTypeEnum.COMPUTATION)) {
			ComputationElement computationElement = (ComputationElement) flexibleElt;
			if (computationElement != null) {
				if (computationRule != null) {
					// TODO: Parser ici la règle et faire un resolve dependencies
					computationElement.setRule(computationRule);
					specificChanges = true;
                    
                    removeAllValuesForElement(computationElement, em);
				}
				if (minLimit != null) {
					computationElement.setMinimumValue(minLimit.toString());
					specificChanges = true;
				}
				if (maxLimit != null) {
					computationElement.setMaximumValue(maxLimit.toString());
					specificChanges = true;
				}
				
				if (specificChanges) {
					flexibleElt = em.merge(computationElement);
				}
			}
		}
		em.flush();
		em.clear();
	}

	private static String retrieveTable(final String className) {

		final int bI = className.lastIndexOf(".") + 1;
		String table = className.substring(bI);

		try {

			final Class<?> c = Class.forName(className);
			final Table tableAnnotation = c.getAnnotation(Table.class);
			table = tableAnnotation.name();

		} catch (final Exception e) {
			LOG.error("Exception while retrieving 'table' annotation from the flexible element of type '" + className + "'.", e);
		}

		return table;
	}

	private static void changeOldType(final EntityManager em, final ElementTypeEnum type, final FlexibleElement flexibleElement) {

		final String oldflexTable = retrieveTable(ElementTypeEnum.getClassName(type));

		if (oldflexTable != null) {

			// If it is a question element, should delete the child choices
			if (oldflexTable.equals("question_element")) {
				em.createNativeQuery("Delete from " + "question_choice_element" + " where " + "id_question = :flexId").setParameter("flexId", flexibleElement.getId())
					.executeUpdate();
			}

			em.createNativeQuery("Delete from " + oldflexTable + " where " + "id_flexible_element = :flexId").setParameter("flexId", flexibleElement.getId())
				.executeUpdate();
		}
	}

	private static Object createNewFlexibleElement(final EntityManager em, final ElementTypeEnum oldType, final ElementTypeEnum type,
			final FlexibleElement flexibleElement) {

		Object newElement = null;
		try {

			final Class<?> elementClass = Class.forName(ElementTypeEnum.getClassName(type));
			newElement = elementClass.newInstance();

			if (flexibleElement != null && flexibleElement.getId() != null && oldType != null) {
				LOG.debug("Old Type '{}' (class '{}').", oldType, flexibleElement.getClass());
				((FlexibleElement) newElement).setCreationDate(new Date());
				((FlexibleElement) newElement).setLabel(flexibleElement.getLabel());
				((FlexibleElement) newElement).setPrivacyGroup(flexibleElement.getPrivacyGroup());
				((FlexibleElement) newElement).setValidates(flexibleElement.isValidates());
				((FlexibleElement) newElement).setAmendable(flexibleElement.isAmendable());
				((FlexibleElement) newElement).setExportable(flexibleElement.isExportable());
				((FlexibleElement) newElement).setId(flexibleElement.getId());
				final String flexTable = retrieveTable(elementClass.getName());
				// Update Type
				if (flexTable != null) {
					changeOldType(em, oldType, flexibleElement);
					em.createNativeQuery("INSERT INTO " + flexTable + " (id_flexible_element) " + "Values (:flexId)").setParameter("flexId", flexibleElement.getId())
						.executeUpdate();
				}
			}

		} catch (final Exception e) {
			LOG.error("Exception while creating new flexible element.", e);
		}

		return newElement;
	}

	private static void changeBanner(final EntityManager em, final Integer posB, final Object model, final FlexibleElement flexibleElt) {

		final LayoutGroup bannerGroup;
		if (model instanceof ProjectModel) {
			bannerGroup = ((ProjectModel) model).getProjectBanner().getLayout().getGroups().get(posB);

		} else if (model instanceof OrgUnitModel) {
			bannerGroup = ((OrgUnitModel) model).getBanner().getLayout().getGroups().get(posB);

		} else {
			throw new UnsupportedOperationException("Invalid model type.");
		}

		LayoutConstraint newLayoutConstraint = null;
		boolean positionTaken = false;

		for (final LayoutConstraint lc : bannerGroup.getConstraints()) {
			if (posB.equals(lc.getSortOrder())) {
				positionTaken = true;
				newLayoutConstraint = lc;
				lc.setElement(flexibleElt);

				newLayoutConstraint = em.merge(lc);
			}
		}

		if (!positionTaken) {
			newLayoutConstraint = new LayoutConstraint();

			newLayoutConstraint.setElement(flexibleElt);
			newLayoutConstraint.setParentLayoutGroup(bannerGroup);
			newLayoutConstraint.setSortOrder(new Integer(posB + 1));

			em.persist(newLayoutConstraint);
		}
	}

	private static void changePositionInBanner(final EntityManager em, final Integer posB, final Object model, final FlexibleElement flexibleElt,
			final LayoutConstraint oldBannerLayoutConstraint) {

		final LayoutGroup bannerGroup;
		if (model instanceof ProjectModel) {
			bannerGroup = ((ProjectModel) model).getProjectBanner().getLayout().getGroups().get(posB);

		} else if (model instanceof OrgUnitModel) {
			bannerGroup = ((OrgUnitModel) model).getBanner().getLayout().getGroups().get(posB);

		} else {
			throw new UnsupportedOperationException("Invalid model type.");
		}

		// Delete any constraint that places another flexible element in the same position.
		for (final LayoutConstraint lc : bannerGroup.getConstraints()) {
			em.remove(lc);
		}

		oldBannerLayoutConstraint.setElement(flexibleElt);
		oldBannerLayoutConstraint.setParentLayoutGroup(bannerGroup);
		oldBannerLayoutConstraint.setSortOrder(new Integer(posB + 1));
		em.merge(oldBannerLayoutConstraint);
	}

	private static ProjectReportModel findReportModel(final EntityManager em, final String reportName) {

		final TypedQuery<ProjectReportModel> query = em.createQuery("SELECT r FROM ProjectReportModel r WHERE r.name = :name", ProjectReportModel.class);
		query.setParameter("name", reportName);

		try {

			return query.getSingleResult();

		} catch (final Exception e) {
			return null;
		}
	}
    
    /**
     * Removes every value associated with the given flexible element.
     * 
     * @param element 
     *          Element to purge.
     */
    private static void removeAllValuesForElement(final FlexibleElement element, final EntityManager em) {
        em.createQuery("DELETE FROM Value AS v WHERE v.element = :element")
                .setParameter("element", element)
                .executeUpdate();
    }

	/**
	 * Utility class private constructor.
	 */
	private ModelUtil() {
		// Only provides static methods.
	}
}
