package org.sigmah.shared.dispatch;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.ToStringBuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <p>
 * Functional command exception.
 * </p>
 * <p>
 * Exception thrown by services when a <em>functional</em> issue is detected and an appropriate message should be
 * displayed.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FunctionalException extends CommandException {

	/**
	 * The functional error codes with corresponding {@code i18n} messages.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static enum ErrorCode implements IsSerializable {

		/**
		 * Authentication failure.<br>
		 * No parameters.
		 */
		AUTHENTICATION_FAILURE,

		// --
		// Admin - OrgUnits.
		// --

		/**
		 * Cannot reference an OrgUnit's parent as itself.<br>
		 * No parameters.
		 */
		ADMIN_MOVE_ORG_UNIT_ITSELF_AS_PARENT,

		/**
		 * Cycle detected while moving an OrgUnit within its hierarchy.<br>
		 * No parameters.
		 */
		ADMIN_MOVE_ORG_UNIT_CYCLE_DETECTED,

		/**
		 * Cannot remove an OrgUnit with children.<br>
		 * No parameters.
		 */
		ADMIN_REMOVE_ORG_UNIT_HAS_CHILDREN,

		/**
		 * Cannot remove an OrgUnit with related projects.<br>
		 * No parameters.
		 */
		ADMIN_REMOVE_ORG_UNIT_HAS_PROJECTS,

		/**
		 * Cannot remove a <b>root</b> OrgUnit.<br>
		 * No parameters.
		 */
		ADMIN_REMOVE_ORG_UNIT_IS_ROOT,

		/**
		 * Cannot create a user with an existing email.<br>
		 * {0} = existing email.
		 */
		ADMIN_USER_DUPLICATE_EMAIL,

		;
	}

	/**
	 * Returns the functional exception message.<br/>
	 * Method {@link FunctionalException#getParameter(int)} can be used to populate message dynamic parameters.
	 * 
	 * @param exception
	 *          The functional exception that may embed message parameter(s).
	 * @param errorCode
	 *          Functional error code (cannot be {@code null}).
	 * @return the functional exception message.
	 */
	private static String getMessage(final FunctionalException exception, final ErrorCode errorCode) {
		switch (errorCode) {

			case AUTHENTICATION_FAILURE:
				return I18N.CONSTANTS.loginConnectErrorBadLogin();

			case ADMIN_MOVE_ORG_UNIT_ITSELF_AS_PARENT:
				return I18N.CONSTANTS.adminOrgUnitMoveErrorItself();

			case ADMIN_MOVE_ORG_UNIT_CYCLE_DETECTED:
				return I18N.CONSTANTS.adminOrgUnitMoveErrorCycle();

			case ADMIN_REMOVE_ORG_UNIT_HAS_CHILDREN:
				return I18N.CONSTANTS.adminOrgUnitRemoveHasChildren();

			case ADMIN_REMOVE_ORG_UNIT_HAS_PROJECTS:
				return I18N.CONSTANTS.adminOrgUnitRemoveHasProjects();

			case ADMIN_REMOVE_ORG_UNIT_IS_ROOT:
				return I18N.CONSTANTS.adminOrgUnitRemoveIsRoot();

			case ADMIN_USER_DUPLICATE_EMAIL:
				return I18N.MESSAGES.existingEmailAddress(exception.getParameter(0));

			default:
				return errorCode.toString();
		}
	}

	/**
	 * Returns the functional exception title.<br/>
	 * Method {@link FunctionalException#getParameter(int)} can be used to populate title dynamic parameters.
	 * 
	 * @param exception
	 *          The functional exception that may embed title parameter(s)..
	 * @param errorCode
	 *          Functional error code (cannot be {@code null}).
	 * @return the functional exception title, or {@code null} to use the default title.
	 */
	private static String getTitle(final FunctionalException exception, final ErrorCode errorCode) {
		switch (errorCode) {

			case ADMIN_MOVE_ORG_UNIT_ITSELF_AS_PARENT:
			case ADMIN_MOVE_ORG_UNIT_CYCLE_DETECTED:
				return I18N.CONSTANTS.adminOrgUnitMoveFailed();

			case ADMIN_REMOVE_ORG_UNIT_HAS_CHILDREN:
			case ADMIN_REMOVE_ORG_UNIT_HAS_PROJECTS:
			case ADMIN_REMOVE_ORG_UNIT_IS_ROOT:
				return I18N.CONSTANTS.adminOrgUnitRemoveUnavailable();

			default:
				return null; // Default title.
		}
	}

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -324930495914540987L;

	/**
	 * This exception {@code errorType}. Cannot be {@code null} (assertion).
	 * 
	 * @see ErrorCode
	 */
	private ErrorCode errorCode;

	/**
	 * The error message relative parameters. Can be {@code null}.<br/>
	 * They are used to populate dynamic message parameters (be careful with order).
	 */
	private String[] parameters;

	@SuppressWarnings("unused")
	@Deprecated
	private FunctionalException() {
		// Serialization.
	}

	/**
	 * Builds a {@link FunctionalException} according to the given message-relative {@link ErrorCode}.
	 * 
	 * @param errorCode
	 *          See {@link #errorCode}.
	 * @param parameters
	 *          See {@link #parameters}.
	 */
	public FunctionalException(ErrorCode errorCode, String... parameters) {
		assert errorCode != null : "Error code is required.";
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("Exception", "FunctionalException");
		builder.append("Error code", errorCode);
		return builder.toString();
	}

	/**
	 * Returns the error message parameter specified at given {@code index}.
	 * 
	 * @param index
	 *          The parameter index.
	 * @return the error message parameter specified at given {@code index}. Returns {@code null} if no parameter has been
	 *         provided or {@code index} is out of bounds.
	 * @see #parameters
	 */
	protected final String getParameter(int index) {
		if (parameters == null || index >= parameters.length || index < 0) {
			return null;
		}
		return parameters[index];
	}

	/**
	 * Returns the number of parameters set into the current exception.
	 * 
	 * @return the number of parameters set into the current exception.
	 */
	protected final int getParametersNumber() {
		return parameters != null ? parameters.length : 0;
	}

	/**
	 * Returns the current functional exception corresponding {@link ErrorCode}.
	 * 
	 * @return The {@link ErrorCode} (never {@code null}).
	 */
	public final ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * Returns the functional error corresponding title (if any).
	 * 
	 * @return The functional error corresponding title (if any), or {@code null}.
	 */
	public final String getTitle() {
		if (!GWT.isClient()) {
			return null;
		}
		return getTitle(this, errorCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getMessage() {
		if (!GWT.isClient()) {
			return toString(); // Displays exception class name and error code.
		}
		return getMessage(this, errorCode);
	}

	/**
	 * Verify if the actual error is of the given type
	 * 
	 * @param errorCode
	 *          Functional error code.
	 * @return result of the comparison
	 */
	public final boolean is(ErrorCode errorCode) {
		return this.errorCode == errorCode;
	}

}
