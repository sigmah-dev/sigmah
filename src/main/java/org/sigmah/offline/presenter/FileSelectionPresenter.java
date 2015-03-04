package org.sigmah.offline.presenter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
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
import org.sigmah.offline.view.FileSelectionView;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.HasProgressListeners;
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
	
		Button getCancelButton();
		Button getTransferFilesButton();
		
	}
	
	@Inject
	private TransfertAsyncDAO transfertAsyncDAO;
	
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
				pushAndPullFiles();
				hideView();
			}
		});
	}
	
	@Override
	public void onPageRequest(PageRequest request) {
		transfertAsyncDAO.getAll(TransfertType.UPLOAD, new AsyncCallback<TransfertJS>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("An error occured while searching for not uploaded files.", caught);
			}

			@Override
			public void onSuccess(TransfertJS result) {
				view.getUploadStore().add(result.getFileVersion().toDTO());
			}
		});
		
		dispatch.execute(new GetFilesFromFavoriteProjects(), new AsyncCallback<ListResult<FileVersionDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				N10N.error(I18N.CONSTANTS.offlineActionTransferFilesListFilesError());
			}

			@Override
			public void onSuccess(ListResult<FileVersionDTO> result) {
				view.getDownloadStore().add(result.getList());
			}
		});
	}
	
	@Deprecated
	private void pushAndPullFiles() {

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
				N10N.error(I18N.CONSTANTS.offlineActionTransferFilesListFilesError());
			}

			@Override
			public void onSuccess(ListResult<FileVersionDTO> result) {
				for(final FileVersionDTO fileVersionDTO : result.getList()) {
					injector.getTransfertManager().cache(fileVersionDTO);
				}
			}
		});
		
	}
	
}
