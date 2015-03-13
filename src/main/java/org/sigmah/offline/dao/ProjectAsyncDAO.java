package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ProjectFundingJS;
import org.sigmah.offline.js.ProjectJS;
import org.sigmah.offline.js.Values;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectAsyncDAO extends AbstractAsyncDAO<ProjectDTO> {
	
	private final ProjectModelAsyncDAO projectModelAsyncDAO;
	private final OrgUnitAsyncDAO orgUnitAsyncDAO;
	private final PhaseAsyncDAO phaseAsyncDAO;
	private final LogFrameAsyncDAO logFrameAsyncDAO;
	private final MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	private final ReminderAsyncDAO reminderAsyncDAO;

	@Inject
	public ProjectAsyncDAO(ProjectModelAsyncDAO projectModelAsyncDAO, 
			OrgUnitAsyncDAO orgUnitAsyncDAO, PhaseAsyncDAO phaseAsyncDAO, 
			LogFrameAsyncDAO logFrameAsyncDAO, 
			MonitoredPointAsyncDAO monitoredPointAsyncDAO,
			ReminderAsyncDAO reminderAsyncDAO) {
		this.projectModelAsyncDAO = projectModelAsyncDAO;
		this.orgUnitAsyncDAO = orgUnitAsyncDAO;
		this.phaseAsyncDAO = phaseAsyncDAO;
		this.logFrameAsyncDAO = logFrameAsyncDAO;
		this.monitoredPointAsyncDAO = monitoredPointAsyncDAO;
		this.reminderAsyncDAO = reminderAsyncDAO;
	}
	
	@Override
	public void saveOrUpdate(final ProjectDTO t, final AsyncCallback<ProjectDTO> callback, Transaction transaction) {
		final ObjectStore projectStore = transaction.getObjectStore(Store.PROJECT);
		
		final ProjectJS projectJS = ProjectJS.toJavaScript(t);
		projectStore.put(projectJS).addCallback(new AsyncCallback<Request>() {
        
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving project " + projectJS.getId() + '.', caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Project " + projectJS.getId() + " has been successfully saved.");
                if(callback != null) {
                    callback.onSuccess(t);
                }
            }
        });
        
		// Saving the project model
		projectModelAsyncDAO.saveOrUpdate(t.getProjectModel(), null, transaction);
		
		// Saving phases
		phaseAsyncDAO.saveOrUpdate(t.getPhases(), transaction);
		
		// Saving the log frame
		logFrameAsyncDAO.saveOrUpdate(t.getLogFrame(), null, transaction);
		
		// Saving the monitored points and reminders
		monitoredPointAsyncDAO.saveOrUpdate(t.getPointsList(), transaction);
		reminderAsyncDAO.saveOrUpdate(t.getRemindersList(), transaction);
	}

	@Override
	public void get(final int id, final AsyncCallback<ProjectDTO> callback, final Transaction transaction) {
		get(id, true, callback, transaction);
	}
	
	private void get(final int id, final boolean loadChildren, final AsyncCallback<ProjectDTO> callback, final Transaction transaction) {
		if(loadChildren && transaction.useObjectFromCache(ProjectDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore projectStore = transaction.getObjectStore(Store.PROJECT);

		projectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final ProjectJS projectJS = request.getResult();
				if(projectJS != null) {
					final ProjectDTO projectDTO = projectJS.toDTO();

					final RequestManager<ProjectDTO> requestManager = new RequestManager<ProjectDTO>(projectDTO, callback);
					loadProjectDTO(projectJS, loadChildren, requestManager, projectDTO, transaction);
					requestManager.ready();
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}
	
	/**
	 * Retrieves only the informations stored inside ProjectJS.
	 * 
	 * @param id Identifier of the project to retrieve.
	 * @param callback Handler to call when the search is done.
	 */
	public void getWithoutDependencies(final int id, final AsyncCallback<ProjectDTO> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				final ObjectStore projectStore = transaction.getObjectStore(getRequiredStore());
				
				projectStore.get(id).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request request) {
						callback.onSuccess(request.getResult() != null ? request.<ProjectJS>getResult().toDTO() : null);
					}
				});
			}
		});
	}
	
	private void setChildrenProjects(final ProjectDTO project) {
		final ArrayList<ProjectDTO> children = new ArrayList<ProjectDTO>();

		// Maps the funding projects.
		if (project.getFunding() != null) {
			for (final ProjectFundingDTO funding : project.getFunding()) {
				final ProjectDTO projectFunding = funding.getFunding();
				if(projectFunding != null) {
					children.add(projectFunding);
				}
			}
		}

		// Maps the funded projects.
		if (project.getFunded() != null) {
			for (final ProjectFundingDTO funded : project.getFunded()) {
				final ProjectDTO projectFunded = funded.getFunded();
				if (projectFunded != null) {
					children.add(projectFunded);
				}
			}
		}

		project.setChildrenProjects(children);
	}
	
	public void getProjectsByIds(final Collection<Integer> ids, final AsyncCallback<ListResult<ProjectDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>();
				
				final ListResult<ProjectDTO> projectListResult = new ListResult<ProjectDTO>(projects);
				
				final RequestManager<ListResult<ProjectDTO>> requestManager = new RequestManager<ListResult<ProjectDTO>>(projectListResult, callback);
				
				for(int id : ids) {
					get(id, new RequestManagerCallback<ListResult<ProjectDTO>, ProjectDTO>(requestManager) {
						@Override
						public void onRequestSuccess(ProjectDTO result) {
							projects.add(result);
						}
					}, transaction);
				}
				
				requestManager.ready();
            }
        });
	}
	
	public void getProjectsByOrgUnits(final Collection<Integer> orgUnitsIds, final AsyncCallback<ListResult<ProjectDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(final Transaction transaction) {
                final ObjectStore projectStore = transaction.getObjectStore(Store.PROJECT);
				final Index orgUnitIndex = projectStore.index("orgUnit");
				
				final int size = orgUnitsIds.size();
				final int[] requests = new int[] {0};
				
				final ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>();
				final ListResult<ProjectDTO> projectListResult = new ListResult<ProjectDTO>(projects);
									
				final RequestManager<ListResult<ProjectDTO>> requestManager = new RequestManager<ListResult<ProjectDTO>>(projectListResult, callback);
				
				for(final Integer orgUnitId : orgUnitsIds) {
					final OpenCursorRequest openCursorRequest = orgUnitIndex.openCursor(IDBKeyRange.only(orgUnitId.intValue()));
					
                    openCursorRequest.addCallback(new RequestManagerCallback<ListResult<ProjectDTO>, Request>(requestManager) {

                        @Override
                        public void onRequestSuccess(Request request) {
                            final Cursor cursor = openCursorRequest.getResult();
							if(cursor != null) {
								final ProjectJS projectJS = (ProjectJS) cursor.getValue();
								final ProjectDTO projectDTO = projectJS.toDTO();
								
								final RequestManager<ProjectDTO> projectDTORequestManager = new RequestManager<ProjectDTO>(projectDTO, new RequestManagerCallback<ListResult<ProjectDTO>, ProjectDTO>(requestManager) {
									@Override
									public void onRequestSuccess(final ProjectDTO project) {
                                        projects.add(project);
									}
								});
								
								loadProjectDTO(projectJS, true, projectDTORequestManager, projectDTO, transaction);
								projectDTORequestManager.ready();
								
								cursor.next();
								
							} else {
								requests[0]++;
								if(requests[0] == size) {
									requestManager.ready();
								}
							}
                        }
                    });
				}
            }
        });
	}
	
	public void getIdsByOrgUnits(final Collection<Integer> orgUnitsIds, final AsyncCallback<ListResult<ProjectDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ObjectStore projectStore = transaction.getObjectStore(Store.PROJECT);
				final Index orgUnitIndex = projectStore.index("orgUnit");
				
				final int size = orgUnitsIds.size();
				final int[] requests = new int[] {0};
				
				final ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>();
				
				for(final Integer orgUnitId : orgUnitsIds) {
					final OpenCursorRequest openCursorRequest = orgUnitIndex.openCursor(IDBKeyRange.only(orgUnitId.intValue()));
					
                    openCursorRequest.addCallback(new AsyncCallback<Request>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }

                        @Override
                        public void onSuccess(Request request) {
                            final Cursor cursor = openCursorRequest.getResult();
							if(cursor != null) {
								final ProjectJS projectJS = (ProjectJS) cursor.getValue();
								projects.add(projectJS.toDTO());
								
								cursor.next();
								
							} else {
								requests[0]++;
								if(requests[0] == size) {
									final ListResult<ProjectDTO> projectListResult = new ListResult<ProjectDTO>(projects);
									callback.onSuccess(projectListResult);
								}
							}
                        }
                    });
				}
            }
        });
	}
	
	private <M> void loadProjectDTO(final ProjectJS projectJS, final boolean loadChildren, final RequestManager<M> requestManager, 
			final ProjectDTO projectDTO, final Transaction transaction) {
		// Loading project model
		projectModelAsyncDAO.get(projectJS.getProjectModel(), new RequestManagerCallback<M, ProjectModelDTO>(requestManager) {
			@Override
			public void onRequestSuccess(ProjectModelDTO result) {
				projectDTO.setProjectModel(result);
                projectDTO.setVisibilities(result.getVisibilities());
				
				if(loadChildren) {
					transaction.getObjectCache().put(projectDTO.getId(), projectDTO);

					final RequestManager<Void> fundedsAndFundingsRequestManager = new RequestManager<Void>(null, new RequestManagerCallback<M, Void>(requestManager) {

						@Override
						public void onRequestSuccess(Void result) {
							// Sets children projects.
							setChildrenProjects(projectDTO);
						}
					});

					// Loading related projects
					final JsArray<ProjectFundingJS> fundeds = projectJS.getFunded();
					if(fundeds != null) {
						final ArrayList<ProjectFundingDTO> dtos = new ArrayList<ProjectFundingDTO>();
						projectDTO.setFunded(dtos);

						loadProjectFundings(fundeds, dtos, fundedsAndFundingsRequestManager, transaction);
					}

					final JsArray<ProjectFundingJS> fundings = projectJS.getFunding();
					if(fundings != null) {
						final ArrayList<ProjectFundingDTO> dtos = new ArrayList<ProjectFundingDTO>();
						projectDTO.setFunding(dtos);

						loadProjectFundings(fundings, dtos, fundedsAndFundingsRequestManager, transaction);
					}

					fundedsAndFundingsRequestManager.ready();
				}
			}
		}, transaction);

		// Loading phases
		if(projectJS.getPhases() != null) {
			final ArrayList<PhaseDTO> phases = new ArrayList<PhaseDTO>();
			projectDTO.setPhases(phases);

			final JsArrayInteger phaseIds = projectJS.getPhases();
			final int size = phaseIds.length();

			for(int index = 0; index < size; index++) {
				phaseAsyncDAO.get(phaseIds.get(index), new RequestManagerCallback<M, PhaseDTO>(requestManager) {
					@Override
					public void onRequestSuccess(PhaseDTO result) {
						result.setParentProject(projectDTO);
						phases.add(result);
					}
				}, transaction);
			}
		}

        if(Values.isDefined(projectJS, "currentPhase")) {
            phaseAsyncDAO.get(projectJS.getCurrentPhase(), new RequestManagerCallback<M, PhaseDTO>(requestManager) {
                @Override
                public void onRequestSuccess(PhaseDTO result) {
                    projectDTO.setCurrentPhase(result);
                    projectDTO.setCurrentPhaseName(result.getPhaseModel().getName());
                }
            }, transaction);
        }
		
		// Loading log frame
        if(Values.isDefined(projectJS, "logFrame")) {
            logFrameAsyncDAO.get(projectJS.getLogFrame(), new RequestManagerCallback<M, LogFrameDTO>(requestManager) {
                @Override
                public void onRequestSuccess(LogFrameDTO result) {
                    projectDTO.setLogFrame(result);
                }
            }, transaction);
        }

		// Loading monitored points and reminders
		if(projectDTO.getPointsList() != null && projectDTO.getPointsList().getId() != null) {
			monitoredPointAsyncDAO.getAllByParentListId(projectDTO.getPointsList().getId(), new RequestManagerCallback<M, List<MonitoredPointDTO>>(requestManager) {
				
				@Override
				public void onRequestSuccess(List<MonitoredPointDTO> result) {
					projectDTO.getPointsList().setPoints(result);
				}
			}, transaction);
		}
		
		if(projectDTO.getRemindersList() != null && projectDTO.getRemindersList().getId() != null) {
			reminderAsyncDAO.getAllByParentListId(projectDTO.getRemindersList().getId(), new RequestManagerCallback<M, List<ReminderDTO>>(requestManager) {
				
				@Override
				public void onRequestSuccess(List<ReminderDTO> result) {
					projectDTO.getRemindersList().setReminders(result);
				}
			}, transaction);
		}
		
		// Loading OrgUnit name.
        if(Values.isDefined(projectJS, "orgUnit")) {
            orgUnitAsyncDAO.get(projectJS.getOrgUnit(), new RequestManagerCallback<M, OrgUnitDTO>(requestManager) {
                @Override
                public void onRequestSuccess(OrgUnitDTO result) {
                    projectDTO.setOrgUnitName(result.getName() + " - " + result.getFullName());
                }
            }, transaction);
        }
	}
	
	private <M> void loadProjectFundings(final JsArray<ProjectFundingJS> projectFundings, final List<ProjectFundingDTO> dtos,
			final RequestManager<M> requestManager, final Transaction transaction) {
		for(int index = 0; index < projectFundings.length(); index++) {
			final ProjectFundingJS projectFundingJS = projectFundings.get(index);
			final ProjectFundingDTO dto = projectFundingJS.toDTO();
			dtos.add(dto);

			get(projectFundingJS.getFunded(), false, new RequestManagerCallback<M, ProjectDTO>(requestManager) {
				@Override
				public void onRequestSuccess(ProjectDTO result) {
					dto.setFunded(result);
				}
			}, transaction);

			get(projectFundingJS.getFunding(), false, new RequestManagerCallback<M, ProjectDTO>(requestManager) {
				@Override
				public void onRequestSuccess(ProjectDTO result) {
					dto.setFunding(result);
				}
			}, transaction);
		}
	}

	@Override
	public Store getRequiredStore() {
		return Store.PROJECT;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(projectModelAsyncDAO);
		list.add(orgUnitAsyncDAO);
		list.add(phaseAsyncDAO);
		list.add(logFrameAsyncDAO);
		list.add(monitoredPointAsyncDAO);
		list.add(reminderAsyncDAO);
		return list;
	}
}
