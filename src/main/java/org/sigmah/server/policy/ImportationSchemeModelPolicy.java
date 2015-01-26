package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.hibernate.mapping.Array;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.BudgetElement;
import org.sigmah.shared.domain.element.BudgetSubField;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.importation.ImportationScheme;
import org.sigmah.shared.domain.importation.ImportationSchemeModel;
import org.sigmah.shared.domain.importation.Variable;
import org.sigmah.shared.domain.importation.VariableBudgetElement;
import org.sigmah.shared.domain.importation.VariableBudgetSubField;
import org.sigmah.shared.domain.importation.VariableBudgetSubFieldId;
import org.sigmah.shared.domain.importation.VariableFlexibleElement;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.google.inject.Inject;

public class ImportationSchemeModelPolicy implements EntityPolicy<ImportationScheme> {
	private final Mapper mapper;
	private final EntityManager em;
	private ImportationSchemeModel importationSchemeModel;
	private static final Log log = LogFactory.getLog(ImportationSchemeModelPolicy.class);

	@Inject
	public ImportationSchemeModelPolicy(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
		this.importationSchemeModel = new ImportationSchemeModel();
	}

	@Override
	public Object create(User user, PropertyMap properties) {
		ImportationSchemeModelDTO importationSchemeModelDTO = (ImportationSchemeModelDTO) properties
						.get(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL);
		ImportationSchemeDTO importationSchemeDTO = properties.get(AdminUtil.ADMIN_SCHEMA);
		ProjectModelDTO projectModelDTO = null;
		OrgUnitModelDTO orgUnitModelDTO = null;
		if (properties.get(AdminUtil.ADMIN_PROJECT_MODEL) != null) {
			projectModelDTO = properties.get(AdminUtil.ADMIN_PROJECT_MODEL);
		} else if (properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL) != null) {
			orgUnitModelDTO = properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL);
		}
		if (importationSchemeModelDTO.getId() > 0) {
			update(user, importationSchemeModelDTO.getId(), properties);
		} else {
			if (importationSchemeDTO != null && (projectModelDTO != null || orgUnitModelDTO != null)) {
				ImportationScheme importationScheme = em.find(ImportationScheme.class,
								Long.valueOf(importationSchemeDTO.getId()));
				importationSchemeModel.setImportationScheme(importationScheme);
				if (projectModelDTO != null) {
					ProjectModel pm = em.find(ProjectModel.class, Long.valueOf(projectModelDTO.getId()));
					importationSchemeModel.setProjectModel(pm);
				} else {
					OrgUnitModel oum = em.find(OrgUnitModel.class, orgUnitModelDTO.getId());
					importationSchemeModel.setOrgUnitModel(oum);

				}
				em.persist(importationSchemeModel);
			}
		}

		importationSchemeModelDTO = mapper.map(importationSchemeModel, ImportationSchemeModelDTO.class);
		return importationSchemeModelDTO;
	}

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		// Retrieve the importationSchemeModel

		if (changes.get(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL) != null) {
			importationSchemeModel = (ImportationSchemeModel) em.find(ImportationSchemeModel.class,
							Long.valueOf((Integer) entityId));

			if ( changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null) {
				FlexibleElementDTO flexibleElementDTO = (FlexibleElementDTO) changes
								.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT);
				

				if(flexibleElementDTO instanceof BudgetElementDTO) {
					if(changes.get(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS) != null){
						VariableBudgetElement varBe = new VariableBudgetElement();
						
						BudgetElement fle = em.find(BudgetElement.class, Long.valueOf(flexibleElementDTO.getId()));
						varBe.setFlexibleElement(fle);
						
						List<VariableBudgetSubFieldDTO> varBfDTOs = (List<VariableBudgetSubFieldDTO>) changes.get(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS);
						List<VariableBudgetSubField> varBsfs = new ArrayList<VariableBudgetSubField>();
						
						for(VariableBudgetSubFieldDTO vbsf : varBfDTOs){
							VariableBudgetSubField varBsf = new VariableBudgetSubField();
							
							BudgetSubField bsf = em.find(BudgetSubField.class, Long.valueOf(vbsf.getBudgetSubFieldDTO().getId()));
							varBsf.setBudgetSubField(bsf);
							
							Variable variable = em.find(Variable.class, Long.valueOf(vbsf.getVariableDTO().getId()));
							varBsf.setVariable(variable);
							
							VariableBudgetSubFieldId varbsfId = new VariableBudgetSubFieldId();
							varbsfId.setBudgetSubFieldId(bsf.getId());
							varbsfId.setVarId(variable.getId());

							varBsf.setId(varbsfId);
							
							varBsf.setVariableBudgetElement(varBe);
							
							varBsfs.add(varBsf);
						}
						
						varBe.setVariableBudgetSubFields(varBsfs);
						

						
						if (changes.get(AdminUtil.PROP_VAR_FLE_ID_KEY) != null) {
							varBe.setIsKey(true);
						} else {
							varBe.setIsKey(false);
						}
						
						varBe.setImportationSchemeModel(importationSchemeModel);

						em.persist(varBe);
						importationSchemeModel.getVariableFlexibleElements().add(varBe);
						
					} 
				} else {
					if(changes.get(AdminUtil.PROP_VAR_VARIABLE) != null) {

						VariableDTO variableDTO = (VariableDTO) changes.get(AdminUtil.PROP_VAR_VARIABLE);
						
						VariableFlexibleElement varfle = new VariableFlexibleElement();


						Variable variable = em.find(Variable.class, Long.valueOf(variableDTO.getId()));
						varfle.setVariable(variable);

					
						
						FlexibleElement fle = em.find(FlexibleElement.class, Long.valueOf(flexibleElementDTO.getId()));
						varfle.setFlexibleElement(fle);
						
						if (changes.get(AdminUtil.PROP_VAR_FLE_ID_KEY) != null) {
							varfle.setIsKey(true);
						} else {
							varfle.setIsKey(false);
						}
						
						varfle.setImportationSchemeModel(importationSchemeModel);
						
						em.persist(varfle);
						importationSchemeModel.getVariableFlexibleElements().add(varfle);


					}
					
				}

				importationSchemeModel = em.merge(importationSchemeModel);
				
			}
		}

	}

}
