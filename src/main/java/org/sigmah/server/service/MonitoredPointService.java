package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.Date;

import org.sigmah.server.dao.MonitoredPointDAO;
import org.sigmah.server.dao.MonitoredPointListDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.reminder.MonitoredPointHistory;
import org.sigmah.server.domain.reminder.MonitoredPointList;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link MonitoredPoint} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class MonitoredPointService extends AbstractEntityService<MonitoredPoint, Integer, MonitoredPointDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MonitoredPointService.class);

	@Inject
	private ProjectDAO projectDAO;

	@Inject
	private MonitoredPointDAO monitoredPointDAO;

	@Inject
	private MonitoredPointListDAO monitoredPointListDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitoredPoint create(final PropertyMap properties, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Starts monitored point creation.");
		}

		final User user = context.getUser();

		// Retrieves parameters.
		final Object expectedDate = properties.get(MonitoredPointDTO.EXPECTED_DATE);
		final Object label = properties.get(MonitoredPointDTO.LABEL);
		final Object projectId = properties.get(ReminderDTO.PROJECT_ID);

		if (!(projectId instanceof Integer)) {
			throw new CommandException("Invalid project id.");
		}

		// Retrieves project.
		LOG.debug("Retrieves the project #{}.", projectId);
		final Project project = projectDAO.findById((Integer) projectId);

		// Retrieves list.
		MonitoredPointList list = monitoredPointListDAO.findByProjectId(project.getId());

		// Creates the list if needed.
		if (list == null) {

			LOG.debug("The project #{} doesn't have a points list. Creates it.", project.getId());

			list = new MonitoredPointList();
			list.setPoints(new ArrayList<MonitoredPoint>());
			project.setPointsList(list);
		}

		// Creates point.
		final MonitoredPoint point = new MonitoredPoint();
		point.setLabel((String) label);
		point.setExpectedDate(new Date((Long) expectedDate));
		point.setCompletionDate(null);
		point.setFile(null);
		point.setDeleted(false);

		// Adds it to the list.
		list.addMonitoredPoint(point);

		final MonitoredPointHistory hist = new MonitoredPointHistory();
		hist.setDate(new Date());
		hist.setType(ReminderChangeType.CREATED);
		hist.setUserId(user.getId());
		hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
		point.addHistory(hist);

		// Saves it.
		monitoredPointListDAO.persist(list, user);
		projectDAO.persist(project, user);

		LOG.debug("Ends monitored point creation #{} in list #{}.", point.getId(), list.getId());

		return point;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitoredPoint update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) throws CommandException {

		final User user = context.getUser();
		final MonitoredPoint point = monitoredPointDAO.findById(entityId);

		if (point == null) {
			throw new CommandException("Cannot find MonitoredPoint with id #" + entityId + ".");
		}

		final boolean date_modified = !(new Date((Long) changes.get(MonitoredPointDTO.EXPECTED_DATE)).equals(point.getExpectedDate()));
		final boolean label_modified = !((String) changes.get(MonitoredPointDTO.LABEL)).equals(point.getLabel());

		// Update 3 properties: ExpectedDate,Label,Deleted
		point.setExpectedDate(new Date((Long) changes.get(MonitoredPointDTO.EXPECTED_DATE)));
		point.setLabel((String) changes.get(MonitoredPointDTO.LABEL));
		final Boolean deleted = (Boolean) changes.get(MonitoredPointDTO.DELETED);
		if (deleted != null) {
			point.setDeleted(deleted);
		}

		if (!point.getDeleted()) {

			if (date_modified) {
				final MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

			if (label_modified) {
				final MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

		} else {
			final MonitoredPointHistory hist = new MonitoredPointHistory();
			hist.setDate(new Date());
			hist.setType(ReminderChangeType.DELETED);
			hist.setUserId(user.getId());
			hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
			point.addHistory(hist);
		}

		return monitoredPointDAO.persist(point, user);
	}

}
