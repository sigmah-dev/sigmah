package org.sigmah.server.handler;

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


import java.io.InputStream;
import org.sigmah.shared.command.GetImportInformation;
import org.sigmah.shared.command.result.ImportInformationResult;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.IOException;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.servlet.importer.Importer;
import org.sigmah.server.servlet.importer.Importers;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetImportInformation} command.
 * <p/>
 * Retrieve the file matching the given <code>fileId</code> and parse it with 
 * the given importation scheme. Results are not applied by this command.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class GetImportInformationHandler extends AbstractCommandHandler<GetImportInformation, ImportInformationResult> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateEntityHandler.class);
	
	@Inject
	private Injector injector;
	
	@Inject
	private FileStorageProvider storageProvider;

	@Override
	public ImportInformationResult execute(GetImportInformation command, UserDispatch.UserExecutionContext context) throws CommandException {
		ImportInformationResult result = null;
		
		if (command.getFileName() != null && command.getScheme() != null) {
			result = doImport(command.getFileName(), command.getScheme(), context);
			
		} else if(command.getFileName() != null && command.isCommit()) {
			try {
				storageProvider.delete(command.getFileName());
			} catch (IOException ex) {
				LOGGER.debug("An error occured while removing the imported file '" + command.getFileName() + "'.", ex);
			}
		}

		return result;
	}
	
	protected ImportInformationResult doImport(String fileName, ImportationSchemeDTO importationScheme, UserDispatch.UserExecutionContext context) throws CommandException {
		
		try (final InputStream inputStream = storageProvider.open(fileName)) {
			
			final Importer importer = Importers.createImporterForScheme(importationScheme);
			
			// Importer initialization.
			importer.setExecutionContext(context);
			importer.setInjector(injector);
			importer.setInputStream(inputStream);
			importer.initialize();

			return new ImportInformationResult(importer.getCorrespondances());
			
		} catch (IOException | IllegalStateException | UnsupportedOperationException e) {
			throw new CommandException("Error while importing file '" + fileName + "'.", e);
		}
	}
	
}
