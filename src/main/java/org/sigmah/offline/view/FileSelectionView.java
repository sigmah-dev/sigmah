package org.sigmah.offline.view;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
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
import org.sigmah.client.util.DateUtils;
import org.sigmah.offline.presenter.FileSelectionPresenter;
import org.sigmah.shared.dto.element.FilesListElementDTO;
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
		super(new PopupWidget(true), 620); // 800
	}

	@Override
	public void initialize() {
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

	@Override
	public GridSelectionModel<FileVersionDTO> getUploadSelectionModel() {
		return getUploadGrid().getSelectionModel();
	}

	@Override
	public GridSelectionModel<FileVersionDTO> getDownloadSelectionModel() {
		return getDownloadGrid().getSelectionModel();
	}
	
	@Override
	public void addFileVersionToUploadGrid(FileVersionDTO fileVersion) {
		getUploadStore().add(fileVersion);
		getUploadGrid().getSelectionModel().select(fileVersion, true);
	}
	
	@Override
	public void addFileVersionToDownloadGrid(FileVersionDTO fileVersion) {
		getDownloadStore().add(fileVersion);
		getDownloadGrid().getSelectionModel().select(fileVersion, true);
	}

	@Override
	public void setUploadSelectedFileSize(long totalSize) {
		getStatus(uploadPanel).setText(I18N.MESSAGES.sigmahOfflinePrepareOfflineFileTransferTotalUploadSize(sizeToString(totalSize)));
	}

	@Override
	public void setDownloadSelectedFileSize(long totalSize) {
		getStatus(downloadPanel).setText(I18N.MESSAGES.sigmahOfflinePrepareOfflineFileTransferTotalDownloadSize(sizeToString(totalSize)));
	}
	
	private Grid<FileVersionDTO> getUploadGrid() {
		return (Grid<FileVersionDTO>) uploadPanel.getWidget(0);
	}
	
	private Grid<FileVersionDTO> getDownloadGrid() {
		return (Grid<FileVersionDTO>) downloadPanel.getWidget(0);
	}
	
	private Status getStatus(ContentPanel contentPanel) {
		return (Status) ((ToolBar) contentPanel.getBottomComponent()).getWidget(0);
	}
	
	private ContentPanel createGridPanel(String title, AbstractImagePrototype icon) {
		final CheckBoxSelectionModel<FileVersionDTO> selectionModel = new CheckBoxSelectionModel<FileVersionDTO>();
		
		final Grid<FileVersionDTO> grid = new Grid<FileVersionDTO>(new ListStore<FileVersionDTO>(), createColumnModel(selectionModel));
		grid.setSelectionModel(selectionModel);
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
	
	private ColumnModel createColumnModel(CheckBoxSelectionModel<FileVersionDTO> selectionModel) {
		// Project/orgunit column.
//		final ColumnConfig containerColumnConfig = new ColumnConfig(CONTAINER, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnContainer(), 140);
		
		// File type column.
//		final ColumnConfig fileTypeColumnConfig = new ColumnConfig(FILE_TYPE, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnType(), 140);
		
		// File name column.
		final ColumnConfig fileNameColumnConfig = new ColumnConfig(FILE_NAME, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnName(), 200);
		fileNameColumnConfig.setRenderer(new GridCellRenderer<FileVersionDTO>() {

			@Override
			public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				return model.getName() + '.' + model.getExtension();
			}
		});
		
		// Last modification date column.
		final ColumnConfig lastModificationColumnConfig = new ColumnConfig(LAST_MODIFICATION, I18N.CONSTANTS.sigmahOfflinePrepareOfflineFileTransferColumnLastModification(), 85);
		lastModificationColumnConfig.setRenderer(new GridCellRenderer<FileVersionDTO>() {

			@Override
			public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				return DateUtils.DATE_SHORT.format(model.getAddedDate());
			}
		});
		
		// Author full name column.
		final ColumnConfig authorColumnConfig = new ColumnConfig(AUTHOR, I18N.CONSTANTS.flexibleElementFilesListAuthor(), 100);
		authorColumnConfig.setRenderer(new GridCellRenderer<FileVersionDTO>() {

			@Override
			public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				if(model.getAuthorFirstName() != null && !model.getAuthorFirstName().isEmpty()) {
					return model.getAuthorFirstName() + ' ' + model.getAuthorName();
				} else {
					return model.getAuthorName();
				}
			}
		});
		
		// Size column.
		final ColumnConfig sizeColumnConfig = new ColumnConfig(SIZE, I18N.CONSTANTS.flexibleElementFilesListSize(), 85);
		sizeColumnConfig.setRenderer(new GridCellRenderer<FileVersionDTO>() {

			@Override
			public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionDTO> store, Grid<FileVersionDTO> grid) {
				return sizeToString(model.getSize());
			}
		});
		
		return new ColumnModel(Arrays.asList(
			selectionModel.getColumn(),
//			containerColumnConfig,
//			fileTypeColumnConfig,
			fileNameColumnConfig,
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
