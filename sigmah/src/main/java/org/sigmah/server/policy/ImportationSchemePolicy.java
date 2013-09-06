package org.sigmah.server.policy;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.importation.ImportationScheme;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.domain.importation.Variable;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.google.inject.Inject;

public class ImportationSchemePolicy implements EntityPolicy<ImportationScheme> {
	private final Mapper mapper;
	private final EntityManager em;
	private ImportationScheme importationScheme;
	private static final Log log = LogFactory.getLog(ImportationSchemePolicy.class);

	@Inject
	public ImportationSchemePolicy(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
		this.importationScheme = null;
	}

	@Override
	public Object create(User user, PropertyMap properties) {
		ImportationSchemeDTO schemaToUpdate = properties.get(AdminUtil.ADMIN_SCHEMA);
		String name = null;
		if (properties.containsKey(AdminUtil.PROP_SCH_NAME)) {
			name = (String) properties.get(AdminUtil.PROP_SCH_NAME);
		}
		ImportationSchemeImportType schImportType = null;
		ImportationSchemeFileFormat schFileFormat = null;
		if (properties.containsKey(AdminUtil.PROP_SCH_FILE_FORMAT)) {
			schFileFormat = (ImportationSchemeFileFormat) properties.get(AdminUtil.PROP_SCH_FILE_FORMAT);
		}
		if (properties.containsKey(AdminUtil.PROP_SCH_IMPORT_TYPE)) {
			schImportType = (ImportationSchemeImportType) properties.get(AdminUtil.PROP_SCH_IMPORT_TYPE);
		}
		if (schemaToUpdate.getId() > 0) {
			importationScheme = em.find(ImportationScheme.class, new Integer(schemaToUpdate.getId()).longValue());
			if (importationScheme != null) {
				update(user, importationScheme, properties);
			}
		} else {
			importationScheme = new ImportationScheme();
			importationScheme.setName(name);
			importationScheme.setFileFormat(schFileFormat);
			importationScheme.setImportType(schImportType);
			importationScheme.setFirstRow(0);
			em.persist(importationScheme);
		}
		ImportationSchemeDTO importationSchemeDTO = mapper.map(importationScheme, ImportationSchemeDTO.class);
		return importationSchemeDTO;
	}

	@Override
	public void update(User user, Object entity, PropertyMap changes) {
		if (entity != null) {
			importationScheme = em.find(ImportationScheme.class, ((ImportationScheme) entity).getId());
		}
		if (importationScheme != null) {
			if (changes.containsKey(AdminUtil.PROP_SCH_NAME)) {
				String schName = changes.get(AdminUtil.PROP_SCH_NAME);
				importationScheme.setName(schName);
			}
			if (changes.containsKey(AdminUtil.PROP_SCH_FIRST_ROW)) {
				Integer schFirstRow = (Integer) changes.get(AdminUtil.PROP_SCH_FIRST_ROW);
				importationScheme.setFirstRow(schFirstRow);;
			} else {
				importationScheme.setFirstRow(0);
			}
			if (changes.containsKey(AdminUtil.PROP_SCH_SHEET_NAME)) {
				String schSheetName = (String) changes.get(AdminUtil.PROP_SCH_SHEET_NAME);
				importationScheme.setSheetName(schSheetName);
			}
			if (changes.containsKey(AdminUtil.PROP_VAR_VARIABLE)) {
				persistVariable(user, importationScheme, changes);
			}

			importationScheme = em.merge(importationScheme);
		}
	}

	public void persistVariable(User user, Object entity, PropertyMap properties) {
		VariableDTO var = (VariableDTO) properties.get(AdminUtil.PROP_VAR_VARIABLE);
		Variable varToUpdate = null;
		if (var.getId() > 0) {
			varToUpdate = em.find(Variable.class, new Integer(var.getId()).longValue());
		} else {
			varToUpdate = new Variable();
			importationScheme.getVariables().add(varToUpdate);
			varToUpdate.setImportationScheme(importationScheme);
		}

		if (properties.containsKey(AdminUtil.PROP_VAR_NAME)) {
			String varName = properties.get(AdminUtil.PROP_VAR_NAME);
			varToUpdate.setName(varName);
		}
		if (properties.containsKey(AdminUtil.PROP_VAR_REFERENCE)) {
			String varReference = properties.get(AdminUtil.PROP_VAR_REFERENCE);
			varToUpdate.setReference(varReference);
		}
		if(varToUpdate.getId() != null){
			em.merge(varToUpdate);
		} else {
			em.persist(varToUpdate);
		}
		
	}

}
