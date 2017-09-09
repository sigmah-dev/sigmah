package org.sigmah.client.ui.widget.form;

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

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.*;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.DispatchQueue;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.dto.element.FlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

/**
 * Custom {@link com.extjs.gxt.ui.client.widget.TabPanel} implementation.
 *
 * @see Forms
 */
public class IterableGroupPanel extends TabPanel {

  public static class IterableGroupItem extends TabItem {
    private IterableGroupPanel parent;
    private int iterationId;
    private String iterationName;
    private Map<FlexibleElementDTO, Boolean> mandatoryElements = new HashMap<FlexibleElementDTO, Boolean>();
    private Button btnRename;

    public IterableGroupItem(IterableGroupPanel parent, int iterationId, String iterationName) {
      super();
      this.parent = parent;
      this.iterationId = iterationId;
      this.iterationName = iterationName;

      if(parent.canEdit) {
        generateToolBar();
      }
    }

    public int getIterationId() {
      return iterationId;
    }

    public void setIterationId(int iterationId) {
      this.iterationId = iterationId;
    }

    public String getIterationName() {
      return iterationName;
    }

    public void setIterationName(String iterationName) {
      this.iterationName = iterationName;
      refreshRenameButton();
      refreshTitle();
    }

    public void setElementValidity(FlexibleElementDTO element, boolean valid) {
      mandatoryElements.put(element, valid);
      revalidateElements();
    }

    public void revalidateElements() {
      boolean valid = true;

      for (Boolean elementValid : mandatoryElements.values()) {
        if (!elementValid) {
          valid = false;
        }
      }

      if (valid) {
        getHeader().setIcon(IconImageBundle.ICONS.elementCompleted());
      } else {
        getHeader().setIcon(IconImageBundle.ICONS.elementUncompleted());
      }
    }

    public void refreshTitle() {
      String name = iterationName!=null?iterationName:"";
      String title = (parent.getItems().indexOf(this) + 1) + ". " + name;
      getHeader().setText(title);
    }

    private void generateToolBar() {

      btnRename = new Button();
      btnRename.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent menuEvent) {
          renameTab();
        }
      });

      refreshRenameButton();

      Button btnRemove = new Button(I18N.CONSTANTS.layoutGroupIterationRemoveButton());
      btnRemove.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent menuEvent) {
          removeTab();
        }
      });

      ToolBar tb = new ToolBar();
      tb.setAutoHeight(true);
      tb.add(btnRename);
      tb.add(btnRemove);

      add(tb);
    }

    private void refreshRenameButton() {

      if(getIterationName() == null || "".equals(getIterationName())) {
        btnRename.setText(I18N.CONSTANTS.layoutGroupIterationSetNameButton());
      } else {
        btnRename.setText(I18N.CONSTANTS.layoutGroupIterationRenameButton());
      }
    }

    private void renameTab() {
      final Window w = new Window();
      w.setHeadingText(I18N.CONSTANTS.layoutGroupIterationRename());
      w.setPlain(true);
      w.setModal(true);
      w.setBlinkModal(true);
      w.setLayout(new FitLayout());
      w.setSize(500, 100);

      final TextField<String> field = new TextField<String>();
      field.setFieldLabel(I18N.CONSTANTS.layoutGroupIterationName());
      field.setValue(getIterationName());

      Button btn = Forms.button(I18N.CONSTANTS.ok());
      btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent buttonEvent) {

          String tabName = field.getValue();

          setIterationName(tabName);

          parent.iterationNameChanged(iterationId, tabName);

          w.hide();
        }
      });

      com.extjs.gxt.ui.client.widget.form.FormPanel fp = new com.extjs.gxt.ui.client.widget.form.FormPanel();
      fp.setHeaderVisible(false);
      fp.setLayout(Forms.layout(150, 300));
      fp.add(field);
      fp.getButtonBar().add(btn);

      w.add(fp);
      w.show();
    }

    private void removeTab() {
      N10N.confirmation(I18N.CONSTANTS.layoutGroupIterationDelete(), I18N.CONSTANTS.layoutGroupIterationDeleteConfirm(), new ConfirmCallback() {

        @Override
        public void onAction() {

          parent.removeTab(iterationId);
        }
      });
    }
  }

  public interface Delegate {
    FieldSet createGroupLayoutFieldSet(FlexibleElementContainer container, LayoutGroupDTO layoutGroup, DispatchQueue queue, Integer iterationId, IterableGroupPanel tabPanel, IterableGroupItem tabItem);

    void addIterationTabItem(int iterationId, IterableGroupItem tab);

    IterationChange getIterationChange(int iterationId);

    void setIterationChange(IterationChange iterationChange);
  }

  /**
   * Application command dispatcher.
   */
  protected final DispatchAsync dispatch;

  /**
   * Next temporary iterationId (used until database insertion)
   */
  private int nextIterationId = -2;

  /**
   * Iterative group corresponding to this TabPanel
   */
  private LayoutGroupDTO layoutGroup;

  private FlexibleElementContainer container;

  private final boolean canEdit;

  private Delegate delegate;

  public IterableGroupPanel(final DispatchAsync dispatch, final LayoutGroupDTO layoutGroup, FlexibleElementContainer container, final boolean canEdit, final EventBus eventBus) {
    super();

    this.dispatch = dispatch;
    this.container = container;
    this.layoutGroup = layoutGroup;
    this.canEdit = canEdit;

    if(canEdit) {
      addListener(Events.Render, new Listener<TabPanelEvent>() {

        @Override
        public void handleEvent(TabPanelEvent tpe) {

          El strip = el().childNode(0).childNode(0).childNode(0);
          int clearIdx = strip.getChildIndex(strip.lastChild().dom);
          final Button button = new Button("+");
          button.setEnabled(canEdit);
          button.setVisible(!Profiler.INSTANCE.isOfflineMode());
          button.render(strip.dom, clearIdx - 1);
          ComponentHelper.doAttach(button);
          button.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
              addEmptyTab();
            }
          });

		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				if (ApplicationState.OFFLINE == event.getState()) {
					button.setVisible(false);
				}
			}

		});
      }

      });
    }
  }

  private void addEmptyTab() {

    if (delegate != null) {

      final Window w = new Window();
      w.setHeadingText(I18N.CONSTANTS.layoutGroupIterationCreation());
      w.setPlain(true);
      w.setModal(true);
      w.setBlinkModal(true);
      w.setLayout(new FitLayout());
      w.setSize(500, 100);

      final TextField<String> field = new TextField<String>();
      field.setFieldLabel(I18N.CONSTANTS.layoutGroupIterationName());

      Button btn = Forms.button(I18N.CONSTANTS.ok());
      btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent buttonEvent) {

          DispatchQueue queue = new DispatchQueue(dispatch, true);
          String tabName = field.getValue();
          int iterationId = getTemporaryIterationId();

          final IterableGroupItem tab = new IterableGroupItem(IterableGroupPanel.this, iterationId, tabName);
          addIterationTab(tab);
          delegate.addIterationTabItem(iterationId, tab);

          Layout tabLayout = Layouts.fitLayout();

          tab.setLayout(tabLayout);

          FieldSet tabSet = delegate.createGroupLayoutFieldSet(container, layoutGroup, queue, iterationId, IterableGroupPanel.this, tab);

          tab.add(tabSet);

          queue.start();

          setSelection(tab);

          IterationChange ic = new IterationChange();
          ic.setIterationId(iterationId);
          ic.setName(tabName);
          ic.setLayoutGroupId(layoutGroup.getId());
          delegate.setIterationChange(ic);

          w.hide();
        }
      });

      FormPanel fp = new FormPanel();
      fp.setHeaderVisible(false);
      fp.setLayout(Forms.layout(150, 300));
      fp.add(field);
      fp.getButtonBar().add(btn);

      w.add(fp);
      w.show();
    }
  }

  public void addIterationTab(final IterableGroupItem item) {

    super.add(item);

    item.refreshTitle();
  }

  private void removeTab(int iterationId) {

    if (delegate != null) {
      IterationChange ic = delegate.getIterationChange(iterationId);
      if (ic == null) {
        ic = new IterationChange();
        ic.setIterationId(iterationId);
      }

      ic.setDeleted(true);
      ic.setLayoutGroupId(layoutGroup.getId());
      delegate.setIterationChange(ic);
    }

    remove(getSelectedItem());

    refreshTabNames();
  }

  private void refreshTabNames() {

    for (int i = 0; i < getItemCount(); i++) {
      ((IterableGroupItem) getItem(i)).refreshTitle();
    }
  }

  public void setElementValidity(FlexibleElementDTO element, boolean valid) {
    ((IterableGroupItem) getSelectedItem()).setElementValidity(element, valid);
  }

  public void validateElements() {
    for (TabItem item : getItems()) {
      ((IterableGroupItem) item).revalidateElements();
    }
  }

  public Integer getCurrentIterationId() {
    return ((IterableGroupItem) getSelectedItem()).getIterationId();
  }

  public int getTemporaryIterationId() {
    return nextIterationId--;
  }

  public void setDelegate(Delegate delegate) {
    this.delegate = delegate;
  }

  private void iterationNameChanged(int iterationId, String newName) {
    IterationChange ic = delegate.getIterationChange(iterationId);
    if (ic == null) {
      ic = new IterationChange();
      ic.setIterationId(iterationId);
    }

    ic.setName(newName);
    ic.setLayoutGroupId(layoutGroup.getId());
    delegate.setIterationChange(ic);
  }
}
