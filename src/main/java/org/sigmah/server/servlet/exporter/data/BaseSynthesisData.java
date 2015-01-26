package org.sigmah.server.servlet.exporter.data;

import javax.persistence.EntityManager;

import org.sigmah.server.dispatch.CommandHandler;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.GetValueHandler;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.shared.command.GetValue;

import com.google.inject.Injector;

/*
 * Base synthesis data for project and org unit synthesis exports
 * @author sherzod
 */
public abstract class BaseSynthesisData extends ExportData {

	protected final EntityManager entityManager;
	private final GlobalExportDataProvider dataProvider;
	private final CommandHandler<GetValue, ?> handler;

	/*
	 * private final Locale locale; private final Translator translator;
	 */
	public BaseSynthesisData(final Exporter exporter, final Injector injector) {
		super(exporter, 3);
		entityManager = injector.getInstance(EntityManager.class);
		dataProvider = injector.getInstance(GlobalExportDataProvider.class);
		handler = injector.getInstance(GetValueHandler.class);
		/*
		 * this.locale = locale; translator = new UIConstantsTranslator(new Locale(""));
		 */
	}

	public GlobalExportDataProvider getDataProvider() {
		return dataProvider;
	}

	public CommandHandler<GetValue, ?> getHandler() {
		return handler;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	/*
	 * public Locale getLocale() { return locale; } public Translator getTranslator() { return translator; }
	 */
	public abstract Project getProject();

	public abstract OrgUnit getOrgUnit();

}
