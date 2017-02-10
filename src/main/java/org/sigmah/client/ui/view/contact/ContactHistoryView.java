package org.sigmah.client.ui.view.contact;
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

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;

import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.contact.ContactHistoryPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.result.ContactHistory;

public class ContactHistoryView extends AbstractView implements ContactHistoryPresenter.View {
  private static final int RESIZE_DELAY = 200;
  private static final int PADDING = 10;

  private LayoutContainer container;
  private Grid<ContactHistory> contactHistoryGrid;

  private ImageProvider imageProvider;

  @Override
  public void initialize() {
    contactHistoryGrid = new Grid<ContactHistory>(new ListStore<ContactHistory>(), buildColumnModel());
    contactHistoryGrid.getView().setForceFit(true);
    contactHistoryGrid.setAutoHeight(true);
    contactHistoryGrid.addListener(Events.ViewReady, new Listener<BaseEvent>() {
      @Override
      public void handleEvent(BaseEvent be) {
        resizeGrid(contactHistoryGrid, container, true);
      }
    });
    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
        resizeGrid(contactHistoryGrid, container, true);
      }
    });

    container = Panels.content(null, false, Layouts.vBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCH, new Layouts.LayoutOptions(new Padding(10))), "x-border-layout-ct");
    container.setScrollMode(Style.Scroll.AUTOY);
    container.add(contactHistoryGrid);
    add(container);
  }

  @Override
  public void updateGridData(List<ContactHistory> contactHistories) {
    contactHistoryGrid.getStore().removeAll();
    contactHistoryGrid.getStore().add(contactHistories);
    resizeGrid(contactHistoryGrid, container, true);
  }

  @Override
  public void setImageProvider(ImageProvider imageProvider) {
    this.imageProvider = imageProvider;
  }

  private ColumnModel buildColumnModel() {
    ColumnConfig dateColumn = new ColumnConfig(ContactHistory.UPDATED_AT, 120);
    dateColumn.setRenderer(new GridCellRenderer<ContactHistory>() {
      @Override
      public Object render(ContactHistory contactHistory, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
        return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(contactHistory.getUpdatedAt());
      }
    });
    dateColumn.setHeaderText(I18N.CONSTANTS.contactHistoryUpdatedAtHeader());

    ColumnConfig userColumn = new ColumnConfig(ContactHistory.USER_FULL_NAME, I18N.CONSTANTS.contactHistoryUserHeader(), 150);
    ColumnConfig changeTypeColumn = new ColumnConfig(ContactHistory.FORMATTED_CHANGE_TYPE, I18N.CONSTANTS.contactHistoryChangeTypeHeader(), 150);
    ColumnConfig subjectColumn = new ColumnConfig(ContactHistory.SUBJECT, I18N.CONSTANTS.contactHistorySubjectHeader(), 150);

    ColumnConfig valueColumn = new ColumnConfig(ContactHistory.FORMATTED_VALUE, 150);
    valueColumn.setRenderer(new GridCellRenderer<ContactHistory>() {
      @Override
      public Object render(ContactHistory contactHistory, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ContactHistory> store, Grid<ContactHistory> grid) {
        if (contactHistory.getValueType() == ContactHistory.ValueType.STRING) {
          return contactHistory.getFormattedValue();
        }

        final Image image = new Image();
        image.setWidth("100px");
        imageProvider.provideDataUrl(contactHistory.getFormattedValue(), new SuccessCallback<String>() {
          @Override
          public void onSuccess(String dataUrl) {
            image.setUrl(dataUrl);
          }
        });
        return image;
      }
    });
    valueColumn.setHeaderText(I18N.CONSTANTS.contactHistoryValueHeader());

    ColumnConfig commentColumn = new ColumnConfig(ContactHistory.COMMENT, I18N.CONSTANTS.contactHistoryCommentHeader(), 150);

    return new ColumnModel(Arrays.asList(dateColumn, userColumn, changeTypeColumn, subjectColumn, valueColumn, commentColumn));
  }

  private void resizeGrid(final Grid grid, final LayoutContainer container, final boolean canBeDelayed) {
    new Timer() {
      @Override
      public void run() {
        if (!grid.isVisible()) {
          if (canBeDelayed) {
            this.schedule(RESIZE_DELAY);
          }
          return;
        }

        grid.setAutoHeight(false);

        int height;
        int maxHeight = container.getHeight() - PADDING * 2;

        int unwrappedGridHeight = grid.getView().getScroller().firstChild().getHeight() + grid.getView().getHeader().getHeight();
        if (unwrappedGridHeight < maxHeight) {
          height = unwrappedGridHeight;
        } else {
          height = maxHeight;
        }

        grid.setHeight(height);
        grid.getView().layout();
      }
    }.schedule(RESIZE_DELAY);
  }
}
