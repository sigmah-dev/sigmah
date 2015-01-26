package org.sigmah.client.event;

import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.presenter.CreateProjectPresenter.Mode;
import org.sigmah.client.ui.presenter.reminder.ReminderType;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.report.ReportReference;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fire when an element is updated.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class UpdateEvent extends GwtEvent<UpdateHandler> {

	// ---------------------------------------------------------------------------------
	//
	// Update keys.
	//
	// ---------------------------------------------------------------------------------

	/**
	 * <p>
	 * When the viewport size is udpated.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String VIEWPORT_SIZE_UPDATE = "VIEWPORT_SIZE_UPDATE";

	/**
	 * <p>
	 * When the project banner should be udpated.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String PROJECT_BANNER_UPDATE = "PROJECT_BANNER_UPDATE";

	/**
	 * When a project is created.
	 * <ul>
	 * <li>params[0] = current creation {@link Mode mode}</li>
	 * <li>params[1] = the created {@link ProjectDTO project}</li>
	 * <p>
	 * If the creation mode is {@link Mode#FUNDED_BY_ANOTHER_PROJECT}
	 * </p>
	 * <ul>
	 * <li>params[2] = the funded {@link Double amount}</li>
	 * </ul>
	 * <p>
	 * If the creation mode is {@link Mode#FUNDING_ANOTHER_PROJECT}
	 * </p>
	 * <ul>
	 * <li>params[2] = the funding {@link Double amount}</li>
	 * </ul>
	 * </p> </ul>
	 */
	public static final String PROJECT_CREATE = "PROJECT_CREATE";

	/**
	 * <p>
	 * When a project is deleted.
	 * </p>
	 * <p>
	 * <li>params[0] = The current {@link PageRequest} at the time of the delete action.</li>
	 * </p>
	 */
	public static final String PROJECT_DELETE = "PROJECT_DELETE";

	/**
	 * <p>
	 * When a <b>draft</b> project is deleted.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>params[0] = deleted draft {@link ProjectDTO project}.</li>
	 * </ul>
	 * </p>
	 */
	public static final String PROJECT_DRAFT_DELETE = "PROJECT_DRAFT_DELETE";

	/**
	 * <p>
	 * When a reminder/monitored point is deleted or udpated.
	 * </p>
	 * <p>
	 * param[0] = The {@link ReminderType} value.
	 * </p>
	 */
	public static final String REMINDER_UPDATED = "REMINDER_UPDATED";

	/**
	 * <p>
	 * When a linked project (funding/funded) is created or udpated.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The {@link LinkedProjectType}.</li>
	 * <li>param[1] = The {@link ProjectFundingDTO} created/updated value.</li>
	 * </ul>
	 * </p>
	 */
	public static final String LINKED_PROJECT_UPDATE = "LINKED_PROJECT_UPDATE";

	/**
	 * <p>
	 * When a linked project (funding/funded) is deleted.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The {@link LinkedProjectType}.</li>
	 * <li>param[1] = The {@link ProjectFundingDTO} deleted value.</li>
	 * </ul>
	 * </p>
	 */
	public static final String LINKED_PROJECT_DELETE = "LINKED_PROJECT_DELETE";

	/**
	 * <p>
	 * When a calendar event is created/udpated.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The {@link org.sigmah.shared.dto.calendar.Event}.</li>
	 * </ul>
	 * </p>
	 */
	public static final String CALENDAR_EVENT_UPDATE = "CALENDAR_EVENT_UPDATE";

	/**
	 * <p>
	 * When a new report has been created or a file has been attached to a Project/OrgUnit.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The {@link ReportReference} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String REPORT_DOCUMENTS_UPDATE = "REPORT_DOCUMENTS_UPDATE";

	/**
	 * <p>
	 * When a privacy group has been created or a updated.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String PRIVACY_GROUP_UPDATE = "PRIVACY_GROUP_UPDATE";

	/**
	 * <p>
	 * When a profile has been created or a updated.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String PROFILE_UPDATE = "PROFILE_UPDATE";

	/**
	 * <p>
	 * When a user has been created or a updated.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String USER_UPDATE = "USER_UPDATE";

	/**
	 * <p>
	 * When a OrgUnit has been created or moved.
	 * </p>
	 * <p>
	 * No parameter is provided with this event.
	 * </p>
	 */
	public static final String ORG_UNIT_UPDATE = "ORG_UNIT_UPDATE";

	/**
	 * <p>
	 * When a {@code OrgUnitModel} has been created.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The created {@link OrgUnitModelDTO} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String ORG_UNIT_MODEL_ADD = "ORG_UNIT_MODEL_ADD";

	/**
	 * <p>
	 * When a {@code ProjectModel} has been created.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The created {@link ProjectModelDTO} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String PROJECT_MODEL_ADD = "PROJECT_MODEL_ADD";

	/**
	 * <p>
	 * When a {@code PhaseModel} has been created/updated.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The updated parent {@link ProjectModelDTO} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String PHASE_MODEL_UPDATE = "PHASE_MODEL_UPDATE";

	/**
	 * <p>
	 * When a {@code LayoutGroup} has been created/updated.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The updated parent {@link LayoutGroupDTO} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String LAYOUT_GROUP_UPDATE = "LAYOUT_GROUP_UPDATE";

	/**
	 * <p>
	 * When a {@code FlexbibleElement} has been created/updated.
	 * </p>
	 * <p>
	 * <ul>
	 * <li>param[0] = The updated parent {@link IsModel} instance (project or org unit).</li>
	 * <li>param[1] = Boolean flag set to {@code true} in case of flexible element update, or set to {@code false} in case
	 * of flexible element creation.</li>
	 * <li>param[2] = The created/updated {@link FlexibleElementDTO} instance.</li>
	 * </ul>
	 * </p>
	 */
	public static final String FLEXIBLE_ELEMENT_UPDATE = "FLEXIBLE_ELEMENT_UPDATE";

	/**
	 * After Importing category model
	 */
	public static final String CATEGORY_MODEL_IMPORT = "CATEGORY_MODEL_IMPORT";

	/**
	 * After Importing Report model
	 */
	public static final String REPORT_MODEL_IMPORT = "REPORT_MODEL_IMPORT";

	/**
	 * After Importing Project model
	 */
	public static final String PROJECT_MODEL_IMPORT = "PROJECT_MODEL_IMPORT";

	/**
	 * After Importing OrgUnit model
	 */
	public static final String ORG_UNIT_MODEL_IMPORT = "ORG_UNIT_MODEL_IMPORT";

	/**
	 * <p>
	 * When an {@code IndicatorDTO} has been created or updated.
	 * </p>
	 * <ul>
	 * <li>param[0] = Identifier of the updated indicator.</li>
	 * <li>param[1] = Changes mades to the indicator.</li>
	 * </ul>
	 */
	public static final String INDICATOR_UPDATED = "INDICATOR_UPDATED";

	/**
	 * <p>
	 * When an {@code IndicatorDTO} has been deleted.
	 * </p>
	 * <ul>
	 * <li>param[0] = Identifier of the deleted indicator.</li>
	 * </ul>
	 */
	public static final String INDICATOR_REMOVED = "INDICATOR_REMOVED";

	/**
	 * when adding or updating sub Budget field in flexible element
	 */
	public static final String EDIT_FLEXIBLEELEMNT_EDIT_BUDGETSUBFIELD = "EDIT_FLEXIBLEELEMNT_EDIT_BUDGETSUBFIELD";

	/**
	 * when rename amendment
	 */

	public static final String AMENDMENT_RENAME = "AMENDMENT_RENAME";

	/**
	 * After add or edit imporation scheme to update liste of Importation Schemes
	 */

	public static final String UPDATE_LISTE_IMPORTATION_SCHEME = "UPDATE_LISTE_IMPORTATION_SCHEME";

	/**
	 * After add or edit variable imprtation scheme
	 */
	public static final String UPDATE_LISTE_VARIABLE_SCHEME = "UPDATE_LISTE_VARIABLE_SCHEME";

	// ---------------------------------------------------------------------------------
	//
	// UpdateEvent Implementation.
	//
	// ---------------------------------------------------------------------------------
	private static Type<UpdateHandler> TYPE;

	public static Type<UpdateHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<UpdateHandler>();
		}
		return TYPE;
	}

	/**
	 * Updated element name.
	 */
	private final String elementName;

	/**
	 * Update event optional parameters.
	 */
	private final Object[] params;

	/**
	 * Initializes a new {@code UpdateEvent}.
	 * 
	 * @param elementName
	 *          The updated element name.
	 * @param params
	 *          The optional event parameters.
	 */
	public UpdateEvent(final String elementName, final Object... params) {
		this.elementName = elementName;
		this.params = params;
	}

	public String getEntityName() {
		return elementName;
	}

	/**
	 * Returns the given {@code index} corresponding parameter value.
	 * 
	 * @param <T>
	 *          The parameter value type.
	 * @param index
	 *          The parameter index.
	 * @return The given {@code index} corresponding parameter value, or {@code null} if no parameter exist for the given
	 *         {@code index} or if it cannot be cast into the provided type.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParam(final int index) {

		T param = null;

		if (params != null && params.length >= index + 1) {

			try {

				param = (T) params[index];

			} catch (final ClassCastException e) {
				param = null;
			}
		}

		return param;

	}

	/**
	 * Returns if the current event concerns the given {@code elementName}.
	 * 
	 * @param elementName
	 *          The event parameter name.
	 * @return {@code true} if the current event concerns the given {@code elementName}, {@code false} otherwise.
	 */
	public boolean concern(final String elementName) {
		return this.elementName.equals(elementName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<UpdateHandler> getAssociatedType() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(UpdateHandler handler) {
		handler.onUpdate(this);
	}

}
