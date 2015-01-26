package org.sigmah.client.ui.view.reminder;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.ui.presenter.reminder.ReminderHistoryPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.inject.Singleton;

/**
 * Reminder frame view used to show a reminder or a monitored point's history.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReminderHistoryView extends AbstractPopupView<PopupWidget> implements ReminderHistoryPresenter.View {

	private Grid<AbstractModelDataEntityDTO<?>> grid;

	/**
	 * Builds the view.
	 */
	public ReminderHistoryView() {
		super(new PopupWidget(true, Layouts.fitLayout()), 750, 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId(ReminderHistoryDTO.ID);
		column.setHeaderText("Token ID"); // TODO i18n
		column.setWidth(100);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.DATE, "DATE", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.VALUE, "NOTE", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		column = new ColumnConfig(ReminderHistoryDTO.TYPE, "ACTION", 150); // TODO i18n
		column.setAlignment(HorizontalAlignment.LEFT);
		configs.add(column);

		grid = new Grid<AbstractModelDataEntityDTO<?>>(new ListStore<AbstractModelDataEntityDTO<?>>(), new ColumnModel(configs));
		grid.getView().setForceFit(true);
		grid.setBorders(false);
		grid.setAutoExpandColumn(ReminderHistoryDTO.VALUE);

		initPopup(grid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setData(final List<? extends AbstractModelDataEntityDTO<?>> dataList) {
		grid.getStore().removeAll();
		grid.getStore().add(dataList);
		grid.getStore().commitChanges();
	}

}
