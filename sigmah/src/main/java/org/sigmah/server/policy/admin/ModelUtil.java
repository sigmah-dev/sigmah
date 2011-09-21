package org.sigmah.server.policy.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.server.policy.PropertyMap;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.domain.element.FilesListElement;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.element.ReportElement;
import org.sigmah.shared.domain.element.ReportListElement;
import org.sigmah.shared.domain.element.TextAreaElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

public class ModelUtil {

    private final static Log log = LogFactory.getLog(ModelUtil.class);

    @SuppressWarnings("unchecked")
    public static void persistFlexibleElement(EntityManager em, Mapper mapper, PropertyMap changes, Object model) {

        FlexibleElementDTO flexibleEltDTO = null;
        if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null) {
            // Common attributes
            String name = (String) changes.get(AdminUtil.PROP_FX_NAME);
            ElementTypeEnum type = (ElementTypeEnum) changes.get(AdminUtil.PROP_FX_TYPE);
            Boolean isCompulsory = null;
            if (changes.get(AdminUtil.PROP_FX_IS_COMPULSARY) != null)
                isCompulsory = (Boolean) changes.get(AdminUtil.PROP_FX_IS_COMPULSARY);
            PrivacyGroupDTO pg = null;
            if (changes.get(AdminUtil.PROP_FX_PRIVACY_GROUP) != null)
                pg = (PrivacyGroupDTO) changes.get(AdminUtil.PROP_FX_PRIVACY_GROUP);
            Boolean amend = null;
            if (changes.get(AdminUtil.PROP_FX_AMENDABLE) != null)
                amend = (Boolean) changes.get(AdminUtil.PROP_FX_AMENDABLE);

            // Position
            LayoutGroupDTO group = null;
            if (changes.get(AdminUtil.PROP_FX_GROUP) != null)
                group = (LayoutGroupDTO) changes.get(AdminUtil.PROP_FX_GROUP);
            Integer order = null;
            if (changes.get(AdminUtil.PROP_FX_ORDER_IN_GROUP) != null)
                order = (Integer) changes.get(AdminUtil.PROP_FX_ORDER_IN_GROUP);
            Boolean inBanner = null;
            if (changes.get(AdminUtil.PROP_FX_IN_BANNER) != null)
                inBanner = (Boolean) changes.get(AdminUtil.PROP_FX_IN_BANNER);
            Integer posB = null;
            if (changes.get(AdminUtil.PROP_FX_POS_IN_BANNER) != null) {
                posB = (Integer) changes.get(AdminUtil.PROP_FX_POS_IN_BANNER);
                posB = posB - 1;
            }

            // FIXME
            HashMap<String, Object> oldLayoutFields = (HashMap<String, Object>) changes
                    .get(AdminUtil.PROP_FX_OLD_FIELDS);
            LayoutConstraintDTO oldLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields
                    .get(AdminUtil.PROP_FX_LC);
            LayoutConstraintDTO oldBannerLayoutConstraintDTO = (LayoutConstraintDTO) oldLayoutFields
                    .get(AdminUtil.PROP_FX_LC_BANNER);
            ElementTypeEnum oldType = (ElementTypeEnum) oldLayoutFields.get(AdminUtil.PROP_FX_TYPE);
            Integer oldOrder = (Integer) oldLayoutFields.get(AdminUtil.PROP_FX_ORDER_IN_GROUP);

            // Specific attributes
            Character textType = (Character) changes.get(AdminUtil.PROP_FX_TEXT_TYPE);
            Integer maxLimit = null;
            if (changes.get(AdminUtil.PROP_FX_MAX_LIMIT) != null)
                maxLimit = (Integer) changes.get(AdminUtil.PROP_FX_MAX_LIMIT);
            Integer minLimit = null;
            if (changes.get(AdminUtil.PROP_FX_MIN_LIMIT) != null)
                minLimit = (Integer) changes.get(AdminUtil.PROP_FX_MIN_LIMIT);
            Integer length = null;
            if (changes.get(AdminUtil.PROP_FX_LENGTH) != null)
                length = (Integer) changes.get(AdminUtil.PROP_FX_LENGTH);
            Boolean decimal = null;
            if (changes.get(AdminUtil.PROP_FX_DECIMAL) != null)
                decimal = (Boolean) changes.get(AdminUtil.PROP_FX_DECIMAL);

            ReportModelDTO reportModel = null;
            if (changes.get(AdminUtil.PROP_FX_REPORT_MODEL) != null)
                reportModel = (ReportModelDTO) changes.get(AdminUtil.PROP_FX_REPORT_MODEL);

            Boolean isMultiple = null;
            if (changes.get(AdminUtil.PROP_FX_Q_MULTIPLE) != null)
                isMultiple = (Boolean) changes.get(AdminUtil.PROP_FX_Q_MULTIPLE);
            CategoryTypeDTO category = null;
            if (changes.get(AdminUtil.PROP_FX_Q_CATEGORY) != null)
                category = (CategoryTypeDTO) changes.get(AdminUtil.PROP_FX_Q_CATEGORY);
            List<String> qChoices = null;
            if (changes.get(AdminUtil.PROP_FX_Q_CHOICES) != null)
                qChoices = (List<String>) changes.get(AdminUtil.PROP_FX_Q_CHOICES);

            flexibleEltDTO = (FlexibleElementDTO) changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT);

            FlexibleElement flexibleElt = null;
            if (flexibleEltDTO.getId() != 0) {
                flexibleElt = em.find(FlexibleElement.class, new Integer(flexibleEltDTO.getId()).longValue());
            } else {
                flexibleElt = (FlexibleElement) createNewFlexibleElement(em, oldType, type, flexibleElt);
            }

            log.debug("Saving : (" + name + "," + type + "," + group + "," + order + "," + inBanner + "," + posB + ","
                    + isCompulsory + "," + pg + "," + amend + ")");
            log.debug("Also Saving : (" + maxLimit + "," + minLimit + "," + textType + "," + length + "," + decimal
                    + "," + reportModel + ")");

            Boolean basicChanges = false;
            if (flexibleElt != null) {// update flexible element
                // //////////////// First, basic attributes
                if (name != null) {
                    flexibleElt.setLabel(name);
                    basicChanges = true;
                }
                if (amend != null) {
                    flexibleElt.setAmendable(amend);
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
                LayoutGroup parentLayoutGroup = em.find(LayoutGroup.class, new Integer(group.getId()).longValue());
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
                        newLayoutConstraint.setId(new Integer(oldLayoutConstraintDTO.getId()).longValue());
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
                        changeBanner(em, posB, (ProjectModel) model, flexibleElt);
                    else if (model instanceof OrgUnitModel)
                        changeBanner(em, posB, (OrgUnitModel) model, flexibleElt);
                } else {// delete from banner
                    if (oldBannerLayoutConstraintDTO != null) {
                        LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO,
                                LayoutConstraint.class);
                        oldBannerLayoutConstraint = em.find(LayoutConstraint.class, oldBannerLayoutConstraint.getId());
                        em.remove(oldBannerLayoutConstraint);
                    }
                }
            } else {// same state on banner
                if (posB != null) {// Position has changed means surely element
                                   // was already in banner so there's an old
                                   // banner layout constraint
                    LayoutConstraint oldBannerLayoutConstraint = mapper.map(oldBannerLayoutConstraintDTO,
                            LayoutConstraint.class);
                    if (model instanceof ProjectModel)
                        changePositionInBanner(em, posB, (ProjectModel) model, flexibleElt, oldBannerLayoutConstraint);
                    else if (model instanceof OrgUnitModel)
                        changePositionInBanner(em, posB, (OrgUnitModel) model, flexibleElt, oldBannerLayoutConstraint);
                }
            }

            // ////////////////Type
            if (oldType != null && type != null) {
                flexibleElt = (FlexibleElement) createNewFlexibleElement(em, oldType, type, flexibleElt);
                log.debug("changed type " + flexibleElt.getClass());
            }
            em.flush();
            em.clear();
            flexibleElt = em.find(FlexibleElement.class, flexibleElt.getId());
            // ////////////////Specific changes
            Boolean specificChanges = false;

            if (ElementTypeEnum.FILES_LIST.equals(type) || (ElementTypeEnum.FILES_LIST.equals(oldType) && type == null)) {
                FilesListElement filesListElement = (FilesListElement) flexibleElt;
                // FilesListElement filesListElement =
                // em.find(FilesListElement.class, flexibleElt.getId());
                if (filesListElement != null) {
                    if (maxLimit != null) {
                        filesListElement.setLimit(maxLimit);
                        specificChanges = true;
                    }
                    if (specificChanges) {
                        filesListElement = em.merge(filesListElement);
                        flexibleElt = filesListElement;
                    }
                }
            } else if (ElementTypeEnum.TEXT_AREA.equals(type)
                    || (ElementTypeEnum.TEXT_AREA.equals(oldType) && type == null)) {
                TextAreaElement textAreaElement = (TextAreaElement) flexibleElt;
                if (textAreaElement != null) {
                    if (maxLimit != null) {
                        ((TextAreaElement) flexibleElt).setMaxValue(new Integer(maxLimit).longValue());
                        specificChanges = true;
                    }
                    if (minLimit != null) {
                        ((TextAreaElement) flexibleElt).setMinValue(new Integer(minLimit).longValue());
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
                    if (reportModel != null) {
                        if (reportModel.getName() != null) {
                            ProjectReportModel reportId = findReportModel(em, reportModel.getName());
                            ((ReportElement) flexibleElt).setModel(reportId);
                            specificChanges = true;
                        }

                    }

                    if (specificChanges) {
                        flexibleElt = em.merge((ReportElement) flexibleElt);
                    }
                }
            } else if (ElementTypeEnum.REPORT_LIST.equals(type)
                    || (ElementTypeEnum.REPORT_LIST.equals(oldType) && type == null)) {
                ReportListElement reportElement = em.find(ReportListElement.class, flexibleElt.getId());
                if (reportElement != null) {
                    if (reportModel.getName() != null) {
                        ProjectReportModel reportId = findReportModel(em, reportModel.getName());
                        ((ReportListElement) flexibleElt).setModel(reportId);
                        specificChanges = true;
                    }

                    if (specificChanges) {
                        flexibleElt = em.merge((ReportListElement) flexibleElt);
                    }
                }

            } else if (ElementTypeEnum.QUESTION.equals(type)
                    || (ElementTypeEnum.QUESTION.equals(oldType) && type == null)) {

                QuestionElement questionElement = em.find(QuestionElement.class, flexibleElt.getId());
                if (questionElement != null) {

                    if (isMultiple != null) {
                        ((QuestionElement) flexibleElt).setIsMultiple(isMultiple);
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
                            choices.add(qChoice);
                        }
                        ((QuestionElement) flexibleElt).setChoices(choices);
                        specificChanges = true;
                    }

                    if (specificChanges) {
                        flexibleElt = em.merge((QuestionElement) flexibleElt);
                    }
                }
            }
            em.flush();
            em.clear();
        }
    }

    public static String retrieveTable(String className) {
        String table = null;
        int bI = className.lastIndexOf(".") + 1;
        table = className.substring(bI);

        try {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Class c = (Class<FlexibleElement>) Class.forName(className);
            @SuppressWarnings("unchecked")
            Table a = (Table) c.getAnnotation(Table.class);
            table = a.name();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return table;
    }

    private static void changeOldType(EntityManager em, ElementTypeEnum type, FlexibleElement flexibleElement) {
        String oldflexTable = retrieveTable(ElementTypeEnum.getClassName(type));

        if (oldflexTable != null) {

            // If it is a question element, should delete the child choices
            if (oldflexTable.equals("question_element")) {
                em.createNativeQuery("Delete from " + "question_choice_element" + " where " + "id_question = :flexId")
                        .setParameter("flexId", flexibleElement.getId()).executeUpdate();
            }

            em.createNativeQuery("Delete from " + oldflexTable + " where " + "id_flexible_element = :flexId")
                    .setParameter("flexId", flexibleElement.getId()).executeUpdate();
        }
    }

    private static Object createNewFlexibleElement(EntityManager em, ElementTypeEnum oldType, ElementTypeEnum type,
            FlexibleElement flexibleElement) {

        String flexTable = null;

        @SuppressWarnings("rawtypes")
        Class c;
        Object newElement = null;
        try {
            c = Class.forName(ElementTypeEnum.getClassName(type));
            newElement = c.newInstance();

            if (flexibleElement != null && flexibleElement.getId() != null && oldType != null) {
                log.debug("Old Type " + oldType + " " + flexibleElement.getClass());
                ((FlexibleElement) newElement).setLabel(flexibleElement.getLabel());
                ((FlexibleElement) newElement).setPrivacyGroup(flexibleElement.getPrivacyGroup());
                ((FlexibleElement) newElement).setValidates(flexibleElement.isValidates());
                ((FlexibleElement) newElement).setAmendable(flexibleElement.isAmendable());
                ((FlexibleElement) newElement).setId(flexibleElement.getId());
                flexTable = retrieveTable(c.getName());
                // Update Type
                if (flexTable != null) {
                    changeOldType(em, oldType, flexibleElement);
                    em.createNativeQuery("INSERT INTO " + flexTable + " (id_flexible_element) " + "Values (:flexId)")
                            .setParameter("flexId", flexibleElement.getId()).executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newElement;
    }

    private static void changeBanner(EntityManager em, Integer posB, Object model, FlexibleElement flexibleElt) {
        LayoutGroup bannerGroup = null;
        if (model instanceof ProjectModel)
            bannerGroup = ((ProjectModel) model).getProjectBanner().getLayout().getGroups().get(posB);
        else if (model instanceof OrgUnitModel)
            bannerGroup = ((OrgUnitModel) model).getBanner().getLayout().getGroups().get(posB);

        LayoutConstraint newLayoutConstraint = null;
        boolean positionTaken = false;
        for (LayoutConstraint lc : bannerGroup.getConstraints()) {
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

    private static void changePositionInBanner(EntityManager em, Integer posB, Object model,
            FlexibleElement flexibleElt, LayoutConstraint oldBannerLayoutConstraint) {

        LayoutGroup bannerGroup = null;
        if (model instanceof ProjectModel)
            bannerGroup = ((ProjectModel) model).getProjectBanner().getLayout().getGroups().get(posB);
        else if (model instanceof OrgUnitModel)
            bannerGroup = ((OrgUnitModel) model).getBanner().getLayout().getGroups().get(posB);

        // Delete any constraint that places another flexible element in the
        // same position
        for (LayoutConstraint lc : bannerGroup.getConstraints()) {
            em.remove(lc);
        }
        oldBannerLayoutConstraint.setElement(flexibleElt);
        oldBannerLayoutConstraint.setParentLayoutGroup(bannerGroup);
        oldBannerLayoutConstraint.setSortOrder(new Integer(posB + 1));
        em.merge(oldBannerLayoutConstraint);
    }

    private static ProjectReportModel findReportModel(EntityManager em, String reportName) {
        final Query query = em.createQuery("Select r from ProjectReportModel r Where r.name = :name").setParameter(
                "name", reportName);

        try {
            if (query.getSingleResult() != null) {
                ProjectReportModel report = (ProjectReportModel) query.getSingleResult();
                return report;
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
