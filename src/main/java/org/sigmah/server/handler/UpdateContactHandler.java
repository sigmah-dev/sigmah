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
import com.google.inject.persist.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;


import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Contact;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.DefaultContactFlexibleElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.service.ValueService;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.UpdateContact;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.value.TripletValueDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateContactHandler extends AbstractCommandHandler<UpdateContact, VoidResult> {
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateContactHandler.class);

  private final I18nServer i18nServer;
  private final ContactDAO contactDAO;
  private final ValueService valueService;

  @Inject
  public UpdateContactHandler(final I18nServer i18nServer, final ContactDAO contactDAO, final ValueService valueService) {
    this.i18nServer = i18nServer;
    this.contactDAO = contactDAO;
	this.valueService = valueService;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public VoidResult execute(UpdateContact cmd, UserExecutionContext context) throws CommandException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("[execute] Updates contact #" + cmd.getContactId() + " with following values #" + cmd.getValues().size() + " : " + cmd.getValues());
    }

    updateContact(cmd.getValues(), cmd.getContactId(), context, cmd.getComment());

    return null;
  }

  @Transactional(rollbackOn = CommandException.class)
  protected void updateContact(List<ValueEventWrapper> values, Integer contactId, UserExecutionContext context, String comment) throws CommandException {
    // This date must be the same for all the saved values !
    final Date historyDate = new Date();

    final User user = context.getUser();
    final Contact contact = contactDAO.findById(contactId);

    // Verify if the modifications conflicts with the contact state.
    final List<String> conflicts = searchForConflicts(contact, context);

    // Iterating over the value change events
    for (final ValueEventWrapper valueEvent : values) {

      // Event parameters.
      final FlexibleElementDTO source = valueEvent.getSourceElement();
      final FlexibleElement element = em().find(FlexibleElement.class, source.getId());
      final TripletValueDTO updateListValue = valueEvent.getTripletValue();
      final String updateSingleValue = valueEvent.getSingleValue();
	  
      final Set<Integer> multivaluedIdsValue = valueEvent.getMultivaluedIdsValue();
      final Integer iterationId = valueEvent.getIterationId();

      LOGGER.debug("[execute] Updates value of element #{} ({})", source.getId(), source.getEntityName());
      LOGGER.debug("[execute] Event of type {} with value {} and list value {} (iteration : {}).", valueEvent.getChangeType(), updateSingleValue, updateListValue, iterationId);

      if (source instanceof DefaultContactFlexibleElementDTO) {
		// Case of the default flexible element which values arent't stored
		// like other values. These values impact directly the contact.
        valueService.saveValue(updateSingleValue, historyDate, (DefaultContactFlexibleElement)element, contact, iterationId, user, comment);
      }
	  else if (updateSingleValue != null) {
	    valueService.saveValue(updateSingleValue, historyDate, element, contactId, iterationId, user, comment);
	  }
	  else if (multivaluedIdsValue != null) {
	    valueService.saveValue(multivaluedIdsValue, valueEvent, historyDate, element, contactId, iterationId, user, comment);
      }
      else if (updateListValue != null) {
		// Special case : this value is a part of a list which is the true value of the flexible element. (only used for
		// the TripletValue class for the moment)
		valueService.saveValue(updateListValue, valueEvent.getChangeType(), historyDate, element, contactId, iterationId, user, comment);
      }
	  else {
	    LOGGER.warn("Empty value event received for element #{} ({}) of container #{}.", source.getId(), source.getEntityName(), contactId);
	  }
	}
	  
    if (!conflicts.isEmpty()) {
      // A conflict was found.
      throw new UpdateConflictException(contact.toContainerInformation(), conflicts.toArray(new String[0]));
    }
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
