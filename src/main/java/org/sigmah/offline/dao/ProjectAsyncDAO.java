package org.sigmah.offline.dao;

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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.sigmah.offline.indexeddb.Indexes;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionChoiceElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Asynchronous DAO for saving and loading <code>ProjectDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectAsyncDAO extends AbstractUserDatabaseAsyncDAO<ProjectDTO, ProjectJS> {
	
	@Inject
	private ProjectModelAsyncDAO projectModelAsyncDAO;
	
	@Inject
	private OrgUnitAsyncDAO orgUnitAsyncDAO;
	
	@Inject
	private PhaseAsyncDAO phaseAsyncDAO;
	
	@Inject
	private LogFrameAsyncDAO logFrameAsyncDAO;
	
	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;
	
	@Inject
	private ValueAsyncDAO valueAsyncDAO;

	@Override
	public void saveOrUpdate(final ProjectDTO t, final AsyncCallback<ProjectDTO> callback, Transaction<Store> transaction) {
		super.saveOrUpdate(t, callback, transaction);
        
		// Saving the project model
		projectModelAsyncDAO.saveOrUpdate(t.getProjectModel(), null, transaction);
		
		// Saving phases
		phaseAsyncDAO.saveAll(t.getPhases(), null, transaction);
		
		// Saving the log frame
		logFrameAsyncDAO.saveOrUpdate(t.getLogFrame(), null, transaction);
		
		// Saving the monitored points and reminders
		monitoredPointAsyncDAO.saveOrUpdate(t.getPointsList(), transaction);
		reminderAsyncDAO.saveOrUpdate(t.getRemindersList(), transaction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<ProjectDTO> callback, final Transaction<Store> transaction) {
		get(id, true, callback, transaction);
	}
	
	private void get(final int id, final boolean loadChildren, final AsyncCallback<ProjectDTO> callback, final Transaction<Store> transaction) {
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
				if (projectJS != null) {
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
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
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
	
	/**
	 * Retrieves only the informations stored inside ProjectJS.
	 * 
	 * @param indexName
	 *			Name of the index to use.
	 * @param id
	 *			Indexed value to retrieve.
	 * @param callback
	 *			Handler to call when the search is done.
	 */
	public void getByIndexWithoutDependencies(final String indexName, final int id, final AsyncCallback<ProjectDTO> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore projectStore = transaction.getObjectStore(getRequiredStore());
				
				projectStore.index(indexName).get(id).addCallback(new AsyncCallback<Request>() {

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
	
	/**
	 * Returns the 2 projects associated with the given project funding id.
	 * 
	 * @param id
	 *			Identifier of the project funding.
	 * @param callback
	 *			Handler to call when the search is done.
	 */
	public void getByProjectFundingId(final int id, final AsyncCallback<List<ProjectDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				getProjectsByIndex(Indexes.PROJECT_PROJECTFUNDINGS, id, callback, transaction);
			}
		});
	}
	
	private void getProjectsByIndex(final String index, final int id, final AsyncCallback<List<ProjectDTO>> callback, final Transaction<Store> transaction) {
		final List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
		final RequestManager<List<ProjectDTO>> requestManager = new RequestManager<List<ProjectDTO>>(projects, callback);
				
		final ObjectStore projectStore = transaction.getObjectStore(getRequiredStore());
		
		final OpenCursorRequest request = projectStore.index(index).openKeyCursor(IDBKeyRange.only(id));
		final int cursorRequest = requestManager.prepareRequest();
		request.addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				requestManager.setRequestFailure(cursorRequest, caught);
			}

			@Override
			public void onSuccess(Request result) {
				final Cursor cursor = request.getResult();
				if (cursor != null) {
					final ProjectJS projectJS = cursor.getValue();
					if (projectJS != null) {
						final ProjectDTO projectDTO = projectJS.toDTO();
						projects.add(projectDTO);

						loadProjectDTO(projectJS, true, requestManager, projectDTO, transaction);
					} else {
						get(cursor.getPrimaryKey(), new RequestManagerCallback<List<ProjectDTO>, ProjectDTO>(requestManager) {
							
							@Override
							public void onRequestSuccess(final ProjectDTO project) {
								if (project != null) {
									projects.add(project);
								}
							}
							
						}, transaction);
					}
					cursor.next();
				} else {
					requestManager.setRequestSuccess(cursorRequest);
				}
			}

		});
		
		requestManager.ready();
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
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(Transaction<Store> transaction) {
                final ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>();
				
				final ListResult<ProjectDTO> projectListResult = new ListResult<ProjectDTO>(projects);
				
				final RequestManager<ListResult<ProjectDTO>> requestManager = new RequestManager<ListResult<ProjectDTO>>(projectListResult, callback);
				
				for(int id : ids) {
					get(id, new RequestManagerCallback<ListResult<ProjectDTO>, ProjectDTO>(requestManager) {
						@Override
						public void onRequestSuccess(ProjectDTO result) {
							projects.add(result);
							projectListResult.setSize(projects.size());
						}
					}, transaction);
				}
				
				requestManager.ready();
            }
        });
	}
	
	public void getProjectsByOrgUnits(final Collection<Integer> orgUnitsIds, final AsyncCallback<ListResult<ProjectDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(final Transaction<Store> transaction) {
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
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(Transaction<Store> transaction) {
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
			final ProjectDTO projectDTO, final Transaction<Store> transaction) {
		
		// Loading categories
		final int categoriesRequest = requestManager.prepareRequest();
			
		final RequestManager<ProjectDTO> categoryRequestManager = new RequestManager<ProjectDTO>(projectDTO, new RequestManagerCallback<M, ProjectDTO>(requestManager) {
			@Override
			public void onRequestSuccess(final ProjectDTO project) {
				project.setCategoryElements(new HashSet<CategoryElementDTO>());
				
				for(final FlexibleElementDTO element : project.getProjectModel().getAllElements()) {
					if(element.getElementType() == ElementTypeEnum.QUESTION && 
						((QuestionElementDTO)element).getCategoryType() != null) {
						valueAsyncDAO.get(ValueJSIdentifierFactory.toIdentifier(element.getEntityName(), projectDTO.getId(), element.getId(), null), new RequestManagerCallback<M, ValueResult>(requestManager) {
							
							@Override
							public void onRequestSuccess(ValueResult result) {
								project.getCategoryElements().addAll(getCategoryElements(result, (QuestionElementDTO) element));
							}
						});
					}
				}
				requestManager.setRequestSuccess(categoriesRequest);
			}
		});
		final int projectModelRequest = categoryRequestManager.prepareRequest();
		categoryRequestManager.ready();
		
		// Loading project model
		projectModelAsyncDAO.get(projectJS.getProjectModel(), new RequestManagerCallback<M, ProjectModelDTO>(requestManager) {
			@Override
			public void onRequestSuccess(ProjectModelDTO result) {
				projectDTO.setProjectModel(result);
                projectDTO.setVisibilities(result.getVisibilities());
				
				categoryRequestManager.setRequestSuccess(projectModelRequest);
				
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
		if (projectJS.getPhases() != null) {
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

        if (Values.isDefined(projectJS, "currentPhase")) {
            phaseAsyncDAO.get(projectJS.getCurrentPhase(), new RequestManagerCallback<M, PhaseDTO>(requestManager) {
                @Override
                public void onRequestSuccess(PhaseDTO result) {
                    projectDTO.setCurrentPhase(result);
                    projectDTO.setCurrentPhaseName(result.getPhaseModel().getName());
                }
            }, transaction);
        }
		
		// Loading log frame
        if (Values.isDefined(projectJS, "logFrame")) {
            logFrameAsyncDAO.get(projectJS.getLogFrame(), new RequestManagerCallback<M, LogFrameDTO>(requestManager) {
                @Override
                public void onRequestSuccess(LogFrameDTO result) {
                    projectDTO.setLogFrame(result);
                }
            }, transaction);
        }

		// Loading monitored points and reminders
		if (projectDTO.getPointsList() != null && projectDTO.getPointsList().getId() != null) {
			monitoredPointAsyncDAO.getAllByParentListId(projectDTO.getPointsList().getId(), new RequestManagerCallback<M, List<MonitoredPointDTO>>(requestManager) {
				
				@Override
				public void onRequestSuccess(List<MonitoredPointDTO> result) {
					projectDTO.getPointsList().setPoints(result);
				}
			}, transaction);
		}
		
		if (projectDTO.getRemindersList() != null && projectDTO.getRemindersList().getId() != null) {
			reminderAsyncDAO.getAllByParentListId(projectDTO.getRemindersList().getId(), new RequestManagerCallback<M, List<ReminderDTO>>(requestManager) {
				
				@Override
				public void onRequestSuccess(List<ReminderDTO> result) {
					projectDTO.getRemindersList().setReminders(result);
				}
			}, transaction);
		}
		
		// Loading OrgUnit name.
        if (Values.isDefined(projectJS, "orgUnit")) {
            orgUnitAsyncDAO.get(projectJS.getOrgUnit(), new RequestManagerCallback<M, OrgUnitDTO>(requestManager) {
                @Override
                public void onRequestSuccess(OrgUnitDTO result) {
					if (result != null) {
						projectDTO.setOrgUnitName(result.getName() + " - " + result.getFullName());
					}
                }
            }, transaction);
        }
	}
	
	private Set<CategoryElementDTO> getCategoryElements(ValueResult valueResult, QuestionElementDTO questionElement) {
		if (valueResult != null && valueResult.isValueDefined()) {
			if(questionElement.getMultiple() == null || !questionElement.getMultiple()) {
				final String idChoice = valueResult.getValueObject();

				for (final QuestionChoiceElementDTO choice : questionElement.getChoices()) {
					if (idChoice.equals(String.valueOf(choice.getId()))) {
						return Collections.<CategoryElementDTO>singleton(choice.getCategoryElement());
					}
				}
			} else {
				final Set<Integer> selectedChoicesId = new HashSet<Integer>(ValueResultUtils.splitValuesAsInteger(valueResult.getValueObject()));
				
				final Set<CategoryElementDTO> elements = new HashSet<CategoryElementDTO>();
				
				for (final QuestionChoiceElementDTO choice : questionElement.getChoices()) {
					if (selectedChoicesId.contains(choice.getId())) {
						elements.add(choice.getCategoryElement());
					}
				}

				return elements;
			}
		}
		return Collections.<CategoryElementDTO>emptySet();
	}
	
	private <M> void loadProjectFundings(final JsArray<ProjectFundingJS> projectFundings, final List<ProjectFundingDTO> dtos,
			final RequestManager<M> requestManager, final Transaction<Store> transaction) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.PROJECT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BaseAsyncDAO<Store>> getDependencies() {
		final ArrayList<BaseAsyncDAO<Store>> list = new ArrayList<BaseAsyncDAO<Store>>();
		list.add(projectModelAsyncDAO);
		list.add(orgUnitAsyncDAO);
		list.add(phaseAsyncDAO);
		list.add(logFrameAsyncDAO);
		list.add(monitoredPointAsyncDAO);
		list.add(reminderAsyncDAO);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectJS toJavaScriptObject(ProjectDTO t) {
		return ProjectJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectDTO toJavaObject(ProjectJS js) {
		return js.toDTO();
	}
	
}
