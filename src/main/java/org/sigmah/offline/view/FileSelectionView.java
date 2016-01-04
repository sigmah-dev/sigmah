package org.sigmah.offline.view;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.offline.presenter.FileSelectionPresenter;
import org.sigmah.offline.presenter.TreeGridFileModel;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * {@link FileSelectionPresenter}'s view implementation.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FileSelectionView extends AbstractPopupView<PopupWidget> implements FileSelectionPresenter.View {
	
	private ContentPanel uploadPanel;
	private ContentPanel downloadPanel;
	
	private Button cancelButton;
	private Button transferFilesButton;
	
	private Map<Integer, TreeGridFileModel> parents;
	
	/**
	 * Popup's initialization.
	 */
	public FileSelectionView() {
		super(new PopupWidget(true), 620); // 800
	}

	@Override
	public void initialize() {
		this.parents = new HashMap<Integer, TreeGridFileModel>();
		
		this.uploadPanel = createGridPanel(
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferPopupUploads(), 
			IconImageBundle.ICONS.right());
		
		this.downloadPanel = createGridPanel(
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferPopupDownloads(), 
			IconImageBundle.ICONS.left());
		
		final LayoutContainer container = new LayoutContainer(new RowLayout(Style.Orientation.VERTICAL));
		container.add(uploadPanel);
		container.add(downloadPanel);
		
		this.cancelButton = Forms.button(I18N.CONSTANTS.cancel());
		this.transferFilesButton = Forms.button(I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferButtonTransferFiles(),
			IconImageBundle.ICONS.transfer());
		
		getPopup().addButton(cancelButton);
		getPopup().addButton(transferFilesButton);
		
		initPopup(container);
	}

	@Override
	public TreeStore<TreeGridFileModel> getUploadStore() {
		return getUploadGrid().getTreeStore();
	}

	@Override
	public TreeStore<TreeGridFileModel> getDownloadStore() {
		return getDownloadGrid().getTreeStore();
	}
	
	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public Button getTransferFilesButton() {
		return transferFilesButton;
	}

	@Override
	public GridSelectionModel<TreeGridFileModel> getUploadSelectionModel() {
		return getUploadGrid().getSelectionModel();
	}

	@Override
	public GridSelectionModel<TreeGridFileModel> getDownloadSelectionModel() {
		return getDownloadGrid().getSelectionModel();
	}
	
	@Override
	public void addFileVersionToUploadGrid(FileVersionDTO fileVersion, EntityDTO<Integer> parent) {
		add(getUploadGrid(), fileVersion, parent);
	}
	
	@Override
	public void addFileVersionToDownloadGrid(FileVersionDTO fileVersion, EntityDTO<Integer> parent) {
		add(getDownloadGrid(), fileVersion, parent);
	}
	
	private void add(TreeGrid<TreeGridFileModel> grid, FileVersionDTO fileVersion, EntityDTO<Integer> parent) {
		TreeGridFileModel parentModel = parents.get(parent.getId());
		
		if(parentModel == null) {
			parentModel = new TreeGridFileModel(parent);
			parents.put(parent.getId(), parentModel);
			
			grid.getTreeStore().add(parentModel, false);
		}
		
		final TreeGridFileModel fileVersionModel = new TreeGridFileModel(fileVersion);
		grid.getTreeStore().add(parentModel, fileVersionModel, false);
	}

	@Override
	public void selectAndExpandAll() {
		getUploadGrid().expandAll();
		getDownloadGrid().expandAll();
		
		getUploadGrid().getSelectionModel().selectAll();
		getDownloadGrid().getSelectionModel().selectAll();
	}
	
	@Override
	public void setUploadSelectedFileSize(long totalSize) {
		getStatus(uploadPanel).setText(I18N.MESSAGES.sigmahOfflinePrepareOfflineFileTransferTotalUploadSize(sizeToString(totalSize)));
	}

	@Override
	public void setDownloadSelectedFileSize(long totalSize) {
		getStatus(downloadPanel).setText(I18N.MESSAGES.sigmahOfflinePrepareOfflineFileTransferTotalDownloadSize(sizeToString(totalSize)));
	}

	@Override
	public void clear() {
		getUploadStore().removeAll();
		getDownloadStore().removeAll();
		// BUGFIX #698
		parents.clear();
	}
	
	@Override
	public TreeGrid<TreeGridFileModel> getUploadGrid() {
		return (TreeGrid<TreeGridFileModel>) uploadPanel.getWidget(0);
	}
	
	@Override
	public TreeGrid<TreeGridFileModel> getDownloadGrid() {
		return (TreeGrid<TreeGridFileModel>) downloadPanel.getWidget(0);
	}
	
	private Status getStatus(ContentPanel contentPanel) {
		return (Status) ((ToolBar) contentPanel.getBottomComponent()).getWidget(0);
	}
	
	private ContentPanel createGridPanel(String title, AbstractImagePrototype icon) {
		final TreeStore<TreeGridFileModel> store = new TreeStore<TreeGridFileModel>();
		
		final CheckBoxSelectionModel<TreeGridFileModel> selectionModel = createSelectionModel(store);
		
		final TreeGrid<TreeGridFileModel> grid = new TreeGrid<TreeGridFileModel>(store, createColumnModel(selectionModel));
		grid.setSelectionModel(selectionModel);
		grid.addPlugin(selectionModel);
		grid.getView().setForceFit(true);
		
		final ToolBar bottomBar = new ToolBar();
		bottomBar.add(new Status());
		
		final ContentPanel panel = Panels.content(title);
		panel.setIcon(icon);
		panel.setHeight(200);
		panel.add(grid);
		panel.setBottomComponent(bottomBar);
		
		return panel;
	}
	
	private CheckBoxSelectionModel<TreeGridFileModel> createSelectionModel(final TreeStore<TreeGridFileModel> store) {
		final CheckBoxSelectionModel<TreeGridFileModel> selectionModel = new CheckBoxSelectionModel<TreeGridFileModel>();
		
		selectionModel.addListener(Events.BeforeSelect, new Listener<SelectionEvent<TreeGridFileModel>>() {

			@Override
			public void handleEvent(SelectionEvent<TreeGridFileModel> be) {
				final TreeGridFileModel model = be.getModel();
				if(model.getChildren() != null) {
					selectionModel.select(store.getChildren(model), true);
				}
			}
		});
		
		return selectionModel;
	}
	
	private ColumnModel createColumnModel(CheckBoxSelectionModel<TreeGridFileModel> selectionModel) {
		
		// Project / org unit / file name column.
		final ColumnConfig nameColumnConfig = new ColumnConfig(TreeGridFileModel.NAME, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnName(), 200);
		nameColumnConfig.setRenderer(new TreeGridCellRenderer());
		
		// Last modification date column.
		final ColumnConfig lastModificationColumnConfig = new ColumnConfig(TreeGridFileModel.LAST_MODIFICATION, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnLastModification(), 85);
		
		// Author full name column.
		final ColumnConfig authorColumnConfig = new ColumnConfig(TreeGridFileModel.AUTHOR, I18N.CONSTANTS.flexibleElementFilesListAuthor(), 100);
		
		// Size column.
		final ColumnConfig sizeColumnConfig = new ColumnConfig(TreeGridFileModel.SIZE, I18N.CONSTANTS.flexibleElementFilesListSize(), 85);
		
		return new ColumnModel(Arrays.asList(
			selectionModel.getColumn(),
			nameColumnConfig,
			lastModificationColumnConfig,
			authorColumnConfig,
			sizeColumnConfig
		));
	}
	
	private String sizeToString(long size) {
		final FilesListElementDTO.Size converter = FilesListElementDTO.Size.convertToBestUnit(new FilesListElementDTO.Size(size, FilesListElementDTO.Size.SizeUnit.BYTE));
		return Math.round(converter.getSize()) + " " + FilesListElementDTO.Size.SizeUnit.getTranslation(converter.getUnit());
	}
}
