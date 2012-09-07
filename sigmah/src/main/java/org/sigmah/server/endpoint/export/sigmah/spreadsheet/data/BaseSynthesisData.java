/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.Locale;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.sigmah.server.Translator;
import org.sigmah.server.UIConstantsTranslator;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider;
import org.sigmah.server.endpoint.gwtrpc.handler.GetValueHandler;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;

import com.google.inject.Injector;

/*
 * Base synthesis data for project and org unit synthesis exports
 * @author sherzod
 */
public abstract class BaseSynthesisData extends ExportData{
	 
	 protected final EntityManager entityManager;
	 private final GlobalExportDataProvider dataProvider;
	 private final CommandHandler<GetValue> handler;
	 private final Locale locale;
	 private final Translator translator;
	 
	public BaseSynthesisData(			
			final Exporter exporter,
			final Injector injector,
			final Locale locale) {
		super(exporter, 3);		 
		entityManager = injector.getInstance(EntityManager.class);
		dataProvider=injector.getInstance(GlobalExportDataProvider.class);
		handler=new GetValueHandler(entityManager,
				injector.getInstance(Mapper.class));
		this.locale=locale;
		translator= new UIConstantsTranslator(new Locale(""));
			
  	}

	public GlobalExportDataProvider getDataProvider() {
		return dataProvider;
	}
		 
	public CommandHandler<GetValue> getHandler() {
		return handler;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Locale getLocale() {
		return locale;
	}

	public Translator getTranslator() {
		return translator;
	}
	
	public abstract Project getProject();
	public abstract OrgUnit getOrgUnit();

}