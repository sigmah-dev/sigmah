package org.sigmah.server.handler;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.IOException;
import java.io.InputStream;
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
		
		try (final InputStream inputStream = storageProvider.open(command.getFileId())) {
			final Importer importer = Importers.createImporterForScheme(command.getScheme());
			importer.setExecutionContext(context);
			importer.setInjector(injector);
			importer.initialize();

			importer.setInputStream(inputStream);

			final AutomatedImporter automatedImporter = new AutomatedImporter(importer);
			automatedImporter.importCorrespondances(command);
			
		} catch (IOException ex) {
			throw new CommandException("Error while importing file '" + command.getFileName() + "'.", ex);
		}
		
		return null;
	}
	
}
