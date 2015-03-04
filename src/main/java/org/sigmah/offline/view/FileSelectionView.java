package org.sigmah.offline.view;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import java.util.Arrays;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.offline.presenter.FileSelectionPresenter;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * {@link FileSelectionPresenter}'s view implementation.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FileSelectionView extends AbstractPopupView<PopupWidget> implements FileSelectionPresenter.View {
	
	private static final String CONTAINER = "container";
	private static final String FILE_TYPE = "fileType";
	private static final String FILE_NAME = "fileName";
	private static final String LAST_MODIFICATION = "lastModification";
	private static final String AUTHOR = "author";
	private static final String SIZE = "size";
	
	private ContentPanel uploadPanel;
	private ContentPanel downloadPanel;
	
	private Button cancelButton;
	private Button transferFilesButton;
	
	/**
	 * Popup's initialization.
	 */
	public FileSelectionView() {
		super(new PopupWidget(true), 750);
	}

	@Override
	public void initialize() {
		this.uploadPanel = createGridPanel(
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferPopupUploads(), 
			IconImageBundle.ICONS.right(),
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferTotalUploadSize());
		
		this.downloadPanel = createGridPanel(
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferPopupDownloads(), 
			IconImageBundle.ICONS.left(),
			I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferTotalDownloadSize());
		
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
	public ListStore<FileVersionDTO> getUploadStore() {
		return getUploadGrid().getStore();
	}

	@Override
	public ListStore<FileVersionDTO> getDownloadStore() {
		return getDownloadGrid().getStore();
	}
	
	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public Button getTransferFilesButton() {
		return transferFilesButton;
	}
	
	private Grid<FileVersionDTO> getUploadGrid() {
		return (Grid<FileVersionDTO>) uploadPanel.getWidget(0);
	}
	
	private Grid<FileVersionDTO> getDownloadGrid() {
		return (Grid<FileVersionDTO>) downloadPanel.getWidget(0);
	}
	
	private ContentPanel createGridPanel(String title, AbstractImagePrototype icon, String statusText) {
		final CheckBoxSelectionModel<FileVersionDTO> selectionModel = new CheckBoxSelectionModel<FileVersionDTO>();
		
		final Grid<FileVersionDTO> grid = new Grid<FileVersionDTO>(new ListStore<FileVersionDTO>(), createColumnModel(selectionModel));
		grid.setSelectionModel(selectionModel);
		
		final Status status = new Status();
		status.setText(statusText);
		
		final ToolBar bottomBar = new ToolBar();
		bottomBar.add(status);
		
		final ContentPanel panel = Panels.content(title);
		panel.setIcon(icon);
		panel.setHeight(200);
		panel.add(grid);
		panel.setBottomComponent(bottomBar);
		
		return panel;
	}
	
	private ColumnModel createColumnModel(CheckBoxSelectionModel<FileVersionDTO> selectionModel) {
		final ColumnConfig containerColumnConfig = new ColumnConfig(CONTAINER, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnContainer(), 150);
		final ColumnConfig fileTypeColumnConfig = new ColumnConfig(FILE_TYPE, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnType(), 150);
		final ColumnConfig fileNameColumnConfig = new ColumnConfig(FILE_NAME, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnName(), 200);
		final ColumnConfig lastModificationColumnConfig = new ColumnConfig(LAST_MODIFICATION, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnLastModification(), 50);
		final ColumnConfig authorColumnConfig = new ColumnConfig(AUTHOR, I18N.CONSTANTS.flexibleElementFilesListAuthor(), 100);
		final ColumnConfig sizeColumnConfig = new ColumnConfig(SIZE, I18N.CONSTANTS.flexibleElementFilesListSize(), 50);
		
		return new ColumnModel(Arrays.asList(
			selectionModel.getColumn(),
			containerColumnConfig,
			fileTypeColumnConfig,
			fileNameColumnConfig,
			lastModificationColumnConfig,
			authorColumnConfig,
			sizeColumnConfig
		));
	}
}
