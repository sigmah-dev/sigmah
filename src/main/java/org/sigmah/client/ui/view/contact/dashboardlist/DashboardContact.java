package org.sigmah.client.ui.view.contact.dashboardlist;

import com.extjs.gxt.ui.client.data.BaseModelData;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

public class DashboardContact extends AbstractModelDataEntityDTO<Integer> {
  private static final long serialVersionUID = 3613009674000251393L;

  private ContactDTO contact;
  private ContactHistory lastChange;

  public static final String PARENT_NAME = "parentName";
  public static final String ROOT_NAME = "rootName";

  public DashboardContact() {
    // serialization
  }

  public DashboardContact(ContactDTO contact, ContactHistory lastChange) {
    this.contact = contact;
    this.lastChange = lastChange;

    init();
  }

  private void init() {
    if(contact != null) {
      set(ContactDTO.TYPE, contact.getType());
      set(ContactDTO.NAME, contact.getName());
      set(ContactDTO.FIRSTNAME, contact.getFirstname());
      set(ContactDTO.EMAIL, contact.getEmail());
      set(ContactDTO.ID, contact.getId());
      set(ContactDTO.PARENT, contact.getParent());
      if (contact.getParent() != null) {
        set(PARENT_NAME, contact.getParent().getName());
      }
      set(ContactDTO.ROOT, contact.getRoot());
      if (contact.getRoot() != null) {
        set(ROOT_NAME, contact.getRoot().getName());
      }
    }

    if(lastChange != null) {
      set(ContactHistory.FORMATTED_CHANGE_TYPE, lastChange.getFormattedChangeType());
      set(ContactHistory.SUBJECT, lastChange.getSubject());
      set(ContactHistory.FORMATTED_VALUE, lastChange.getFormattedValue());
      set(ContactHistory.UPDATED_AT, lastChange.getUpdatedAt());
      set(ContactHistory.COMMENT, lastChange.getComment());
    }
  }

  public ContactDTO getContact() {
    return contact;
  }

  public void setContact(ContactDTO contact) {
    this.contact = contact;
  }

  public ContactHistory getLastChange() {
    return lastChange;
  }

  public void setLastChange(ContactHistory lastChange) {
    this.lastChange = lastChange;
  }

  @Override
  public String getEntityName() {
    return null;
  }
}
