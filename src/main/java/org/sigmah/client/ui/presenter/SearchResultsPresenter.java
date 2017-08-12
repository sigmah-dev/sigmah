package org.sigmah.client.ui.presenter;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.SearchResultsView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.file.Cause;
import org.sigmah.shared.file.ProgressListener;
import org.sigmah.shared.file.TransfertManager;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Search Results page presenter.
 * 
 * 
 */

public class SearchResultsPresenter extends AbstractPagePresenter<SearchResultsPresenter.View> {

	/**
	 * View interface.
	 * 
	 */
	
	@Inject
	private TransfertManager transfertManager;

	
	public static interface SearchResultsClickHandler{
    }
	
	public static interface ProjectResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer projectId);
    }
	
	public static interface ContactResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer contactId);
    }
	
	public static interface OrgUnitResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer orgUnitId);
    }
	
	public static interface FilesResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(FileVersionDTO fv);
    }
	
	@ImplementedBy(SearchResultsView.class)
	public interface View extends ViewInterface {
		
		void setSearchString(String searchText);

		boolean addSearchData(Object searchData);

		void addResultsPanel();

		ContentPanel getSearchResultsPanel();

		void setProjectClickHandler(ProjectResultsClickHandler handler);

		void setContactClickHandler(ContactResultsClickHandler handler);

		void setOrgUnitClickHandler(OrgUnitResultsClickHandler handler);
		
		void setFileClickHandler(FilesResultsClickHandler handler);
	
	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *            Presenter's view interface.
	 * @param injector
	 *            Injected client injector.
	 */
	@Inject
	public SearchResultsPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.SEARCH_RESULTS;
	}

	@Override
	public void onPageRequest(PageRequest request) {
		String title = request.getData(RequestParameter.TITLE).toString();
		view.setSearchString(title);
		view.setProjectClickHandler(new ProjectResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer projectId) {
				PageRequest request = new PageRequest(Page.PROJECT_DASHBOARD);
				request.addParameter(RequestParameter.ID, projectId );
				eventBus.navigateRequest(request);
			}
		});
		view.setContactClickHandler(new ContactResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer contactId) {
				PageRequest request = new PageRequest(Page.CONTACT_DASHBOARD);
				request.addParameter(RequestParameter.ID, contactId );
				eventBus.navigateRequest(request);
			}
		});
		view.setOrgUnitClickHandler(new OrgUnitResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer orgUnitId) {
				PageRequest request = new PageRequest(Page.ORGUNIT_DASHBOARD);
				request.addParameter(RequestParameter.ID, orgUnitId );
				eventBus.navigateRequest(request);
			}
		});
		view.setFileClickHandler(new FilesResultsClickHandler() {
			@Override
			public void onLabelClickEvent(final FileVersionDTO fv){
				transfertManager.canDownload(fv, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						N10N.error("Unable to download!", "Check if the file exists. You may not have access to this file!");
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							transfertManager.download(fv, new ProgressListener() {

								@Override
								public void onProgress(double progress, double speed) {
								}

								@Override
								public void onFailure(Cause cause) {
									N10N.error("Unable to download!", "Check if the file exists. You may not have access to this file!");
								}

								@Override
								public void onLoad(String result) {
								}
							});
						} else {
							N10N.error("Unable to download!", "Check if the file exists. You may not have access to this file!");
						}
					}
				});
			};
		});
		
		if( view.addSearchData(request.getData(RequestParameter.CONTENT)) ){
			view.addResultsPanel();
		}else{
			
		}
	}

}
