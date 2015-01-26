package org.sigmah.offline.sync;

import org.sigmah.offline.status.ApplicationState;
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
import org.sigmah.client.page.Page;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.status.ConnectionStatus;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.SecureNavigationResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.calendar.CalendarType;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

/**
 * Manage sync operations.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class Synchronizer {
    
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
	private DispatchAsync dispatcher;
    
    private ConnectionStatus connectionStatus;

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
    
    public void getApplicationState(boolean online, final StateListener listener) {
        if(online) {
            isPushNeeded(new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable caught) {
                    listener.onStateKnown(ApplicationState.ONLINE);
                }

                @Override
                public void onSuccess(Boolean pushNeeded) {
                    if(pushNeeded) {
                        listener.onStateKnown(ApplicationState.READY_TO_SYNCHRONIZE);
                    } else {
                        listener.onStateKnown(ApplicationState.ONLINE);
                    }
                }
            });
        } else {
            listener.onStateKnown(ApplicationState.OFFLINE);
        }
    }
	
	public <T> void push(final RequestManager<T> requestManager) {
        final int removeRequestId = requestManager.prepareRequest();
        
		updateDiaryAsyncDAO.getAll(new RequestManagerCallback<T, Map<Integer, Command>>(requestManager) {

            @Override
            public void onRequestSuccess(final Map<Integer, Command> commands) {
                final Synchronize synchronize = new Synchronize(new ArrayList(commands.values()));
                dispatcher.execute(synchronize, new RequestManagerCallback<T, VoidResult>(requestManager) {
                    
                    @Override
                    public void onRequestSuccess(VoidResult result) {
                        updateDiaryAsyncDAO.removeAll(commands.keySet(), new RequestManagerCallback<T, Void>(requestManager, removeRequestId) {
                            
                            @Override
                            public void onRequestSuccess(Void result) {
                                // Success
                                connectionStatus.setState(ApplicationState.ONLINE);
                            }
                        });
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
	
	public void isPushNeeded(final AsyncCallback<Boolean> callback) {
		if(updateDiaryAsyncDAO.isAnonymous()) {
			callback.onSuccess(Boolean.FALSE);
			
		} else {
			updateDiaryAsyncDAO.count(new AsyncCallback<Integer>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Integer result) {
					callback.onSuccess(result > 0);
				}
			});
		}
	}
	
	public void pull(final SynchroProgressListener progressListener) {
		final GetProjects getProjects = new GetProjects();
		getProjects.setMappingMode(ProjectDTO.Mode.WITH_RELATED_PROJECTS);
        getProjects.setFavoritesOnly(true);
        
        final double[] progress = {0.0};
        
        // Called if the synchronization failed or when it is completed.
        final RequestManager<Void> manager = new RequestManager<Void>(null, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                progressListener.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                progressListener.onComplete();
            }
        });
        
        // Storing access rights
        final double pageAccessProgress = ACCESS_RIGHTS_VALUE / Page.values().length;
        for(final Page page : Page.values()) {
            dispatcher.execute(new SecureNavigationCommand(page), new RequestManagerCallback<Void, SecureNavigationResult>(manager) {

                @Override
                public void onRequestSuccess(SecureNavigationResult result) {
                    updateProgress(pageAccessProgress, progress, progressListener);
                }
                
            });
        }
        
        // Storing favorites projects
		dispatcher.execute(getProjects, new RequestManagerCallback<Void, ListResult<ProjectDTO>>(manager) {
			
			@Override
			public void onRequestSuccess(ListResult<ProjectDTO> result) {
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
					
					dispatcher.execute(new GetProject(projectId, null), new RequestManagerCallback<Void, ProjectDTO>(manager) {

						@Override
						public void onRequestSuccess(ProjectDTO result) {
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
								dispatcher.execute(getValue, new RequestManagerCallback<Void, ValueResult>(manager) {
									@Override
									public void onRequestSuccess(ValueResult result) {
										// Success
                                        updateProgress(elementProgress, progress, progressListener);
									}
								});
								
								// Caching value history
								final GetHistory getHistory = new GetHistory(element.getId(), projectId);
								dispatcher.execute(getHistory, new RequestManagerCallback<Void, ListResult<HistoryTokenListDTO>>(manager) {
									@Override
									public void onRequestSuccess(ListResult<HistoryTokenListDTO> result) {
										// Success
                                        updateProgress(elementProgress, progress, progressListener);
									}
								});
							}
                            
							// Fetching the calendar
							final PersonalCalendarIdentifier identifier = new PersonalCalendarIdentifier(project.getCalendarId());
							dispatcher.execute(new GetCalendar(CalendarType.Personal, identifier), new RequestManagerCallback<Void, Calendar>(manager) {
								
								@Override
								public void onRequestSuccess(Calendar result) {
									// Success
									updateProgress(elementProgress, progress, progressListener);
								}
							});
							
							// Fetching project reports
							dispatcher.execute(new GetProjectReports(projectId, null), new RequestManagerCallback<Void, ListResult<ReportReference>>(manager) {
								
								@Override
								public void onRequestSuccess(ListResult<ReportReference> result) {
									if(result == null || result.getSize() == 0) {
										// No reports
										updateProgress(elementProgress, progress, progressListener);
									} else {
										final double reportProgress = elementProgress / result.getSize();
										
										// Fetching actuals reports
										for(final ReportReference reportReference : result.getList()) {
											dispatcher.execute(new GetProjectReport(reportReference.getId()), new RequestManagerCallback<Void, ProjectReportDTO>(manager) {

												@Override
												public void onRequestSuccess(ProjectReportDTO result) {
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
				manager.ready();
			}
		});
	}
    
    private void updateProgress(double progress, double[] total, SynchroProgressListener progressListener) {
        total[0] += progress;
        progressListener.onProgress(total[0]);
    }
}
