package org.sigmah.client.ui.view.contact.dashboardlist;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.FilterConfig;
import com.extjs.gxt.ui.client.data.FilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.MemoryProxy;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class PagingContactsProxy extends MemoryProxy<PagingLoadResult<DashboardContact>> {

  private List<DashboardContact> allContacts = new ArrayList<DashboardContact>();

  public PagingContactsProxy() {
    super(null);
  }

  @Override
  public void load(DataReader<PagingLoadResult<DashboardContact>> reader, Object loadConfig, AsyncCallback<PagingLoadResult<DashboardContact>> callback) {

    FilterPagingLoadConfig config = (FilterPagingLoadConfig)loadConfig;

    List<DashboardContact> results;
    int totalLength = 0;
    int offset = config.getOffset();

    if (ClientUtils.isEmpty(allContacts)) {
      results = Collections.emptyList();
    } else {

      sortContacts(config.getSortInfo());

      List<DashboardContact> filteredList = filterContacts(config.getFilterConfigs());

      totalLength = filteredList.size();

      if (offset > totalLength) {
        offset = 0;
      }

      int limit = offset + config.getLimit();
      if (limit >= totalLength) {
        limit = totalLength;
      }
      results = filteredList.subList(offset, limit);
    }

    callback.onSuccess(new BasePagingLoadResult<DashboardContact>(results, offset, totalLength));
  }

  private void sortContacts(SortInfo sortInfo) {
    if (sortInfo.getSortField() != null) {
      final String sortField = sortInfo.getSortField();
      if (sortField != null) {
        Collections.sort(allContacts, sortInfo.getSortDir().comparator(new Comparator<DashboardContact>() {
          public int compare(DashboardContact p1, DashboardContact p2) {
            if (sortField.equals(ContactDTO.TYPE)) {
              return compareNullableStrings(ContactModelType.getName(p1.getContact().getType()), ContactModelType.getName(p2.getContact().getType()));
            }
            if (sortField.equals(ContactDTO.NAME)) {
              return compareNullableStrings(p1.getContact().getName(), p2.getContact().getName());
            }
            if (sortField.equals(ContactDTO.FIRSTNAME)) {
              return compareNullableStrings(p1.getContact().getFirstname(), p2.getContact().getFirstname());
            }
            if (sortField.equals(ContactHistory.FORMATTED_CHANGE_TYPE)) {
              return compareNullableStrings(p1.getLastChange().getFormattedChangeType(), p2.getLastChange().getFormattedChangeType());
            }
            if (sortField.equals(ContactHistory.SUBJECT)) {
              return compareNullableStrings(p1.getLastChange().getSubject(), p2.getLastChange().getSubject());
            }
            if (sortField.equals(ContactHistory.FORMATTED_VALUE)) {
              return compareNullableStrings(p1.getLastChange().getFormattedValue(), p2.getLastChange().getFormattedValue());
            }
            if (sortField.equals(ContactDTO.EMAIL)) {
              return compareNullableStrings(p1.getContact().getEmail(), p2.getContact().getEmail());
            }
            if (sortField.equals(ContactDTO.ID)) {
              return compareNullableStrings(String.valueOf(p1.getContact().getId()), String.valueOf(p2.getContact().getId()));
            }
            if (sortField.equals(ContactDTO.PARENT)) {
              ContactDTO org1 = p1.getContact().getParent();
              ContactDTO org2 = p2.getContact().getParent();
              String nom1 = org1 == null ? "" : org1.getOrganizationName();
              String nom2 = org2 == null ? "" : org2.getOrganizationName();
              return compareNullableStrings(nom1, nom2);
            }
            if (sortField.equals(ContactDTO.ROOT)) {
              ContactDTO org1 = p1.getContact().getRoot();
              ContactDTO org2 = p2.getContact().getRoot();
              String nom1 = org1 == null ? "" : org1.getOrganizationName();
              String nom2 = org2 == null ? "" : org2.getOrganizationName();
              return compareNullableStrings(nom1, nom2);
            }
            if (sortField.equals(ContactHistory.UPDATED_AT)) {
              Date d1 = p1.getLastChange() != null ? p1.getLastChange().getUpdatedAt() : null;
              Date d2 = p2.getLastChange() != null ? p2.getLastChange().getUpdatedAt() : null;
              return compareNullableDates(d1, d2);
            }
            if (sortField.equals(ContactHistory.COMMENT)) {
              return compareNullableStrings(p1.getLastChange().getComment(), p2.getLastChange().getComment());
            }
            return 0;
          }
        }));
      }
    }
  }

  private List<DashboardContact> filterContacts(List<FilterConfig> filters) {

    List<DashboardContact> filtered = new ArrayList<DashboardContact>();

    if(ClientUtils.isEmpty(filters)) {
      filtered.addAll(allContacts);
      return filtered;
    }

    for (DashboardContact contact : allContacts) {
      boolean valid = true;
      for (FilterConfig filter : filters) {
        if(filter.getValue() == null) {
          continue;
        }
        if(contact.get(filter.getField()) == null || filter.isFiltered(contact, filter.getValue(), filter.getComparison(), contact.get(filter.getField()))) {
          valid = false;
          break;
        }
      }
      if (valid) {
        filtered.add(contact);
      }
    }

    return filtered;
  }

  private int compareNullableStrings(String s1, String s2) {
    if(s1 == s2) {
      return 0;
    }
    if (s1 == null) {
      return -1;
    }
    if (s2 == null) {
      return 1;
    }

    return s1.compareToIgnoreCase(s2);
  }

  private int compareNullableDates(Date d1, Date d2) {
    if (d1 == null) {
      return -1;
    }
    if (d2 == null) {
      return 1;
    }

    return d1.compareTo(d2);
  }

  public void clearContacts() {
    this.allContacts.clear();
  }

  public void addContact(DashboardContact contact) {
    if(this.allContacts.contains(contact)) {
      this.allContacts.remove(contact);
    }
    this.allContacts.add(contact);
  }

  public void addContacts(List<DashboardContact> contacts) {
    for(DashboardContact contact : contacts) {
      if (this.allContacts.contains(contact)) {
        this.allContacts.remove(contact);
      }
    }
    this.allContacts.addAll(contacts);
  }
}
