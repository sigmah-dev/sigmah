package org.sigmah.server.handler;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.OrgUnitBannerDAO;
import org.sigmah.server.dao.OrgUnitDetailsDAO;
import org.sigmah.server.dao.PhaseModelDAO;
import org.sigmah.server.dao.PrivacyGroupDAO;
import org.sigmah.server.dao.ProjectBannerDAO;
import org.sigmah.server.dao.ProjectDetailsDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnitBanner;
import org.sigmah.server.domain.OrgUnitDetails;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectBanner;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeletePrivacyGroups;
import org.sigmah.shared.command.result.DeleteResult;
import org.sigmah.shared.command.result.DeleteResult.DeleteErrorCause;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link DeletePrivacyGroups} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeletePrivacyGroupsHandler extends AbstractCommandHandler<DeletePrivacyGroups, DeleteResult<PrivacyGroupDTO>> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(DeleteFlexibleElementsHandler.class);

	/**
	 * Injected {@link PrivacyGroupDAO}.
	 */
	@Inject
	private PrivacyGroupDAO privacyGroupDAO;

	/**
	 * Injected {@link ProjectBannerDAO}.
	 */
	@Inject
	private ProjectBannerDAO projectBannerDAO;

	/**
	 * Injected {@link ProjectDetailsDAO}.
	 */
	@Inject
	private ProjectDetailsDAO projectDetailsDAO;

	/**
	 * Injected {@link PhaseModelDAO}.
	 */
	@Inject
	private PhaseModelDAO phaseModelDAO;

	/**
	 * Injected {@link OrgUnitBannerDAO}.
	 */
	@Inject
	private OrgUnitBannerDAO orgUnitBannerDAO;

	/**
	 * Injected {@link OrgUnitDetailsDAO}.
	 */
	@Inject
	private OrgUnitDetailsDAO orgUnitDetailsDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeleteResult<PrivacyGroupDTO> execute(final DeletePrivacyGroups cmd, final UserExecutionContext context) throws CommandException {

		final List<Integer> privacyGroupIds = cmd.getPrivacyGroupIds();

		// Result that may contain detected error(s).
		final DeleteResult<PrivacyGroupDTO> result = new DeleteResult<PrivacyGroupDTO>();

		if (CollectionUtils.isEmpty(privacyGroupIds)) {
			// Nothing to delete.
			return result;
		}

		performDelete(privacyGroupIds, context, result);

		return result;
	}

	/**
	 * Delete each given privacy group.
	 * 
	 * @param privacyGroupIds List of the privacy group ids to delete.
	 * @param context Execution context.
	 * @param result List of result objects.
	 */
	@Transactional
	protected void performDelete(final List<Integer> privacyGroupIds, final UserExecutionContext context, final DeleteResult<PrivacyGroupDTO> result) {
		// For each privacy group.
		for (final Integer privacyGroupId : privacyGroupIds) {

			// Valid privacy group ?
			if (privacyGroupId == null) {
				continue;
			}

			final PrivacyGroup privacyGroup = privacyGroupDAO.findById(privacyGroupId);
			if (privacyGroup == null) {
				continue;
			}

			// Process delete.
			deletePrivacyGroup(privacyGroup, context, result);
		}
	}

	/**
	 * Deletes the existing given {@code privacyGroup}.
	 * 
	 * @param privacyGroup
	 *          The privacy group to delete (never {@code null}).
	 * @param context
	 *          The execution context.
	 * @param result
	 *          The result that contains detected error(s).
	 */
	private void deletePrivacyGroup(final PrivacyGroup privacyGroup, final UserExecutionContext context, final DeleteResult<PrivacyGroupDTO> result) {

		final Integer privacyGroupId = privacyGroup.getId();
		final PrivacyGroupDTO privacyGroupDTO = mapper().map(privacyGroup, new PrivacyGroupDTO());
		boolean errorDetected = false;

		// --
		// Is there any flexible element(s) related to the privacy group(s) ?
		// --

		if (privacyGroupDAO.countRelatedFlexibleElements(privacyGroupId) > 0) {

			errorDetected = true;
			final List<FlexibleElement> elements = privacyGroupDAO.findRelatedFlexibleElements(privacyGroupId);

			for (final FlexibleElement element : elements) {
				// Handles the flexible element error.
				handleFlexibleElementError(privacyGroupDTO, element, result);
			}
		}

		// --
		// Is there any profile(s) related to the privacy group(s) ?
		// --

		if (privacyGroupDAO.countRelatedProfiles(privacyGroupId) > 0) {

			errorDetected = true;
			final List<Profile> profiles = privacyGroupDAO.findRelatedProfiles(privacyGroupId);

			for (final Profile profile : profiles) {
				result.addError(privacyGroupDTO, new DeleteErrorCause(profile.getName()));
			}
		}

		if (errorDetected) {
			return;
		}

		// --
		// No error detected ; privacy group can be deleted.
		// --

		LOG.debug("Deleting the following privacy group: {}", privacyGroup);
		privacyGroupDAO.remove(privacyGroup, context.getUser());
		result.addDeleted(privacyGroupDTO);
	}

	/**
	 * <p>
	 * Handles the detected flexible element referencing the privacy group.
	 * </p>
	 * <p>
	 * Retrieves the parent component referencing the given flexible {@code element} and adds an error to the
	 * {@code result}.
	 * </p>
	 * 
	 * @param privacyGroup
	 *          The referenced privacy group DTO.
	 * @param element
	 *          The flexible element.
	 * @param result
	 *          The result that contains detected error(s).
	 */
	private void handleFlexibleElementError(final PrivacyGroupDTO privacyGroup, final FlexibleElement element, final DeleteResult<PrivacyGroupDTO> result) {

		// --
		// Does the flexible element belong to a Project model component ?
		// --

		final ProjectModel projectModel;

		final PhaseModel phaseModel = phaseModelDAO.findFromFlexibleElement(element.getId());
		if (phaseModel != null) {
			// Flexible element referenced by phase model.
			projectModel = phaseModel.getParentProjectModel();

		} else {
			final ProjectDetails projectDetails = projectDetailsDAO.findFromFlexibleElement(element.getId());
			if (projectDetails != null) {
				// Flexible element referenced by project details.
				projectModel = projectDetails.getProjectModel();

			} else {
				final ProjectBanner projectBanner = projectBannerDAO.findFromFlexibleElement(element.getId());
				if (projectBanner != null) {
					// Flexible element referenced by project banner.
					projectModel = projectBanner.getProjectModel();

				} else {
					projectModel = null;
				}
			}
		}

		if (projectModel != null) {
			result.addError(privacyGroup, new DeleteErrorCause(element.getLabel(), projectModel.getName(), element instanceof DefaultFlexibleElement));
			return;
		}

		// --
		// Does the flexible element belong to an OrgUnit model component ?
		// --

		final OrgUnitModel orgUnitModel;

		final OrgUnitDetails orgUnitDetails = orgUnitDetailsDAO.findFromFlexibleElement(element.getId());
		if (orgUnitDetails != null) {
			// Flexible element referenced by orgUnit details.
			orgUnitModel = orgUnitDetails.getOrgUnitModel();

		} else {
			final OrgUnitBanner orgUnitBanner = orgUnitBannerDAO.findFromFlexibleElement(element.getId());
			if (orgUnitBanner != null) {
				// Flexible element referenced by orgUnit banner.
				orgUnitModel = orgUnitBanner.getOrgUnitModel();

			} else {
				orgUnitModel = null;
			}
		}

		if (orgUnitModel != null) {
			result.addError(privacyGroup, new DeleteErrorCause(element.getLabel(), orgUnitModel.getName(), element instanceof DefaultFlexibleElement));
			return;
		}

		// No parent model has been found (should never happen).
		result.addError(privacyGroup, new DeleteErrorCause(element.getLabel(), "Unknown model", element instanceof DefaultFlexibleElement));
	}

}
