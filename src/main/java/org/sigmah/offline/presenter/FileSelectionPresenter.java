package org.sigmah.offline.presenter;

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

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.offline.view.FileSelectionView;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.HasProgressListeners;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.file.TransfertType;

/**
 * Presenter for the popup that allow the user to select which files he wants
 * to cache.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FileSelectionPresenter extends AbstractPagePresenter<FileSelectionPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(FileSelectionView.class)
	public static interface View extends ViewPopupInterface {
		
		TreeGrid<TreeGridFileModel> getUploadGrid();
		TreeGrid<TreeGridFileModel> getDownloadGrid();
		
		TreeStore<TreeGridFileModel> getUploadStore();
		TreeStore<TreeGridFileModel> getDownloadStore();
		GridSelectionModel<TreeGridFileModel> getUploadSelectionModel();
		GridSelectionModel<TreeGridFileModel> getDownloadSelectionModel();
		
		void addFileVersionToUploadGrid(FileVersionDTO fileVersion, EntityDTO<Integer> parent);
		void addFileVersionToDownloadGrid(FileVersionDTO fileVersion, EntityDTO<Integer> parent);
		void setUploadSelectedFileSize(long totalSize);
		void setDownloadSelectedFileSize(long totalSize);
		
		void selectAndExpandAll();
		void clear();
		
		Button getCancelButton();
		Button getTransferFilesButton();
		
	}
	
	@Inject
	private TransfertAsyncDAO transfertAsyncDAO;
	
	@Inject
	private ProjectAsyncDAO projectAsyncDAO;
	
	@Inject
	private OrgUnitAsyncDAO orgUnitAsyncDAO;
	
	@Inject
	private TransfertManager transfertManager;
	
	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by the presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected FileSelectionPresenter(final View view, final Injector injector) {
		super(view, injector);
	}
	
	@Override
	public Page getPage() {
		return Page.OFFLINE_SELECT_FILES;
	}

	@Override
	public void onBind() {
		setPageTitle(I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferPopup());
		
		view.getCancelButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hideView();
			}
		});
		
		view.getTransferFilesButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				transferSelectedFiles();
				hideView();
			}
		});
		
		view.getUploadSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				long totalSize = 0L;
				
				for(final TreeGridFileModel model : view.getUploadSelectionModel().getSelectedItems()) {
					if(model.getDTO() instanceof FileVersionDTO) {
						final FileVersionDTO fileVersion = (FileVersionDTO) model.getDTO();
						totalSize += fileVersion.getSize();
					}
				}
				
				view.setUploadSelectedFileSize(totalSize);
			}
		});
		
		view.getDownloadSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				long totalSize = 0L;
				
				for(final TreeGridFileModel model : view.getDownloadSelectionModel().getSelectedItems()) {
					if(model.getDTO() instanceof FileVersionDTO) {
						final FileVersionDTO fileVersion = (FileVersionDTO) model.getDTO();
						totalSize += fileVersion.getSize();
					}
				}
				
				view.setDownloadSelectedFileSize(totalSize);
			}
		});
	}
	
	@Override
	public void onPageRequest(PageRequest request) {
		view.clear();
		
		final Loadable[] loadables = new Loadable[] {
			new LoadingMask(view.getDownloadGrid()),
			new LoadingMask(view.getUploadGrid()),
			view.getTransferFilesButton()};
		
		final RequestManager<VoidResult> manager = new RequestManager<VoidResult>(null, new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				view.getUploadStore().commitChanges();
				view.getDownloadStore().commitChanges();
				
				view.selectAndExpandAll();
				
				for(final Loadable loadable : loadables) {
					loadable.setLoading(false);
				}
			}
		});
		
		// Find local files.
		final int uploadReady = manager.prepareRequest();
		
		transfertAsyncDAO.getAll(TransfertType.UPLOAD, new RequestManagerCallback<VoidResult, List<TransfertJS>>(manager) {

			@Override
			public void onRequestSuccess(List<TransfertJS> transfers) {
				for(final TransfertJS transfert : transfers) {
					addTransfer(transfert, manager);
				}
				
				manager.setRequestSuccess(uploadReady);
			}
		});
		
		// Find distant files.
		final int downloadReady = manager.prepareRequest();
		
		dispatch.execute(new GetFilesFromFavoriteProjects(), new RequestManagerCallback<VoidResult, ListResult<TreeGridFileModel>>(manager) {

			@Override
			public void onRequestSuccess(ListResult<TreeGridFileModel> result) {
				for(final TreeGridFileModel model : result.getList()) {
					for(final FileVersionDTO fileVersion : model.getChildren()) {
						transfertManager.isCached(fileVersion, new RequestManagerCallback<VoidResult, Boolean>(manager) {

							@Override
							public void onRequestSuccess(Boolean cached) {
								if(cached == null || !cached) {
									view.addFileVersionToDownloadGrid(fileVersion, model.getDTO());
								}
							}
						});
					}
				}
				
				manager.setRequestSuccess(downloadReady);
			}
		});
		
		for(final Loadable loadable : loadables) {
			loadable.setLoading(true);
		}
		manager.ready();
	}
	
	private <M> void addTransfer(TransfertJS transfert, RequestManager<M> manager) {
		final FileVersionDTO fileVersion = transfert.getFileVersion().toDTO();
				
		final String parentIdProperty = transfert.getProperties().get(FileUploadUtils.DOCUMENT_PROJECT);
		if(parentIdProperty == null) {
			Log.error("No parent for file version " + fileVersion.getId());
		}

		final int parentId = Integer.parseInt(parentIdProperty);

		projectAsyncDAO.getWithoutDependencies(parentId, new RequestManagerCallback<M, ProjectDTO>(manager) {

			@Override
			public void onRequestSuccess(ProjectDTO project) {
				if(project != null) {
					view.addFileVersionToUploadGrid(fileVersion, project);
				}
			}
		});

		orgUnitAsyncDAO.getWithoutDependencies(parentId, new RequestManagerCallback<M, OrgUnitDTO>(manager) {

			@Override
			public void onRequestSuccess(OrgUnitDTO orgUnit) {
				if(orgUnit != null) {
					view.addFileVersionToUploadGrid(fileVersion, orgUnit);
				}
			}
		});
	}
	
	private void transferSelectedFiles() {
		// Uploads
		final List<TreeGridFileModel> uploads = view.getUploadSelectionModel().getSelectedItems();
		
		for(final TreeGridFileModel model : uploads) {
			if(model.getDTO() instanceof FileVersionDTO) {
				final FileVersionDTO fileVersion = (FileVersionDTO) model.getDTO();
				
				transfertAsyncDAO.getByFileVersionId(fileVersion.getId(), new AsyncCallback<TransfertJS>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.error("An error occured while fetching the content of file version id " + fileVersion.getId() + '.', caught);
					}

					@Override
					public void onSuccess(TransfertJS result) {
						((HasProgressListeners)transfertManager).resumeUpload(result);
					}
				});
			}
		}
		
		// Downloads
		final List<TreeGridFileModel> downloads = view.getDownloadSelectionModel().getSelectedItems();
		
		for(final TreeGridFileModel model : downloads) {
			if(model.getDTO() instanceof FileVersionDTO) {
				final FileVersionDTO fileVersion = (FileVersionDTO) model.getDTO();
				transfertManager.cache(fileVersion);
			}
		}
		
		eventBus.updateZoneRequest(Zone.OFFLINE_BANNER.requestWith(RequestParameter.SHOW_BRIEFLY, true));
	}
	
}
