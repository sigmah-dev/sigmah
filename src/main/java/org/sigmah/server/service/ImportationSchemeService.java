package org.sigmah.server.service;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.Variable;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.google.inject.Singleton;

/**
 * {@link ImportationScheme} policy.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
@Singleton
public class ImportationSchemeService extends AbstractEntityService<ImportationScheme, Integer, ImportationSchemeDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportationScheme create(final PropertyMap properties, final UserExecutionContext context) {

		final ImportationScheme importationScheme;
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
		
		if (schemaToUpdate.getId() != null) {
			importationScheme = em().find(ImportationScheme.class, schemaToUpdate.getId());
			if (importationScheme != null) {
				update(schemaToUpdate.getId(), properties, context);
			}
		} else {
			importationScheme = new ImportationScheme();
			importationScheme.setName(name);
			importationScheme.setFileFormat(schFileFormat);
			importationScheme.setImportType(schImportType);
			importationScheme.setFirstRow(0);
			em().persist(importationScheme);
		}

		return importationScheme;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportationScheme update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {

		ImportationScheme importationScheme = em().find(ImportationScheme.class, entityId);

		if (importationScheme == null) {
			throw new IllegalArgumentException("No '" + entityClass.getSimpleName() + "' cannot be found for id #" + entityId + ".");
		}

		if (changes.containsKey(AdminUtil.PROP_SCH_NAME)) {
			final String schName = changes.get(AdminUtil.PROP_SCH_NAME);
			importationScheme.setName(schName);
		}
		
		if (changes.containsKey(AdminUtil.PROP_SCH_FILE_FORMAT)) {
			importationScheme.setFileFormat((ImportationSchemeFileFormat) changes.get(AdminUtil.PROP_SCH_FILE_FORMAT));
		}
		if (changes.containsKey(AdminUtil.PROP_SCH_IMPORT_TYPE)) {
			importationScheme.setImportType( (ImportationSchemeImportType) changes.get(AdminUtil.PROP_SCH_IMPORT_TYPE));
		}

		if (changes.containsKey(AdminUtil.PROP_SCH_FIRST_ROW)) {
			final Integer schFirstRow = (Integer) changes.get(AdminUtil.PROP_SCH_FIRST_ROW);
			importationScheme.setFirstRow(schFirstRow);
		} else {
			importationScheme.setFirstRow(0);
		}

		if (changes.containsKey(AdminUtil.PROP_SCH_SHEET_NAME)) {
			final String schSheetName = (String) changes.get(AdminUtil.PROP_SCH_SHEET_NAME);
			importationScheme.setSheetName(schSheetName);
		}

		if (changes.containsKey(AdminUtil.PROP_VAR_VARIABLE)) {
			persistVariable(importationScheme, changes, context.getUser());
		}

		return em().merge(importationScheme);
	}

	/**
	 * TODO JavaDoc
	 * 
	 * @param importationScheme
	 * @param properties
	 * @param user
	 */
	public void persistVariable(final ImportationScheme importationScheme, final PropertyMap properties, final User user) {

		final VariableDTO var = (VariableDTO) properties.get(AdminUtil.PROP_VAR_VARIABLE);
		Variable varToUpdate = null;

		if (var.getId() != null) {
			varToUpdate = em().find(Variable.class, var.getId());

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

		if (varToUpdate.getId() != null) {
			em().merge(varToUpdate);

		} else {
			em().persist(varToUpdate);
		}
	}

}
