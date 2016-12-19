package org.sigmah.server.service;

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
import java.util.List;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.domain.importation.VariableBudgetElement;
import org.sigmah.server.domain.importation.VariableBudgetSubField;
import org.sigmah.server.domain.importation.VariableBudgetSubFieldId;
import org.sigmah.server.domain.importation.VariableFlexibleElement;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * {@link ImportationSchemeModel} entity policy.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ImportationSchemeModelService extends AbstractEntityService<ImportationSchemeModel, Integer, ImportationSchemeModelDTO> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ImportationSchemeModelService.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportationSchemeModel create(final PropertyMap properties, final UserExecutionContext context) {

		final ImportationSchemeModel importationSchemeModel = new ImportationSchemeModel();

		ImportationSchemeModelDTO importationSchemeModelDTO = (ImportationSchemeModelDTO) properties.get(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL);
		ImportationSchemeDTO importationSchemeDTO = properties.get(AdminUtil.ADMIN_SCHEMA);
		ProjectModelDTO projectModelDTO = null;
		OrgUnitModelDTO orgUnitModelDTO = null;
		ContactModelDTO contactModelDTO = null;

		if (properties.get(AdminUtil.ADMIN_PROJECT_MODEL) != null) {
			projectModelDTO = properties.get(AdminUtil.ADMIN_PROJECT_MODEL);

		} else if (properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL) != null) {
			orgUnitModelDTO = properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL);
		} else if (properties.get(AdminUtil.ADMIN_CONTACT_MODEL) != null) {
			contactModelDTO = properties.get(AdminUtil.ADMIN_CONTACT_MODEL);
		}

		if (importationSchemeModelDTO.getId() != null) {
			update(importationSchemeModelDTO.getId(), properties, context);

		} else {
			if (importationSchemeDTO != null && (projectModelDTO != null || orgUnitModelDTO != null || contactModelDTO != null)) {
				ImportationScheme importationScheme = em().find(ImportationScheme.class, importationSchemeDTO.getId());
				importationSchemeModel.setImportationScheme(importationScheme);
				if (projectModelDTO != null) {
					ProjectModel pm = em().find(ProjectModel.class, projectModelDTO.getId());
					importationSchemeModel.setProjectModel(pm);

				} else if (orgUnitModelDTO != null) {
					OrgUnitModel oum = em().find(OrgUnitModel.class, orgUnitModelDTO.getId());
					importationSchemeModel.setOrgUnitModel(oum);

				} else {
					ContactModel cm = em().find(ContactModel.class, contactModelDTO.getId());
					importationSchemeModel.setContactModel(cm);
				}
				em().persist(importationSchemeModel);
			}
		}

		return importationSchemeModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportationSchemeModel update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		if (changes.get(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL) == null) {
			throw new IllegalArgumentException("No importation scheme model found into properties map.");
		}

		if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) == null) {
			throw new IllegalArgumentException("No flexible element found into properties map.");
		}

		// Retrieve the importationSchemeModel.
		ImportationSchemeModel importationSchemeModel = em().find(ImportationSchemeModel.class, entityId);

		FlexibleElementDTO flexibleElementDTO = (FlexibleElementDTO) changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT);

		if (flexibleElementDTO instanceof BudgetElementDTO) {

			if (changes.get(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS) != null) {
				VariableBudgetElement varBe = new VariableBudgetElement();

				BudgetElement fle = em().find(BudgetElement.class, flexibleElementDTO.getId());
				varBe.setFlexibleElement(fle);
				
				final Boolean key = changes.get(AdminUtil.PROP_VAR_FLE_ID_KEY);
				varBe.setIsKey(key != null && key);
				
				varBe.setImportationSchemeModel(importationSchemeModel);
				
				em().persist(varBe);

				@SuppressWarnings("unchecked")
				List<VariableBudgetSubFieldDTO> varBfDTOs = (List<VariableBudgetSubFieldDTO>) changes.get(AdminUtil.PROP_VAR_FLE_BUDGETSUBFIELDS);
				List<VariableBudgetSubField> varBsfs = new ArrayList<VariableBudgetSubField>();

				for (VariableBudgetSubFieldDTO vbsf : varBfDTOs) {
					VariableBudgetSubField varBsf = new VariableBudgetSubField();

					BudgetSubField bsf = em().find(BudgetSubField.class, vbsf.getBudgetSubFieldDTO().getId());
					varBsf.setBudgetSubField(bsf);

					Variable variable = em().find(Variable.class, vbsf.getVariableDTO().getId());
					varBsf.setVariable(variable);

					varBsf.setVariableBudgetElement(varBe);

					VariableBudgetSubFieldId varbsfId = new VariableBudgetSubFieldId();
					varbsfId.setBudgetSubFieldId(bsf.getId());
					varbsfId.setVarId(variable.getId());
					varbsfId.setVariableFlexibleId(varBe.getId());

					varBsf.setId(varbsfId);

					varBsfs.add(varBsf);
				}

				varBe.setVariableBudgetSubFields(varBsfs);

				em().merge(varBe);
				importationSchemeModel.getVariableFlexibleElements().add(varBe);
			}

		} else {

			if (changes.get(AdminUtil.PROP_VAR_VARIABLE) != null) {

				VariableDTO variableDTO = (VariableDTO) changes.get(AdminUtil.PROP_VAR_VARIABLE);

				VariableFlexibleElement varfle = new VariableFlexibleElement();

				Variable variable = em().find(Variable.class, variableDTO.getId());
				varfle.setVariable(variable);

				FlexibleElement fle = em().find(FlexibleElement.class, flexibleElementDTO.getId());
				varfle.setFlexibleElement(fle);

				final Boolean key = changes.get(AdminUtil.PROP_VAR_FLE_ID_KEY);
				varfle.setIsKey(key != null && key);

				varfle.setImportationSchemeModel(importationSchemeModel);

				em().persist(varfle);
				importationSchemeModel.getVariableFlexibleElements().add(varfle);
			}

		}

		return em().merge(importationSchemeModel);
	}
	
}
