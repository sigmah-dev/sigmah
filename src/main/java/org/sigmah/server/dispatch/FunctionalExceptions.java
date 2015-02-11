package org.sigmah.server.dispatch;

import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.i18n.I18nServerImpl;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.FunctionalException;

/**
 * Utility methods for FunctionalException.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FunctionalExceptions {
	/**
	 * Server implementation of I18N.
	 */
	private static I18nServer i18n;
	
	/**
	 * Returns the functional exception message from server side. <br/>
	 * Method {@link FunctionalException#getParameter(int)} can be used to populate message dynamic parameters.
	 * 
	 * @param language 
	 *			Language of the connected user.
	 * @param exception
	 *			The functional exception that may embed message parameter(s).
	 * @return the functional exception message.
	 */
	public static String getMessage(final Language language, final FunctionalException exception) {
		if(i18n == null) {
			i18n = new I18nServerImpl();
		}
		
		final FunctionalException.ErrorCode errorCode = exception.getErrorCode();
		switch (errorCode) {

			case AUTHENTICATION_FAILURE:
				return i18n.t(language, "loginConnectErrorBadLogin");

			case ADMIN_MOVE_ORG_UNIT_ITSELF_AS_PARENT:
				return i18n.t(language, "adminOrgUnitMoveErrorItself");

			case ADMIN_MOVE_ORG_UNIT_CYCLE_DETECTED:
				return i18n.t(language, "adminOrgUnitMoveErrorCycle");

			case ADMIN_REMOVE_ORG_UNIT_HAS_CHILDREN:
				return i18n.t(language, "adminOrgUnitRemoveHasChildren");

			case ADMIN_REMOVE_ORG_UNIT_HAS_PROJECTS:
				return i18n.t(language, "adminOrgUnitRemoveHasProjects");

			case ADMIN_REMOVE_ORG_UNIT_IS_ROOT:
				return i18n.t(language, "adminOrgUnitRemoveIsRoot");

			case ADMIN_USER_DUPLICATE_EMAIL:
				return i18n.t(language, "existingEmailAddress", exception.getParameter(0));

			case PROJECT_IS_LOCKED_AMENDABLE_FIELD_IS_READONLY:
				return i18n.t(language, "projectIsLockedAmendableFieldReadOnly", exception.getParameter(0), exception.getParameter(1), exception.getParameter(2));
				
			case IMPORTATION_SCHEME_IS_LINKED:
				return i18n.t(language, "adminImportationSchemesWarnModelsLinked", exception.getParameter(0));
				
			case IMPORT_INVALID_COLUMN_REFERENCE:
				return i18n.t(language, "importInvalidColumnReference", exception.getParameter(0));
				
			default:
				return errorCode.toString();
		}
	}
}
