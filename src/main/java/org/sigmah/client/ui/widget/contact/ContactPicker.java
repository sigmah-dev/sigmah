package org.sigmah.client.ui.widget.contact;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

import java.util.Set;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.shared.command.GetContactByNameOrEmail;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactPicker extends Composite {

  private final FlexibleGrid<ContactDTO> contactsGrid;

  public ContactPicker(final ContactModelType allowedType, final boolean onlyWithoutUser, final Set<Integer> allowedModelIds, final Integer checkboxElementId, final Set<Integer> alreadySelectedContacts, final DispatchAsync dispatch) {

    final ListStore<ContactDTO> matchingContactsStore = new ListStore<ContactDTO>();

    LayoutContainer container = Layouts.border();

    final TextField<String> searchTextbox = Forms.text(null, true);

    LayoutContainer topContainer = Layouts.border();
    topContainer.add(Forms.button(I18N.CONSTANTS.search(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent ce) {
        matchingContactsStore.removeAll();
        dispatch.execute(new GetContactByNameOrEmail(searchTextbox.getValue(), false, onlyWithoutUser, allowedType, allowedModelIds, alreadySelectedContacts, checkboxElementId), new AsyncCallback<ListResult<ContactDTO>>() {
          @Override
          public void onFailure(Throwable caught) {
            Log.error("Error while trying to get contacts for a contact list element.", caught);
          }

          @Override
          public void onSuccess(ListResult<ContactDTO> result) {
            matchingContactsStore.add(result.getList());
          }
        });
      }
    }), Layouts.borderLayoutData(Style.LayoutRegion.EAST, Layouts.Margin.LEFT));
    topContainer.add(searchTextbox, Layouts.borderLayoutData(Style.LayoutRegion.CENTER));

    container.add(topContainer, Layouts.borderLayoutData(Style.LayoutRegion.NORTH, 20f, Layouts.Margin.BOTTOM));

    contactsGrid = new FlexibleGrid<ContactDTO>(matchingContactsStore, null, 5, getColumnModel());
    contactsGrid.setAutoExpandColumn(ContactDTO.NAME);
    contactsGrid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);

    container.add(contactsGrid, Layouts.borderLayoutData(Style.LayoutRegion.CENTER));

    initWidget(container);
  }

  private ColumnConfig[] getColumnModel() {
    final ColumnConfig typeColumn = new ColumnConfig(ContactDTO.TYPE, I18N.CONSTANTS.contactTypeLabel(), 75);
    typeColumn.setRenderer(new GridCellRenderer<ContactDTO>() {

      @Override
      public Object render(final ContactDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
                           final ListStore<ContactDTO> store, final Grid<ContactDTO> grid) {

        ContactModelType type = model.get(property);

        String typeLabel = I18N.CONSTANTS.contactTypeIndividualLabel();

        if (type == ContactModelType.ORGANIZATION) {
          typeLabel = I18N.CONSTANTS.contactTypeOrganizationLabel();
        }

        return typeLabel;
      }
    });

    ColumnConfig nameColumn = new ColumnConfig(ContactDTO.NAME, I18N.CONSTANTS.contactName(), 100);

    ColumnConfig firstNameColumn = new ColumnConfig(ContactDTO.FIRSTNAME, I18N.CONSTANTS.contactFirstName(), 75);

    ColumnConfig emailColumn = new ColumnConfig(ContactDTO.EMAIL, I18N.CONSTANTS.contactEmailAddress(), 150);

    ColumnConfig idColumn = new ColumnConfig(ContactDTO.ID, I18N.CONSTANTS.contactId(), 100);
    idColumn.setHidden(true);

    return new ColumnConfig[]{
            typeColumn,
            nameColumn,
            firstNameColumn,
            emailColumn,
            idColumn
    };
  }

  public ContactDTO getSelectedItem() {
    return contactsGrid.getSelectionModel().getSelectedItem();
  }
}
