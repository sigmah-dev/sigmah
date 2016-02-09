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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.servlet.importer.AutomatedImporter;
import org.sigmah.server.servlet.importer.Importer;
import org.sigmah.server.servlet.importer.Importers;
import org.sigmah.shared.command.AutomatedImport;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link AutomatedImport}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class AutomatedImportHandler extends AbstractCommandHandler<AutomatedImport, ListResult<BaseModelData>> {

	@Inject
	private Injector injector;
	
	@Inject
	private FileStorageProvider storageProvider;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListResult<BaseModelData> execute(AutomatedImport command, UserDispatch.UserExecutionContext context) throws CommandException {
		
		List<BaseModelData> correspondances = Collections.<BaseModelData>emptyList();
		
		try (final InputStream inputStream = storageProvider.open(command.getFileId())) {
			final Importer importer = Importers.createImporterForScheme(command.getScheme());
			importer.setExecutionContext(context);
			importer.setInjector(injector);
			importer.initialize();

			importer.setInputStream(inputStream);

			final AutomatedImporter automatedImporter = new AutomatedImporter(importer);
			correspondances = automatedImporter.importCorrespondances(command);
			
		} catch (IOException ex) {
			throw new CommandException("Error while importing file '" + command.getFileName() + "'.", ex);
		}
		
		return new ListResult<>(correspondances);
	}
	
}
