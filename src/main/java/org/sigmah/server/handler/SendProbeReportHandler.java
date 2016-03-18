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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
@Singleton
public class SendProbeReportHandler extends AbstractCommandHandler<SendProbeReport, Result>{


	/**
	 * End of line.
	 */
	final String LINE_SEPARATOR=System.getProperty("line.separator");
	/**
	 * Markdow report file name.
	 */
	//final String OPTIMISATION_MARKDOWNFILE_NAME="sigmah_performance_report.txt";
	/**
	 * json report file name.
	 */
	//final String OPTIMISATION_BRUT_REPORT_NAME="sigmah_performance_brut_report.txt";
	/**
	 * Injected application properties.
	 */
	@Inject
	private  Properties properties;
	
	/**
	 * Injected application properties.
	 */
	@Inject
	private  MailSender sender;
	/**
	 * gson used to transform javaobject to json.
	 */
	private Gson gson =new Gson();	
	
	private static final Logger LOG = LoggerFactory.getLogger(SendProbeReportHandler.class);
	@Override
	protected Result execute(SendProbeReport command, UserDispatch.UserExecutionContext context) throws CommandException {
		LOG.info("###########################SendProbeReportHandler start "); 		
		if(command.getExecutionsProfiler()!=null){			
			ProbesReportDetails probesReportDetails = buildProbesReportDetails(command.getExecutionsProfiler());
			log(probesReportDetails);			
			Email email=buildEmail();	
			try{
				//properties.getProperty(PropertyKey.MAIL_OPTIMISATION_MARKDOWNFILE);
				String[] filesName={properties.getProperty(PropertyKey.MAIL_OPTIMISATION_MARKDOWN_FILE_NAME),properties.getProperty(PropertyKey.MAIL_OPTIMISATION_JSON_FILE_NAME)};
				InputStream[] streams={buildMarkDownFile(probesReportDetails),buildBrutReportFile(command.getExecutionsProfiler())};
				sender.sendEmailWithMultiAttachmenets(email,filesName,streams);
			}catch(EmailException e){
				LOG.error("Error", e);
			}
		}
		LOG.info("###########################SendProbeReportHandler end "); 
		return null;
	}
	/**
	 * Build json from java object.
	 * @param executionDtoList object to be serialized
	 * @return InputStream
	 */
	public InputStream buildBrutReportFile(List<ExecutionDTO> executionDtoList){
		String stringReport=gson.toJson(executionDtoList);
		return new ByteArrayInputStream(stringReport.getBytes());
	}
	/**
	 * Build mark down file from probesReportDetails.
	 * @param probesReportDetails
	 * @return 
	 */
	private InputStream  buildMarkDownFile(ProbesReportDetails probesReportDetails){
		
		StringBuilder sb=new StringBuilder();
		sb.append("## Performance Report").append(LINE_SEPARATOR);
		sb.append(" * Sigmah version	: ").append(probesReportDetails.getVersionNumber()).append(LINE_SEPARATOR);		
		sb.append(" * Start time		: ").append(probesReportDetails.getStartTime()).append(LINE_SEPARATOR);
		sb.append(" *  End time			: ").append(probesReportDetails.getEndTime()).append(LINE_SEPARATOR);
		sb.append(" * User Agent		: ").append(probesReportDetails.getUserAgent()).append(LINE_SEPARATOR);
		
		sb.append(LINE_SEPARATOR);
		sb.append("### Scenarios execution time ").append(LINE_SEPARATOR);
		sb.append(" | **Scenario**  | **Min**   | **Max** | **Average** | ").append(LINE_SEPARATOR);
		for(ScenarioDetailsDTO scenario: probesReportDetails.getSenarios()){			
			sb.append(" | ").append(scenario.getScenario().name()).append(" | ").append(String.valueOf(scenario.getMinDuartion())).append(" | ").append(String.valueOf(scenario.getMaxDuration())).append(" | ").append(String.valueOf(scenario.getAvrageDuration())).append(" | ").append(LINE_SEPARATOR);
		}
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
	
	
	/**
	 * Build report model
	 * @param executions
	 * @return 
	 */
	private ProbesReportDetails buildProbesReportDetails(List<ExecutionDTO>  executions){
		ProbesReportDetails probesReportDetails =new ProbesReportDetails();
		Date startTime=null;
		Date endDateTime=null;
		
		Map<Scenario,List<ExecutionDTO>>  senarioExecutionMap=new HashMap<Scenario, List<ExecutionDTO>>();		
		
		for(ExecutionDTO execution:executions){
			// build map (senarion, list of execution)
			if(senarioExecutionMap.containsKey(execution.getScenario())){
				senarioExecutionMap.get(execution.getScenario()).add(execution);
			}else{
				List<ExecutionDTO> newList= new ArrayList<ExecutionDTO>(); 
				newList.add(execution);
				senarioExecutionMap.put(execution.getScenario(), newList);
			}
			//initialize common parameters
			if(startTime==null || startTime.after(execution.getDate())){
				startTime=execution.getDate();
			}
			if(endDateTime==null || endDateTime.before(execution.getDate())){
				endDateTime=execution.getDate();
			}
			if(probesReportDetails.getUserAgent()==null){
				probesReportDetails.setUserAgent(execution.getUserAgent());
			}
			if(probesReportDetails.getVersionNumber()==null){
				probesReportDetails.setVersionNumber(execution.getVersionNumber());
			}
		}		
		probesReportDetails.setStartTime(startTime);
		probesReportDetails.setEndTime(endDateTime);
		
		for(Entry<Scenario,List<ExecutionDTO>>  entry:senarioExecutionMap.entrySet()){
			probesReportDetails.getSenarios().add(calculateSenarioDetails(entry.getKey(), entry.getValue()));
		}	
		return probesReportDetails;		
  	}
	/**
	 * Calculate scenario. 
	 * @param scenarioDetailsDTO  scenario details
	 * @param executions list execution of scenario
	 */
	private ScenarioDetailsDTO calculateSenarioDetails(Scenario scenario,List<ExecutionDTO> executions){
		double minDuration=0;
		double maxDuration=-1;
		double sumDuration=0;
		Date startTime=null;
		Date endDateTime=null;
		ScenarioDetailsDTO scenarioDetailsDTO=new ScenarioDetailsDTO();
		scenarioDetailsDTO.setScenario(scenario);
		for(ExecutionDTO execution:executions){
			if(execution.getDuration()<minDuration|| minDuration==0){
				minDuration=execution.getDuration();
			}
			if(execution.getDuration()>maxDuration){
				maxDuration=execution.getDuration();
			}
			sumDuration+=execution.getDuration();
			if(startTime==null || startTime.after(execution.getDate())){
				startTime=execution.getDate();
			}
			if(endDateTime==null || endDateTime.before(execution.getDate())){
				endDateTime=execution.getDate();
			}
		}
		scenarioDetailsDTO.setMaxDuration(maxDuration);
		scenarioDetailsDTO.setMinDuartion(minDuration);
		scenarioDetailsDTO.setAvrageDuration(executions.size()>0?sumDuration/executions.size():-1);
		scenarioDetailsDTO.setEndTime(endDateTime);
		scenarioDetailsDTO.setStartTime(startTime);		
		return scenarioDetailsDTO;
	}
	/**
	 *Build Email object
	 * @return 
	 */
	private Email buildEmail(){
		
		final Email email = new Email();
		email.setFromAddress(properties.getProperty(PropertyKey.MAIL_FROM_ADDRESS));
		email.setFromName(properties.getProperty(PropertyKey.MAIL_FROM_NAME));
		String toAdresse=properties.getProperty(PropertyKey.MAIL_OPTIMISATION_TO_ADDRESS);
		email.setToAddresses(!StringUtils.isBlank(toAdresse)? toAdresse.split(";"):new String[0]);		
		String copyAdresse=properties.getProperty(PropertyKey.MAIL_OPTIMISATION_COPY_ADDRESS);
		email.setCcAddresses(!StringUtils.isBlank(copyAdresse)?copyAdresse.split(";"):new String[0]);
		
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
	
	
	//TODO to be removed
	private void log(ProbesReportDetails probesReportDetails){
		LOG.info("Date debut : "+probesReportDetails.getStartTime());
		LOG.info("Date fin   : "+probesReportDetails.getEndTime());
		LOG.info("Version    : "+probesReportDetails.getVersionNumber());
		LOG.info("Agent      :"+probesReportDetails.getUserAgent());
		LOG.info("		");
		for(ScenarioDetailsDTO senario:probesReportDetails.getSenarios()){
			LOG.info("		Scenario name  : "+senario.getScenario());
			LOG.info("		Min            : "+senario.getMinDuartion());
			LOG.info("		Max            : "+senario.getMaxDuration());
			LOG.info("		Moyenne        : "+senario.getAvrageDuration());
			LOG.info("		");
		}
	}

	public MailSender getSender() {
		return sender;
	}
	
	public void setSender(MailSender sender) {
		this.sender = sender;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
}
