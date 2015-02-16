package org.sigmah.offline.sync;

import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.offline.dao.FileDataAsyncDAO;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.SecureNavigationResult;
import org.sigmah.shared.command.result.SynchronizeResult;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;
import org.sigmah.shared.dto.referential.Container;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

/**
 * Manage sync operations.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class Synchronizer implements OfflineEvent.Source {
    
    private static final double GET_PROJECT_VALUE = 0.1;
    private static final double ACCESS_RIGHTS_VALUE = 0.1;
    private static final double PROJECT_DETAIL_VALUE = 1.0 - GET_PROJECT_VALUE - ACCESS_RIGHTS_VALUE;
    
    @Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;
    
	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Inject
	private TransfertAsyncDAO transfertAsyncDAO;
	
	@Inject
	private FileDataAsyncDAO fileDataAsyncDAO;
    
    @Inject
	private DispatchAsync dispatcher;
	
	@Inject
	private EventBus eventBus;
    
    
	public <T> void push(final RequestManager<T> requestManager) {
        final int removeRequestId = requestManager.prepareRequest();
        
		updateDiaryAsyncDAO.getAll(new RequestManagerCallback<T, Map<Integer, Command>>(requestManager) {

            @Override
            public void onRequestSuccess(final Map<Integer, Command> commands) {
                dispatcher.execute(new Synchronize(new ArrayList(commands.values())), new RequestManagerCallback<T, SynchronizeResult>(requestManager) {
                    
                    @Override
                    public void onRequestSuccess(SynchronizeResult result) {
                        updateDiaryAsyncDAO.removeAll(commands.keySet(), new RequestManagerCallback<T, Void>(requestManager, removeRequestId) {
                            
                            @Override
                            public void onRequestSuccess(Void result) {
                                // Success
								eventBus.fireEvent(new OfflineEvent(Synchronizer.this, ApplicationState.ONLINE));
                            }
						});
						
						// Update local ids for files.
						transfertAsyncDAO.replaceIds(result.getFiles());
						fileDataAsyncDAO.replaceIds(result.getFiles());
						
						// Display erros.
						if(!result.getErrors().isEmpty()) {
							final StringBuilder errorBuilder = new StringBuilder();
							
							for(final Map.Entry<Container, List<String>> entry : result.getErrors().entrySet()) {
								if(entry.getKey().getType() == Container.Type.PROJECT) {
									errorBuilder.append(I18N.CONSTANTS.project());
								} else {
									errorBuilder.append(I18N.CONSTANTS.orgunit());
								}
								
								errorBuilder.append(entry.getKey().getName())
									.append(" - ")
									.append(entry.getKey().getFullName())
									.append(" (")
									.append("<ul>");
									
								for(final String error : entry.getValue()) {
									errorBuilder.append("<li>").append(error).append("</li>");
								}
								errorBuilder.append("</ul>");
							}

							if(result.isErrorConcernFiles()) {
								errorBuilder.append(I18N.MESSAGES.conflictFiles());
							}
							
							errorBuilder.append(I18N.MESSAGES.conflictSentByMail());
						
							N10N.error(errorBuilder.toString());
						}
                    }
                });
                
			}
		});
        
		monitoredPointAsyncDAO.removeTemporaryObjects(new RequestManagerCallback<T, Void>(requestManager) {
			
			@Override
			public void onRequestSuccess(Void result) {
				// Monitored points created offline have been successfully removed.
			}
		});
        
		reminderAsyncDAO.removeTemporaryObjects(new RequestManagerCallback<T, Void>(requestManager) {
			
			@Override
			public void onRequestSuccess(Void result) {
				// Reminders created offline have been successfully removed.
			}
		});
		
        requestManager.ready();
	}
	
	public void pull(final SynchroProgressListener progressListener) {
		final GetProjects getProjects = new GetProjects();
		getProjects.setMappingMode(ProjectDTO.Mode.WITH_RELATED_PROJECTS);
        getProjects.setFavoritesOnly(true);
        
        final double[] progress = {0.0};
        
        // Called if the synchronization failed or when it is completed.
		final CommandQueue queue = new CommandQueue(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				progressListener.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				progressListener.onComplete();
			}
		}, dispatcher);
		
        // Storing access rights
        final double pageAccessProgress = ACCESS_RIGHTS_VALUE / Page.values().length;
        for(final Page page : Page.values()) {
			queue.add(new SecureNavigationCommand(page), new CommandResultHandler<SecureNavigationResult>() {

				@Override
				protected void onCommandSuccess(SecureNavigationResult result) {
                    updateProgress(pageAccessProgress, progress, progressListener);
                }
                
            });
        }
        
        // Storing favorites projects
		queue.add(getProjects, new CommandResultHandler<ListResult<ProjectDTO>>() {
			
			@Override
			protected void onCommandSuccess(ListResult<ProjectDTO> result) {
                updateProgress(GET_PROJECT_VALUE, progress, progressListener);
                final double projectProgress = PROJECT_DETAIL_VALUE / result.getSize();
                
                final HashSet<ProjectDTO> projects = new HashSet<ProjectDTO>();
                projects.addAll(result.getList());
                
                 // Also fetching related projects
                for(final ProjectDTO project : result.getList()) { 
                    for(final ProjectFundingDTO funding : project.getFunding()) {
                        projects.add(funding.getFunding());
                    }
                    for(final ProjectFundingDTO funded : project.getFunded()) {
                        projects.add(funded.getFunded());
                    }
                }
                
				for(final ProjectDTO project : projects) {
					final Integer projectId = project.getId();
					
					queue.add(new GetProject(projectId, null), new CommandResultHandler<ProjectDTO>() {

						@Override
						protected void onCommandSuccess(ProjectDTO result) {
							// TODO: Fetch remaining objects.
							final List<FlexibleElementDTO> elements = result.getProjectModel().getAllElements();
                            final double flexibleElementCount = elements.size();
                            final double historyCount = elements.size();
                            final double calendarCount = project.getCalendarId() != null ? 1.0 : 0.0;
							final double projectReports = 1.0;
							
                            final double elementProgress = projectProgress / (flexibleElementCount + historyCount + calendarCount + projectReports);
                            
							// Fetching flexible elements and their history
							for(final FlexibleElementDTO element : elements) {
								// Caching element value
								final GetValue getValue =  new GetValue(projectId, element.getId(), element.getEntityName());
								queue.add(getValue, new CommandResultHandler<ValueResult>() {
									@Override
									protected void onCommandSuccess(ValueResult result) {
										// Success
                                        updateProgress(elementProgress, progress, progressListener);
									}
								});
								
								// Caching value history
								final GetHistory getHistory = new GetHistory(element.getId(), projectId);
								queue.add(getHistory, new CommandResultHandler<ListResult<HistoryTokenListDTO>>() {
									@Override
									protected void onCommandSuccess(ListResult<HistoryTokenListDTO> result) {
										// Success
                                        updateProgress(elementProgress, progress, progressListener);
									}
								});
							}
                            
							// Fetching the calendar
							final PersonalCalendarIdentifier identifier = new PersonalCalendarIdentifier(project.getCalendarId());
							queue.add(new GetCalendar(CalendarType.Personal, identifier), new CommandResultHandler<Calendar>() {
								
								@Override
								protected void onCommandSuccess(Calendar result) {
									// Success
									updateProgress(elementProgress, progress, progressListener);
								}
							});
							
							// Fetching project reports
							queue.add(new GetProjectReports(projectId, null), new CommandResultHandler<ListResult<ReportReference>>() {
								
								@Override
								protected void onCommandSuccess(ListResult<ReportReference> result) {
									if(result == null || result.getSize() == 0) {
										// No reports
										updateProgress(elementProgress, progress, progressListener);
									} else {
										final double reportProgress = elementProgress / result.getSize();
										
										// Fetching actuals reports
										for(final ReportReference reportReference : result.getList()) {
											queue.add(new GetProjectReport(reportReference.getId()), new CommandResultHandler<ProjectReportDTO>() {

												@Override
												protected void onCommandSuccess(ProjectReportDTO result) {
													// Success
													updateProgress(reportProgress, progress, progressListener);
												}
											});
										}
									}
								}
							});
						}
					});
				}
			}
		});
		
		queue.run();
	}
    
    private void updateProgress(double progress, double[] total, SynchroProgressListener progressListener) {
        total[0] += progress;
		if(total[0] > 1.0) {
			total[0] = 1.0;
		}
        progressListener.onProgress(total[0]);
    }
}
