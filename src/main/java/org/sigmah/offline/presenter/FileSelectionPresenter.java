package org.sigmah.offline.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.offline.view.FileSelectionView;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
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
		
		ListStore<FileVersionDTO> getUploadStore();
		ListStore<FileVersionDTO> getDownloadStore();
		GridSelectionModel<FileVersionDTO> getUploadSelectionModel();
		GridSelectionModel<FileVersionDTO> getDownloadSelectionModel();
		
		void addFileVersionToUploadGrid(FileVersionDTO fileVersion);
		void addFileVersionToDownloadGrid(FileVersionDTO fileVersion);
		void setUploadSelectedFileSize(long totalSize);
		void setDownloadSelectedFileSize(long totalSize);
	
		Button getCancelButton();
		Button getTransferFilesButton();
		
	}
	
	@Inject
	private TransfertAsyncDAO transfertAsyncDAO;
	
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
				
				for(final FileVersionDTO fileVersion : view.getUploadSelectionModel().getSelectedItems()) {
					totalSize += fileVersion.getSize();
				}
				
				view.setUploadSelectedFileSize(totalSize);
			}
		});
		
		view.getDownloadSelectionModel().addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				long totalSize = 0L;
				
				for(final FileVersionDTO fileVersion : view.getDownloadSelectionModel().getSelectedItems()) {
					totalSize += fileVersion.getSize();
				}
				
				view.setDownloadSelectedFileSize(totalSize);
			}
		});
	}
	
	@Override
	public void onPageRequest(PageRequest request) {
		view.getUploadSelectionModel().deselectAll();
		view.getDownloadSelectionModel().deselectAll();
		view.getUploadStore().removeAll();
		view.getDownloadStore().removeAll();
		
		transfertAsyncDAO.getAll(TransfertType.UPLOAD, new AsyncCallback<TransfertJS>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("An error occured while searching for not uploaded files.", caught);
			}

			@Override
			public void onSuccess(TransfertJS result) {
				view.addFileVersionToUploadGrid(result.getFileVersion().toDTO());
			}
		});
		
		dispatch.execute(new GetFilesFromFavoriteProjects(), new AsyncCallback<ListResult<FileVersionDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				N10N.error(I18N.CONSTANTS.offlineActionTransferFilesListFilesError());
			}

			@Override
			public void onSuccess(ListResult<FileVersionDTO> result) {
				for(final FileVersionDTO fileVersion : result.getList()) {
					if(fileVersion.isAvailable()) {
						transfertManager.isCached(fileVersion, new SuccessCallback<Boolean>() {

							@Override
							public void onSuccess(Boolean cached) {
								if(cached == null || !cached) {
									view.addFileVersionToDownloadGrid(fileVersion);
								}
							}
						});
					}
				}
			}
		});
	}
	
	private void transferSelectedFiles() {
		final List<FileVersionDTO> uploads = view.getUploadSelectionModel().getSelectedItems();
		
		for(final FileVersionDTO fileVersion : uploads) {
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
		
		final List<FileVersionDTO> downloads = view.getDownloadSelectionModel().getSelectedItems();
		for(final FileVersionDTO fileVersionDTO : downloads) {
			transfertManager.cache(fileVersionDTO);
		}
	}
	
}
