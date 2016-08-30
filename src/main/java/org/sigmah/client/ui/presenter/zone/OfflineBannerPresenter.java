package org.sigmah.client.ui.presenter.zone;

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


import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.OfflineBannerView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.offline.appcache.ApplicationCache;
import org.sigmah.offline.appcache.ApplicationCacheManager;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.trace.TraceMenuPanel;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.client.util.MessageType;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.offline.appcache.ApplicationCacheEventHandler;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ProgressType;
import org.sigmah.offline.sync.SynchroProgressListener;
import org.sigmah.offline.sync.UpdateDates;
import org.sigmah.offline.view.OfflineMenuPanel;
import org.sigmah.offline.view.SynchronizePopup;
import org.sigmah.shared.file.HasProgressListeners;
import org.sigmah.shared.file.ProgressAdapter;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertType;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Image;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.ui.res.icon.offline.OfflineIconBundle;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.profiler.Checkpoint;
import org.sigmah.client.util.profiler.Execution;
import org.sigmah.client.util.profiler.ExecutionAsyncDAO;
import org.sigmah.client.util.profiler.ProfilerStore;
import org.sigmah.shared.command.SendProbeReport;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.profile.CheckPointDTO;
import org.sigmah.shared.dto.profile.ExecutionDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;
/**
 * Offline banner presenter displaying offline status.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OfflineBannerPresenter extends AbstractZonePresenter<OfflineBannerPresenter.View> 
implements OfflineEvent.Source {
	
	private static final int AUTOCLOSE_TIME = 3000;
    
    private static final String STYLE_MENU_VISIBLE = "offline-button-active";
    private static final double PUSH_VALUE = 0.4;
	private static final double UNDEFINED = -1.0;
    
	private Map<ProgressType, Double> progresses;
    
	private boolean linkHover;
	private boolean menuHover;
	private boolean forceOpen;
	
	private boolean traceLinkHover;
	private boolean traceMenuHover;
	private boolean traceForceOpen;
	
	private ApplicationState lastState = ApplicationState.UNKNOWN;
	
	private final ExecutionAsyncDAO executionAsyncDAO = new ExecutionAsyncDAO();
	
	/**
	 * View interface.
	 */
	@ImplementedBy(OfflineBannerView.class)
	public static interface View extends ViewInterface {

		Panel getTraceHandle();		
		Panel getStatusPanel();        
		OfflineMenuPanel getMenuPanel();		
		TraceMenuPanel getTraceMenuPanel();
		Panel getMenuHandle();        
        SynchronizePopup getSynchronizePopup();		
		void setStatus(ApplicationState state);
        void setProgress(double progress, boolean undefined);
        void setSynchronizeAnchorEnabled(boolean enabled);
        void setTransferFilesAnchorEnabled(boolean enabled);
		boolean isEnabled(Anchor anchor);
        void setWarningIconVisible(boolean visible);
		public Image getTraceModeIcon();
	}
    
	@Inject
	public OfflineBannerPresenter(View view, Injector injector, TransfertManager transfertManager) {
		super(view, injector);
	}
    
	@Override
	public void onBind() {
        progresses = new EnumMap<ProgressType, Double>(ProgressType.class);
        view.getSynchronizePopup().initialize();
		
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				onStateChange(event.getState());
			}
		});
        
        // Application cache progress
		ApplicationCacheManager.addHandler(createApplicationCacheEventHandler());
        
        // File transfer progress
		final TransfertManager transfertManager = injector.getTransfertManager();
        if(transfertManager instanceof HasProgressListeners) {
            ((HasProgressListeners)transfertManager).setProgressListener(TransfertType.DOWNLOAD, createProgressAdapter(ProgressType.DOWNLOAD));
            ((HasProgressListeners)transfertManager).setProgressListener(TransfertType.UPLOAD, createProgressAdapter(ProgressType.UPLOAD));
        } else {
            // Remove file transfer references for unsupported browsers.
            view.getMenuPanel().removeFileBaseWidgets();
        }
        
        // Toggle visibility of the offline menu
		/**/	
		view.getMenuHandle().addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				linkHover = true;
				updateMenuVisibility();
			}
			
		}, MouseOverEvent.getType());
		
		view.getMenuHandle().addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				linkHover = false;
				forceOpen = false;
				updateMenuVisibility();
			}
			
		}, MouseOutEvent.getType());
		 
		// Toggle visibility of the offline menu
		view.getTraceHandle().addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				traceLinkHover = true;
				updateTraceMenuVisibility();     
			}
			
		}, MouseOverEvent.getType());
		// Hide menu
		view.getTraceHandle().addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				traceLinkHover = false;
				traceForceOpen = false;
				updateTraceMenuVisibility();
			}
			
		}, MouseOutEvent.getType());
		
        final OfflineMenuPanel menuPanel = view.getMenuPanel();
		
		menuPanel.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				menuHover = true;
				updateMenuVisibility();
			}			
		}, MouseOverEvent.getType());
		
		menuPanel.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				menuHover = false;
				forceOpen = false;
				updateMenuVisibility();
			}			
		}, MouseOutEvent.getType());
		
		final TraceMenuPanel traceMenuPanel = view.getTraceMenuPanel();		
		traceMenuPanel.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				traceMenuHover = true;
				updateTraceMenuVisibility();
			}
			
		}, MouseOverEvent.getType());
		
		traceMenuPanel.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				traceMenuHover = false;
				traceForceOpen = false;
				updateTraceMenuVisibility();
			}
			
		}, MouseOutEvent.getType());
		
		
        menuPanel.setSigmahUpdateDate(ApplicationCacheManager.getUpdateDate());
        menuPanel.setDatabaseUpdateDate(getDatabaseUpdateDate());
        
        // Destroy local database
        menuPanel.getRemoveOfflineDataAnchor().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                confirmUserDatabaseRemoval();
            }
        });
		
        updateProgressBars();
		
		// Actions
		
		menuPanel.getUpdateDatabaseAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!view.isEnabled(menuPanel.getUpdateDatabaseAnchor())) {
					return;
				}
				if (lastState == ApplicationState.READY_TO_SYNCHRONIZE) {
					setMenuVisible(false);
					pushAndPull();
				} else if(lastState == ApplicationState.ONLINE) {
					setMenuVisible(false);
					pull();
				}
			}
		});
		
		menuPanel.getTransferFilesAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!view.isEnabled(menuPanel.getTransferFilesAnchor())) {
					return;
				}
				if (lastState == ApplicationState.ONLINE) {
					setMenuVisible(false);
					eventBus.navigate(Page.OFFLINE_SELECT_FILES);
				}
			}
		});
		
		traceMenuPanel.getSendReportAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				executionAsyncDAO.getAllExecutions( new AsyncCallback<List<Execution>>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.error("Excpetion occured ");
						}
						@Override
						public void onSuccess(List<Execution> listExecution) {
							List<ExecutionDTO> dtos=new ArrayList<ExecutionDTO>();
							Log.info("listExecution:"+listExecution);
							if(listExecution!=null && ! listExecution.isEmpty()){
								//Mapping from Execution JavaScriptObject to ExecutionDTO
								for(Execution execution :listExecution){
									ExecutionDTO executionDTO=new ExecutionDTO();
									executionDTO.setApplicationCacheStatus(execution.getApplicationCacheStatus());
									executionDTO.setDate(execution.getDate());
									executionDTO.setOnligne(execution.isOnline());
									executionDTO.setScenario(execution.getScenario());
									executionDTO.setUserAgent(execution.getUserAgent());
									executionDTO.setUserEmailAddress(execution.getUserEmailAddress());
									executionDTO.setVersionNumber(execution.getVersionNumber());
									executionDTO.setDuration(execution.getDuration());
									for(Checkpoint checkPoint:execution.getCheckpointSequence()){
										CheckPointDTO checkPointDto=new CheckPointDTO();
										checkPointDto.setDuration(checkPoint.getDuration());
										checkPointDto.setName(checkPoint.getName());
										checkPoint.setTime(checkPoint.getTime());
										executionDTO.getCheckpoints().add(checkPointDto);
									}
									dtos.add(executionDTO);
								}
								//call server
								dispatch.execute(new SendProbeReport(dtos), new CommandResultHandler<Result>(){
									@Override
									protected void onCommandSuccess(Result result) {
										N10N.infoNotif("Info", "<p>"+I18N.CONSTANTS.probeReportSentSucces() +"</p>");
										executionAsyncDAO.removeDataBase(ProfilerStore.EXECUTION);
									}
									protected void onCommandFailure(Result result) {
										N10N.errorNotif("Error", "<p>"+I18N.CONSTANTS.probeReportSentFailure()+"</p>");
									}
								});
							}else{
								N10N.errorNotif("Error", "<p>"+I18N.CONSTANTS.probeReportEmpty()+"</p>");
							}							
						}
					});
			}
		});
		
		traceMenuPanel.getActiveDesactiveModeAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(Profiler.INSTANCE.isActive()){
					Profiler.INSTANCE.setActive(false);
					traceMenuPanel.getActiveDesactiveModeAnchor().setText(I18N.CONSTANTS.probesEnableTrace());
					traceMenuPanel.getDateActivationModeLabel().setVisible(false);
					traceMenuPanel.getDateActivationModeVariable().setVisible(false);	
					traceMenuPanel.getDateActivationModeVariable().setVisible(false);
					traceMenuPanel.getActiveDesactiveModeAnchor().removeStyleName(traceMenuPanel.getDISABLE_ACTION_STYLE());
					traceMenuPanel.getActiveDesactiveModeAnchor().addStyleName(traceMenuPanel.getENABLE_ACTION_STYLE());
					UpdateDates.setSigmahActivationTraceDate(null);
					view.getTraceModeIcon().setResource(OfflineIconBundle.INSTANCE.traceOff());
					
				}else{
					Profiler.INSTANCE.setActive(true);
					traceMenuPanel.getActiveDesactiveModeAnchor().setText(I18N.CONSTANTS.probesDisableTrace());
					UpdateDates.setSigmahActivationTraceDate(new Date());
					traceMenuPanel.getDateActivationModeLabel().setVisible(true);
					traceMenuPanel.getDateActivationModeVariable().setVisible(true);
					traceMenuPanel.getActiveDesactiveModeAnchor().removeStyleName(traceMenuPanel.getENABLE_ACTION_STYLE());
					traceMenuPanel.getActiveDesactiveModeAnchor().addStyleName(traceMenuPanel.getDISABLE_ACTION_STYLE());
					traceMenuPanel.getDateActivationModeVariable().setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(UpdateDates.getSigmahActivationTraceDate()));
					view.getTraceModeIcon().setResource(OfflineIconBundle.INSTANCE.traceOn());
				}				
			}
		});
	}
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		final Boolean showBriefly = zoneRequest.getData(RequestParameter.SHOW_BRIEFLY);
		final Boolean pullDatabase = zoneRequest.getData(RequestParameter.PULL_DATABASE);
		
		// Display briefly the menu panel
		if(showBriefly != null && showBriefly) {
			forceOpen = true;
			updateMenuVisibility();
			
			new Timer() {

				@Override
				public void run() {
					forceOpen = false;
					updateMenuVisibility();
				}
			}.schedule(AUTOCLOSE_TIME);
		}
		
		// Updating dates
		// BUGFIX #714: Dates are refreshed when the current user changes.
		view.getMenuPanel().setSigmahUpdateDate(ApplicationCacheManager.getUpdateDate());
        view.getMenuPanel().setDatabaseUpdateDate(getDatabaseUpdateDate());
		if(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.PROBES_MANGMENT)){
			view.getTraceHandle().setVisible(true);
			view.getTraceHandle().getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}else{
			view.getTraceHandle().setVisible(false);			
		}
		// Users requested to pull data from the server.
		if(pullDatabase != null && pullDatabase) {
			pull();
		}
	}
    
	private void updateMenuVisibility() {
		setMenuVisible(linkHover || menuHover || forceOpen);
	}
	private void updateTraceMenuVisibility() {
		setTraceMenuVisible(traceLinkHover || traceMenuHover || traceForceOpen);
	}
	
	private void setTraceMenuVisible(boolean visible) {
        view.getTraceMenuPanel().setVisible(visible);
                
        if(visible) {
            view.getTraceHandle().addStyleName(STYLE_MENU_VISIBLE);
        } else {
            view.getTraceHandle().removeStyleName(STYLE_MENU_VISIBLE);
        }
    }
    private void setMenuVisible(boolean visible) {
        view.getMenuPanel().setVisible(visible);
                
        if(visible) {
            view.getMenuHandle().addStyleName(STYLE_MENU_VISIBLE);
        } else {
            view.getMenuHandle().removeStyleName(STYLE_MENU_VISIBLE);
        }
    }
    
    private void updateProgressBars() {
        double total = 0.0;
		int undefined = 0;
        
        for(final ProgressType progressType : ProgressType.values()) {
            final Double progress = progresses.get(progressType);
            final RatioBar bar = view.getMenuPanel().getBar(progressType);
            
            if(progress != null) {
				view.getMenuPanel().setBarVisible(progressType, true);
				
				if(progress != UNDEFINED) {
					total += progress;
					bar.setRatio(progress * 100.0);
				} else {
					bar.setRatioUndefined();
					undefined++;
				}
				
			} else {
                view.getMenuPanel().setBarVisible(progressType, false);
            }
        }
        
        final int size = progresses.size();
        if(size > 0) {
            total /= (double) size;
		}
		
        view.setProgress(total, undefined > 0 && undefined == size);
    }
	
	private int getPendingTransfers(ProgressType type) {
		final HasProgressListeners hasProgressListeners = (HasProgressListeners) injector.getTransfertManager();
		switch(type) {
			case DOWNLOAD:
				return hasProgressListeners.getDownloadQueueSize();
				
			case UPLOAD:
				return hasProgressListeners.getUploadQueueSize();
		}
		throw new IllegalArgumentException("Progress type '" + type + "' is not supported.");
	}
	
	private ProgressAdapter createProgressAdapter(final ProgressType type) {
		return new ProgressAdapter() {
			@Override
			public void onProgress(double progress, double speed) {
				if(progress < 1.0) { 
					progresses.put(type, progress);
					updateProgressBars();
					view.getMenuPanel().setPendingsTransfers(type, getPendingTransfers(type));
				} else {
					progresses.remove(type);
					updateProgressBars();
					view.getMenuPanel().setPendingsTransfers(type, 0);
				}
			}

			@Override
			public void onLoad(String result) {
				progresses.remove(type);
				updateProgressBars();
				view.getMenuPanel().setPendingsTransfers(type, 0);
			}
		};
	}
	
    private void confirmUserDatabaseRemoval() {
        N10N.confirmation(I18N.CONSTANTS.offlineModeHeader(), I18N.CONSTANTS.offlineActionDestroyLocalDataConfirm(), new ConfirmCallback() {
            @Override
            public void onAction() {
                setMenuVisible(false);

                N10N.message(I18N.CONSTANTS.offlineActionDestroyLocalDataWorking(), MessageType.OFFLINE);
                
				Profiler.INSTANCE.deleteDatabase();
				
                final OpenDatabaseRequest request = IndexedDB.deleteUserDatabase(auth());
                request.addCallback(new AsyncCallback<Request>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        N10N.message(I18N.CONSTANTS.offlineActionDestroyLocalDataFailure(), MessageType.OFFLINE);
                    }
                    @Override
                    public void onSuccess(Request result) {
                        UpdateDates.setDatabaseUpdateDate(auth(), null);
                        Window.Location.reload();
                    }
                });
            }
        }, new ConfirmCallback() {
            @Override
            public void onAction() {
                setMenuVisible(false);
            }
        });
    }
    
    private ApplicationCacheEventHandler createApplicationCacheEventHandler() {
        return new ApplicationCacheEventHandler() {
			@Override
			public void onStatusChange(ApplicationCache.Status status) {
				switch (status) {
					case DOWNLOADING:
						progresses.put(ProgressType.APPLICATION_CACHE, UNDEFINED);
						updateProgressBars();
						break;
					case UPDATEREADY:
                        progresses.remove(ProgressType.APPLICATION_CACHE);
                        updateProgressBars();
                        view.getMenuPanel().setSigmahUpdateDate(new Date());
						
						ApplicationCacheManager.swapCacheAndReload();
						break;
					default:
						break;
				}
			}

			@Override
			public void onProgress(int loaded, int total) {
                progresses.put(ProgressType.APPLICATION_CACHE, (double)loaded/(double)total);
                updateProgressBars();
			}
		};
    }
    
    private Date getDatabaseUpdateDate() {
        return UpdateDates.getDatabaseUpdateDate(auth());
    }
    
    private void setDatabaseUpdateDate(Date date) {
        UpdateDates.setDatabaseUpdateDate(auth(), date);
        view.getMenuPanel().setDatabaseUpdateDate(date);
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.OFFLINE_BANNER;
	}

    private void onStateChange(ApplicationState state) {
		this.lastState = state;
        view.setStatus(state);
            
        final Anchor syncAnchor = view.getMenuPanel().getUpdateDatabaseAnchor();
		final Anchor fileAnchor = view.getMenuPanel().getTransferFilesAnchor();
		
        switch(state) {
            case OFFLINE:
                syncAnchor.getElement().getStyle().setDisplay(Style.Display.NONE);
                fileAnchor.getElement().getStyle().setDisplay(Style.Display.NONE);
				RootPanel.getBodyElement().addClassName("offline");
                break;

            case READY_TO_SYNCHRONIZE:
				fileAnchor.getElement().getStyle().setDisplay(Style.Display.NONE);
                syncAnchor.getElement().getStyle().clearDisplay();
                syncAnchor.setText(I18N.CONSTANTS.offlineActionSynchronize());
				RootPanel.getBodyElement().addClassName("offline");
                break;

            case ONLINE:
                syncAnchor.getElement().getStyle().clearDisplay();
                syncAnchor.setText(I18N.CONSTANTS.offlineActionUpdateDatabase());
				RootPanel.getBodyElement().removeClassName("offline");
				
                fileAnchor.getElement().getStyle().clearDisplay();
                break;

            default:
                break;
        }
    }

    private void pushAndPull() {
        view.setSynchronizeAnchorEnabled(false);

        progresses.put(ProgressType.DATABASE, 0.0);
        view.setWarningIconVisible(false);
        
        // Show the modal progress popup
        view.getSynchronizePopup().setTask(I18N.CONSTANTS.offlineSynchronizePush());
        view.getSynchronizePopup().center();
        
        injector.getSynchronizer().push(new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                N10N.error(I18N.CONSTANTS.offlineSynchronizePushError());
                view.setWarningIconVisible(true);
                view.setSynchronizeAnchorEnabled(true);
                
                progresses.remove(ProgressType.DATABASE);
                updateProgressBars();
				
				view.getSynchronizePopup().hide();
            }

            @Override
            public void onSuccess(Void result) {
				// Success
				eventBus.fireEvent(new OfflineEvent(OfflineBannerPresenter.this, ApplicationState.ONLINE));
				
                view.getSynchronizePopup().setProgress(PUSH_VALUE);
                view.getSynchronizePopup().setTask(I18N.CONSTANTS.offlineSynchronizePull());
                progresses.put(ProgressType.DATABASE, PUSH_VALUE);

                injector.getSynchronizer().pull(new SynchroProgressListener() {

                    @Override
                    public void onProgress(double progress) {
                        final double total = PUSH_VALUE + progress * (1.0 - PUSH_VALUE);
                        view.getSynchronizePopup().setProgress(total);
                        progresses.put(ProgressType.DATABASE, total);
                        updateProgressBars();
                    }

                    @Override
                    public void onComplete() {
                        view.setSynchronizeAnchorEnabled(true);
                        progresses.remove(ProgressType.DATABASE);
                        updateProgressBars();
                        view.getSynchronizePopup().hide();
                        setDatabaseUpdateDate(new Date());
						
						// Refresh the current page.
						eventBus.navigateRequest(injector.getPageManager().getCurrentPageRequest());
						
						pullFilesInvite();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        progresses.remove(ProgressType.DATABASE);
                        updateProgressBars();
                        
                        view.getSynchronizePopup().hide();
                        N10N.error(I18N.CONSTANTS.offlineSynchronizePullError());
                        view.setWarningIconVisible(true);
                        view.setSynchronizeAnchorEnabled(true);
                    }
                });
            }
        });
    }
    
    private void pull() {
        view.setSynchronizeAnchorEnabled(false);
        progresses.put(ProgressType.DATABASE, 0.0);
        view.setWarningIconVisible(false);
        
        injector.getSynchronizer().pull(new SynchroProgressListener() {

            @Override
            public void onProgress(double progress) {
                progresses.put(ProgressType.DATABASE, progress);
                updateProgressBars();
            }

            @Override
            public void onComplete() {
                progresses.remove(ProgressType.DATABASE);
                updateProgressBars();
                setDatabaseUpdateDate(new Date());
                view.setSynchronizeAnchorEnabled(true);
				
				pullFilesInvite();
            }

            @Override
            public void onFailure(Throwable caught) {
                N10N.error(I18N.CONSTANTS.offlineSynchronizePullError());
                view.setWarningIconVisible(true);
                view.setSynchronizeAnchorEnabled(true);
                
                progresses.remove(ProgressType.DATABASE);
                updateProgressBars();
            }
        });
    }
	
	private void pullFilesInvite() {
		N10N.confirmation(I18N.CONSTANTS.sigmahOfflineAlsoSynchronizeFilesInvite(), new ConfirmCallback() {

			@Override
			public void onAction() {
				eventBus.navigate(Page.OFFLINE_SELECT_FILES);
			}
		});
	}
	
}
