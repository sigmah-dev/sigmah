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
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mail.Email;
import org.sigmah.server.mail.EmailAttachment;
import org.sigmah.server.mail.MailSender;
import org.sigmah.shared.command.SendProbeReport;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.profile.ExecutionDTO;
import org.sigmah.shared.dto.profile.ProbesReportDetails;
import org.sigmah.shared.dto.profile.ScenarioDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for <code>SendProbeReport</code> command.
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
@Singleton
public class SendProbeReportHandler extends AbstractCommandHandler<SendProbeReport, Result> {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SendProbeReportHandler.class);

	/**
	 * End of line.
	 */
	final String LINE_SEPARATOR = System.getProperty("line.separator");

	/**
	 * Injected application properties.
	 */
	@Inject
	private Properties properties;

	/**
	 * Injected application properties.
	 */
	@Inject
	private MailSender sender;

	/**
	 * Gson instance, used to transform java objects to json.
	 */
	private final Gson gson = new Gson();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Result execute(final SendProbeReport command, final UserDispatch.UserExecutionContext context) throws CommandException {

		if (command.getExecutionsProfiler() != null) {
			final ProbesReportDetails probesReportDetails = buildProbesReportDetails(command.getExecutionsProfiler());
			final Email email = buildEmail();
			try {
				final EmailAttachment markdownReport = new EmailAttachment(properties.getProperty(PropertyKey.MAIL_OPTIMISATION_MARKDOWN_FILE_NAME), buildMarkDownFile(probesReportDetails));
				final EmailAttachment rawReport = new EmailAttachment(properties.getProperty(PropertyKey.MAIL_OPTIMISATION_JSON_FILE_NAME), buildRawReportFile(command.getExecutionsProfiler()));

				sender.sendWithAttachments(email, markdownReport, rawReport);
			} catch (EmailException e) {
				LOGGER.error("An error occured while sending the probe report.", e);
			}
		}
		return null;
	}

	/**
	 * Build the raw json report from the execution list.
	 *
	 * @param executions 
	 *			Object to be serialized.
	 * @return The content of the raw report.
	 */
	public byte[] buildRawReportFile(final List<ExecutionDTO> executions) {
		final String stringReport = gson.toJson(executions);
		return stringReport.getBytes();
	}

	/**
	 * Build the mark down report.
	 *
	 * @param probesReportDetails
	 *			Detail of the probe session to report.
	 * @return The content of the main report.
	 */
	private byte[] buildMarkDownFile(final ProbesReportDetails probesReportDetails) {

		final StringBuilder sb = new StringBuilder();
		sb.append("## Performance Report").append(LINE_SEPARATOR);
		sb.append(" * Sigmah version	: ").append(probesReportDetails.getVersionNumber()).append(LINE_SEPARATOR);
		sb.append(" * Start time		: ").append(probesReportDetails.getStartTime()).append(LINE_SEPARATOR);
		sb.append(" *  End time			: ").append(probesReportDetails.getEndTime()).append(LINE_SEPARATOR);
		sb.append(" * User Agent		: ").append(probesReportDetails.getUserAgent()).append(LINE_SEPARATOR);

		sb.append(LINE_SEPARATOR);
		sb.append("### Scenarios execution time ").append(LINE_SEPARATOR);
		sb.append(" | **Scenario**  | **Min**   | **Max** | **Average** | ").append(LINE_SEPARATOR);
		for (ScenarioDetailsDTO scenario : probesReportDetails.getSenarios()) {
			sb.append(" | ").append(scenario.getScenario().name()).append(" | ").append(String.valueOf(scenario.getMinDuartion())).append(" | ").append(String.valueOf(scenario.getMaxDuration())).append(" | ").append(String.valueOf(scenario.getAvrageDuration())).append(" | ").append(LINE_SEPARATOR);
		}
		return sb.toString().getBytes();
	}

	/**
	 * Build the report model.
	 *
	 * @param executions
	 *			List of executions.
	 * @return A summary of the probing session.
	 */
	private ProbesReportDetails buildProbesReportDetails(final List<ExecutionDTO> executions) {

		final ProbesReportDetails probesReportDetails = new ProbesReportDetails();
		Date startTime = null;
		Date endDateTime = null;

		final Map<Scenario, List<ExecutionDTO>> scenarioExecutionMap = new HashMap<Scenario, List<ExecutionDTO>>();

		for (ExecutionDTO execution : executions) {
			// build map (senarion, list of execution)
			if (scenarioExecutionMap.containsKey(execution.getScenario())) {
				scenarioExecutionMap.get(execution.getScenario()).add(execution);
			} else {
				final List<ExecutionDTO> newList = new ArrayList<ExecutionDTO>();
				newList.add(execution);
				scenarioExecutionMap.put(execution.getScenario(), newList);
			}
			//initialize common parameters
			if (startTime == null || startTime.after(execution.getDate())) {
				startTime = execution.getDate();
			}
			if (endDateTime == null || endDateTime.before(execution.getDate())) {
				endDateTime = execution.getDate();
			}
			if (probesReportDetails.getUserAgent() == null) {
				probesReportDetails.setUserAgent(execution.getUserAgent());
			}
			if (probesReportDetails.getVersionNumber() == null) {
				probesReportDetails.setVersionNumber(execution.getVersionNumber());
			}
		}
		probesReportDetails.setStartTime(startTime);
		probesReportDetails.setEndTime(endDateTime);

		for (Entry<Scenario, List<ExecutionDTO>> entry : scenarioExecutionMap.entrySet()) {
			probesReportDetails.getSenarios().add(calculateSenarioDetails(entry.getKey(), entry.getValue()));
		}
		return probesReportDetails;
	}

	/**
	 * Calculate scenario.
	 *
	 * @param scenarioDetailsDTO
	 *			Scenario details.
	 * @param executions List execution of scenario.
	 */
	private ScenarioDetailsDTO calculateSenarioDetails(final Scenario scenario, final List<ExecutionDTO> executions) {
		
		double minDuration = 0;
		double maxDuration = -1;
		double sumDuration = 0;
		
		Date startTime = null;
		Date endDateTime = null;
		
		final ScenarioDetailsDTO scenarioDetails = new ScenarioDetailsDTO();
		scenarioDetails.setScenario(scenario);
		
		for (ExecutionDTO execution : executions) {
			if (execution.getDuration() < minDuration || minDuration == 0) {
				minDuration = execution.getDuration();
			}
			if (execution.getDuration() > maxDuration) {
				maxDuration = execution.getDuration();
			}
			sumDuration += execution.getDuration();
			if (startTime == null || startTime.after(execution.getDate())) {
				startTime = execution.getDate();
			}
			if (endDateTime == null || endDateTime.before(execution.getDate())) {
				endDateTime = execution.getDate();
			}
		}
		
		scenarioDetails.setMaxDuration(maxDuration);
		scenarioDetails.setMinDuartion(minDuration);
		scenarioDetails.setAvrageDuration(executions.size() > 0 ? sumDuration / executions.size() : -1);
		scenarioDetails.setEndTime(endDateTime);
		scenarioDetails.setStartTime(startTime);
		
		return scenarioDetails;
	}

	/**
	 * Build Email object.
	 *
	 * @return A new email object.
	 */
	private Email buildEmail() {
		
		final Email email = new Email();
		email.setFromAddress(properties.getProperty(PropertyKey.MAIL_FROM_ADDRESS));
		email.setFromName(properties.getProperty(PropertyKey.MAIL_FROM_NAME));
		String toAdresse = properties.getProperty(PropertyKey.MAIL_OPTIMISATION_TO_ADDRESS);
		email.setToAddresses(!StringUtils.isBlank(toAdresse) ? toAdresse.split(";") : new String[0]);
		String copyAdresse = properties.getProperty(PropertyKey.MAIL_OPTIMISATION_COPY_ADDRESS);
		email.setCcAddresses(!StringUtils.isBlank(copyAdresse) ? copyAdresse.split(";") : new String[0]);

		email.setContentType(properties.getProperty(PropertyKey.MAIL_CONTENT_TYPE));
		email.setHostName(properties.getProperty(PropertyKey.MAIL_HOSTNAME));
		email.setSmtpPort(properties.getIntegerProperty(PropertyKey.MAIL_PORT));
		email.setEncoding(properties.getProperty(PropertyKey.MAIL_ENCODING));

		email.setSubject("Optimisation message");
		email.setContent("");
		email.setAuthenticationUserName(properties.getProperty(PropertyKey.MAIL_AUTH_USERNAME));
		email.setAuthenticationPassword(properties.getProperty(PropertyKey.MAIL_AUTH_PASSWORD));

		return email;
	}

}
