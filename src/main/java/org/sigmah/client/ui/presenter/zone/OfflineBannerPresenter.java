package org.sigmah.client.ui.presenter.zone;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
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
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.client.util.MessageType;
import org.sigmah.offline.appcache.ApplicationCacheEventHandler;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.status.ProgressType;
import org.sigmah.offline.sync.SynchroProgressListener;
import org.sigmah.offline.sync.Synchronizer;
import org.sigmah.offline.sync.UpdateDates;
import org.sigmah.offline.view.OfflineMenuPanel;
import org.sigmah.offline.view.SynchronizePopup;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.HasProgressListeners;
import org.sigmah.shared.file.ProgressAdapter;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertType;

/**
 * Offline banner presenter displaying offline status.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OfflineBannerPresenter extends AbstractZonePresenter<OfflineBannerPresenter.View> 
implements OfflineEvent.Source {
	
	public static final String SHOW_BRIEFLY = "SHOW_BRIEFLY";
	private static final int AUTOCLOSE_TIME = 3000;
    
    private static final String STYLE_MENU_VISIBLE = "offline-button-active";
    private static final double PUSH_VALUE = 0.4;
	private static final double UNDEFINED = -1.0;
    
    private HandlerRegistration syncHandlerRegistration;
    private HandlerRegistration fileHandlerRegistration;
	private Map<ProgressType, Double> progresses;
    
	private boolean linkHover;
	private boolean menuHover;
	private boolean forceOpen;
	
	/**
	 * View interface.
	 */
	@ImplementedBy(OfflineBannerView.class)
	public static interface View extends ViewInterface {

		Panel getStatusPanel();
        Panel getMenuHandle();
        OfflineMenuPanel getMenuPanel();
        SynchronizePopup getSynchronizePopup();

		void setStatus(ApplicationState state);
        void setProgress(double progress, boolean undefined);
        void setSynchronizeAnchorEnabled(boolean enabled);
        void setTransferFilesAnchorEnabled(boolean enabled);
        void setWarningIconVisible(boolean visible);
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
	}
    
    /**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		final String type = zoneRequest.getData(RequestParameter.TYPE);
		if(SHOW_BRIEFLY.equals(type)) {
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
	}
    
	private void updateMenuVisibility() {
		setMenuVisible(linkHover || menuHover || forceOpen);
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
        view.setStatus(state);
            
        if(syncHandlerRegistration != null) {
            syncHandlerRegistration.removeHandler();
            syncHandlerRegistration = null;
        }
		
		if(fileHandlerRegistration != null) {
			fileHandlerRegistration.removeHandler();
			fileHandlerRegistration = null;
		}

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
                syncHandlerRegistration = syncAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        setMenuVisible(false);
                        pushAndPull();
                    }
                });
				RootPanel.getBodyElement().addClassName("offline");
                break;

            case ONLINE:
                syncAnchor.getElement().getStyle().clearDisplay();
                syncAnchor.setText(I18N.CONSTANTS.offlineActionUpdateDatabase());
				RootPanel.getBodyElement().removeClassName("offline");
                syncHandlerRegistration = syncAnchor.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        setMenuVisible(false);
                        pull();
                    }
                });
				
                fileAnchor.getElement().getStyle().clearDisplay();
				fileHandlerRegistration = fileAnchor.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						setMenuVisible(false);
						pushAndPullFiles();
					}
				});
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
				pushAndPullFiles();
			}
		});
	}
	
	private void pushAndPullFiles() {
		view.setTransferFilesAnchorEnabled(false);

		// Push files
		final TransfertAsyncDAO transfertAsyncDAO = injector.getTransfertAsyncDAO();
		transfertAsyncDAO.getAll(TransfertType.UPLOAD, new AsyncCallback<TransfertJS>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("An error occured while searching for not uploaded files.", caught);
			}

			@Override
			public void onSuccess(TransfertJS result) {
				((HasProgressListeners)injector.getTransfertManager()).resumeUpload(result);
			}
		});
		
		// Pull all files
		dispatch.execute(new GetFilesFromFavoriteProjects(), new AsyncCallback<ListResult<FileVersionDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				view.setTransferFilesAnchorEnabled(true);
				N10N.error(I18N.CONSTANTS.offlineActionTransferFilesListFilesError());
			}

			@Override
			public void onSuccess(ListResult<FileVersionDTO> result) {
				view.setTransferFilesAnchorEnabled(true);
				for(final FileVersionDTO fileVersionDTO : result.getList()) {
					injector.getTransfertManager().cache(fileVersionDTO);
				}
			}
		});
	}
}
