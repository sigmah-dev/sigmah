package org.sigmah.server.handler;

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

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.HistoryToken;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.value.TripletValue;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.UserPermissionPolicy;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.UpdateContact;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.util.ValueResultUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateContactHandler extends AbstractCommandHandler<UpdateContact, VoidResult> {
  private static final Logger LOG = LoggerFactory.getLogger(UpdateContactHandler.class);

  private final Mapper mapper;
  private final Injector injector;
  private final I18nServer i18nServer;
  private final ContactDAO contactDAO;
  private final CountryDAO countryDAO;

  @Inject
  public UpdateContactHandler(Mapper mapper, Injector injector, I18nServer i18nServer, ContactDAO contactDAO, CountryDAO countryDAO) {
    this.mapper = mapper;
    this.injector = injector;
    this.i18nServer = i18nServer;
    this.contactDAO = contactDAO;
    this.countryDAO = countryDAO;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public VoidResult execute(UpdateContact cmd, UserExecutionContext context) throws CommandException {

    if (LOG.isDebugEnabled()) {
      LOG.debug("[execute] Updates contact #" + cmd.getContactId() + " with following values #" + cmd.getValues().size() + " : " + cmd.getValues());
    }

    updateContact(cmd.getValues(), cmd.getContactId(), context, cmd.getComment());

    return null;
  }

  @Transactional(rollbackOn = CommandException.class)
  protected void updateContact(List<ValueEventWrapper> values, Integer contactId, UserExecutionContext context, String comment) throws CommandException {
    // This date must be the same for all the saved values !
    Date historyDate = new Date();

    User user = context.getUser();
    Contact contact = contactDAO.findById(contactId);

    // Verify if the modifications conflicts with the contact state.
    List<String> conflicts = searchForConflicts(contact, context);

    // Iterating over the value change events
    for (ValueEventWrapper valueEvent : values) {

      // Event parameters.
      FlexibleElementDTO source = valueEvent.getSourceElement();
      FlexibleElement element = em().find(FlexibleElement.class, source.getId());
      TripletValueDTO updateListValue = valueEvent.getListValue();
      String updateSingleValue = valueEvent.getSingleValue();

      LOG.debug("[execute] Updates value of element #{} ({})", source.getId(), source.getEntityName());
      LOG.debug("[execute] Event of type {} with value {} and list value {}.", valueEvent.getChangeType(), updateSingleValue, updateListValue);

      // Case of the default flexible element which values arent't stored
      // like other values. These values impact directly the contact.
      if (source instanceof DefaultContactFlexibleElementDTO) {

        DefaultContactFlexibleElementDTO defaultElement = (DefaultContactFlexibleElementDTO) source;

        LOG.debug("[execute] Default element case '{}'.", defaultElement.getType());

        if (!(((DefaultContactFlexibleElementDTO) source).getType().isUpdatable())) {
          continue;
        }
        // Saves the value and switch to the next value.
        String oldValue = saveDefaultElement(contact, defaultElement.getType(), updateSingleValue);

        // Checks if the first value has already been historized or not.
        List<HistoryToken> results = null;
        if (element != null) {
          TypedQuery<HistoryToken> query =
              em().createQuery("SELECT h FROM HistoryToken h WHERE h.elementId = :elementId AND h.projectId = :contactId", HistoryToken.class);
          query.setParameter("elementId", element.getId());
          query.setParameter("contactId", contactId);
          results = query.getResultList();
        }

        if ((results == null || results.isEmpty()) && oldValue != null) {
          // Historize the first value.
          historize(contact.getDateCreated(), element, contactId, null, ValueEventChangeType.ADD, oldValue, null, null);
        }

        // Historize the value.
        historize(historyDate, element, contactId, user, ValueEventChangeType.EDIT, updateSingleValue, null, comment);

        continue;
      }

      // Retrieving the current value
      Value currentValue = retrieveOrCreateValue(contactId, source.getId(), user);

      // Unique value of the flexible element.
      if (updateListValue == null) {

        if (LOG.isDebugEnabled()) {
          LOG.debug("[execute] Basic value case.");
        }

        currentValue.setValue(updateSingleValue);

        // Historize the value.
        historize(historyDate, element, contactId, user, ValueEventChangeType.EDIT, updateSingleValue, null, comment);
      }

      // Special case : this value is a part of a list which is the true value of the flexible element. (only used for
      // the TripletValue class for the moment)
      else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("[execute] List value case.");
        }

        // The value of the element is a list of ids (default separated).
        List<Integer> ids = ValueResultUtils.splitValuesAsInteger(currentValue.getValue());

        if (LOG.isDebugEnabled()) {
          LOG.debug("[execute] The current list of ids is : " + ids + ".");
        }

        // Cast the update value (as a DTO).
        switch (valueEvent.getChangeType()) {
          case ADD:
            onAdd(updateListValue, ids, currentValue, historyDate, element, contactId, user, comment);
            break;

          case REMOVE:
            if (!onDelete(updateListValue, ids, currentValue, historyDate, element, contactId, user, comment)) {
              // Do not historize, the value hasn't been changed.
              continue;
            }
            break;

          case EDIT:
            onEdit(updateListValue, historyDate, element, contactId, user, comment);
            break;

          default:
            LOG.debug("[execute] Unknown command " + valueEvent.getChangeType() + ".");
            break;
        }

        LOG.debug("[execute] The new list of ids is : " + ids + ".");
      }

      // Store the value.
      em().merge(currentValue);
    }

    if (!conflicts.isEmpty()) {
      // A conflict was found.
      throw new UpdateConflictException(contact.toContainerInformation(), conflicts.toArray(new String[0]));
    }
  }

  protected void onAdd(TripletValueDTO item, List<Integer> ids, Value currentValue, Date historyDate, FlexibleElement element, Integer projectId, User user, String comment) {
    LOG.debug("[execute] Adds an element to the list.");

    // Adds the element.
    TripletValue entity = mapper.map(item, new TripletValue());
    entity = em().merge(entity);

    LOG.debug("[execute] Successfully create the entity with id #" + entity.getId() + ".");

    // Updates the value.
    ids.add(entity.getId());
    currentValue.setValue(ValueResultUtils.mergeElements(ids));

    // Historize the value.
    historize(historyDate, element, projectId, user, ValueEventChangeType.ADD, null, entity, comment);
  }

  protected boolean onDelete(TripletValueDTO item, List<Integer> ids, Value currentValue, Date historyDate, FlexibleElement element, Integer projectId, User user, String comment) {
    LOG.debug("[execute] Removes a element from the list.");

    // Retrieves the element.
    TripletValue entity = em().find(TripletValue.class, item.getId());

    if (!(entity instanceof Deleteable)) {
      LOG.debug("[execute] The element isn't deletable, the event is ignored.");
      return false;
    }

    // Marks the entity as deleted.
    ((Deleteable) entity).delete();
    em().merge(entity);

    if (LOG.isDebugEnabled()) {
      LOG.debug("[execute] Successfully remove the entity with id #" + entity.getId() + ".");
    }

    // Updates the value.
    ids.remove(entity.getId());
    currentValue.setValue(ValueResultUtils.mergeElements(ids));

    // Historize the value.
    historize(historyDate, element, projectId, user, ValueEventChangeType.REMOVE, null, entity, comment);
    return true;
  }

  protected void onEdit(TripletValueDTO item, Date historyDate, FlexibleElement element, Integer projectId, User user, String comment) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("[execute] Edits a element from the list.");
    }

    // Retrieves the element.
    TripletValue entity = mapper.map(item, new TripletValue());
    em().merge(entity);

    if (LOG.isDebugEnabled()) {
      LOG.debug("[execute] Successfully edit the entity with id #" + entity.getId() + ".");
    }

    // Historize the value.
    historize(historyDate, element, projectId, user, ValueEventChangeType.EDIT, null, entity, comment);
  }

  private void historize(Date date, FlexibleElement element, Integer contactId, User user, ValueEventChangeType type, String singleValue, TripletValue listValue, String comment) {

    // Manages history.
    if (element != null && element.isHistorable()) {

      HistoryToken historyToken = new HistoryToken();

      historyToken.setElementId(element.getId());
      historyToken.setProjectId(contactId);
      historyToken.setDate(date);
      historyToken.setUser(user);
      historyToken.setType(type);
      historyToken.setComment(comment);

      // Sets the value or list value.
      if (listValue == null) {
        historyToken.setValue(element.asHistoryToken(singleValue));
      } else {
        historyToken.setValue(element.asHistoryToken(listValue));
      }

      em().persist(historyToken);
    }
  }

  /**
   * Retrieves the value for the given project and the given element.
   * If there isn't a value yet, it will be created.
   *
   * @param contactId The project id.
   * @param elementId The source element id.
   * @param user      The user which launch the command.
   * @return The value.
   */
  public Value retrieveOrCreateValue(int contactId, Integer elementId, User user) {

    // Retrieving the current value
    Value currentValue = retrieveCurrentValue(contactId, elementId);

    // Update operation.
    if (currentValue != null) {
      LOG.debug("[execute] Retrieves a value for element #{0}.", elementId);
      currentValue.setLastModificationAction('U');
    }
    // Create operation
    else {
      LOG.debug("[execute] Creates a value for element #{0}.", elementId);

      currentValue = new Value();
      currentValue.setLastModificationAction('C');

      // Parent element
      FlexibleElement element = em().find(FlexibleElement.class, elementId);
      currentValue.setElement(element);

      // Container
      currentValue.setContainerId(contactId);
    }

    // Updates the value's fields.
    currentValue.setLastModificationDate(new Date());
    currentValue.setLastModificationUser(user);

    return currentValue;
  }

  /**
   * Retrieves the value for the given project and the given element but
   * don't create an empty value if none exists.
   *
   * @param contactId The project id.
   * @param elementId The source element id.
   * @return The value or <code>null</code> if not found.
   */
  private Value retrieveCurrentValue(int contactId, Integer elementId) {
    Query query = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :contactId and v.element.id = :elementId");
    query.setParameter("contactId", contactId);
    query.setParameter("elementId", elementId);

    Value currentValue = null;

    try {
      currentValue = (Value) query.getSingleResult();
    } catch (NoResultException nre) {
      // No current value
    }

    return currentValue;
  }

  private String saveDefaultElement(Contact contact, DefaultContactFlexibleElementType type, String value) {

    // All default values are managed as strings.
    // See DefaultContactFlexibleElementDTO.getComponent();
    if (value == null) {
      LOG.error("[saveDefaultElement] The value isn't a string and cannot be considered.");
      return null;
    }

    String oldValue;

    switch (type) {
      case COUNTRY:
        if (contact.getCountry() == null) {
          oldValue = null;
        } else {
          oldValue = String.valueOf(contact.getCountry().getId());
        }

        if ("".equals(value)) {
          contact.setCountry(null);
        } else {
          Country country = countryDAO.findById(Integer.parseInt(value));
          contact.setCountry(country);
        }

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container country to '{}'.", value);
        }
        break;
      case DIRECT_MEMBERSHIP:
        if (contact.getParent() == null) {
          oldValue = null;
        } else {
          oldValue = String.valueOf(contact.getParent().getId());
        }

        if ("".equals(value)) {
          contact.setParent(null);
        } else {
          Contact parent = contactDAO.findById(Integer.parseInt(value));
          contact.setParent(parent);
        }

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container direct membership to '{}'.", value);
        }
        break;
      case EMAIL_ADDRESS:
        oldValue = contact.getEmail();
        contact.setEmail(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container email to '{}'.", value);
        }
        break;
      case FIRST_NAME:
        oldValue = contact.getFirstname();
        contact.setFirstname(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container first name to '{}'.", value);
        }
        break;
      case FAMILY_NAME:
        // fall through
      case ORGANIZATION_NAME:
        oldValue = contact.getName();
        contact.setName(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container name to '{}'.", value);
        }
        break;
      case PHONE_NUMBER:
        oldValue = contact.getPhoneNumber();
        contact.setPhoneNumber(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container phone number to '{}'.", value);
        }
        break;
      case PHOTO:
        oldValue = contact.getPhoto();
        contact.setPhoto(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container photo to '{}'.", value);
        }
        break;
      case POSTAL_ADDRESS:
        oldValue = contact.getPostalAddress();
        contact.setPostalAddress(value);

        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Set container postal address to '{}'.", value);
        }
        break;

      // Ignored because they should always be unmodifiable
      case CREATION_DATE:
      case LOGIN:
      case MAIN_ORG_UNIT:
      case SECONDARY_ORG_UNITS:
      case TOP_MEMBERSHIP:
        if (LOG.isDebugEnabled()) {
          LOG.debug("[saveDefaultElement] Cannot update container {}.", type);
        }
        return null;
      default:
        throw new IllegalStateException();
    }

    LOG.debug("[saveDefaultElement] Updates the container.");
    contactDAO.update(contact);

    return oldValue;
  }

  // Check if the targeted contact can be updated
  private List<String> searchForConflicts(Contact contact, UserExecutionContext context) throws FunctionalException {
    Language language = context.getLanguage();

    if (contact == null) {
      return Collections.singletonList(i18nServer.t(language, "contactNotFoundError"));
    }

    List<String> conflicts = new ArrayList<>();
    if (contact.getContactModel().isUnderMaintenance()) {
      conflicts.add(i18nServer.t(language, "conflictEditingUnderMaintenanceContact", contact.getFullName()));
    }

    // TODO: check computation values
    // TODO: check permissions

    return conflicts;
  }
}
