package org.sigmah.shared.command;

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
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;

/**
 * Modify the value of one or more flexible elements.
 *
 * Although this command is named <code>UpdateProject</code>, it can be used
 * to modify <code>OrgUnits</code> as well.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateProject extends AbstractCommand<VoidResult> {

	private int projectId;
	private ArrayList<ValueEventWrapper> values = new ArrayList<ValueEventWrapper>();
	private String comment;

	public UpdateProject() {
		// Serialization.
	}

	public UpdateProject(int projectId, List<ValueEvent> values) {
		this(projectId, values, null);
	}
	
	public UpdateProject(int projectId, List<ValueEvent> values, String comment) {
		this.projectId = projectId;
		this.comment = comment;

		this.values.clear();

		final HashMap<Integer, ValueEvent> basicValues = new HashMap<Integer, ValueEvent>();
		final HashMap<ListEntityDTOKey, ValueEvent> listValues = new HashMap<ListEntityDTOKey, ValueEvent>();
		final HashMap<ListEntityDTOKey, ValueEvent> editedValues = new HashMap<ListEntityDTOKey, ValueEvent>();
		final HashMap<Integer, List<ValueEvent>> multivaluedValues = new HashMap<Integer, List<ValueEvent>>();

		for (final ValueEvent event : values) {

			// Manages basic values changes.
			if (event.getSingleValue() != null) {
				// Keep only the last modification to avoid events repetition.
				basicValues.put(event.getSourceElement().getId(), event);
			} else if (event.getMultivaluedIdsValue() != null && !event.getMultivaluedIdsValue().isEmpty()) {
				if (!multivaluedValues.containsKey(event.getSourceElement().getId())) {
					multivaluedValues.put(event.getSourceElement().getId(), new ArrayList<ValueEvent>());
				}
				multivaluedValues.get(event.getSourceElement().getId()).add(event);
			}
			// Manages the elements which are a part of a list.
			else {
				final TripletValueDTO element = event.getTripletValue();

				// Manages only elements which are not stored on the data layer to keep only the last state of each element
				// before sending events to the server.
				if (element.getId() == null) {

					switch (event.getChangeType()) {
						case ADD:
							listValues.put(new ListEntityDTOKey(event.getSourceElement().getId(), element.getIndex()), event);
							break;
						case REMOVE:
							listValues.remove(new ListEntityDTOKey(event.getSourceElement().getId(), element.getIndex()));
							break;
						case EDIT:
							listValues.put(new ListEntityDTOKey(event.getSourceElement().getId(), element.getIndex()), event);
							break;
						default:
							break;
					}
				} else {

					// Keep only the last state of each edited element before sending events to the server.
					switch (event.getChangeType()) {
						case EDIT:
							editedValues.put(new ListEntityDTOKey(event.getSourceElement().getId(), element.getIndex()), event);
							break;
						default:
							this.values.add(wrapEvent(event, event.isProjectCountryChanged()));
							break;
					}
				}
			}
		}

		for (final ValueEvent event : basicValues.values()) {
			this.values.add(wrapEvent(event, event.isProjectCountryChanged()));
		}

		// Store each event for new elements as an 'add' event with the last state of the element.
		for (final ValueEvent event : listValues.values()) {
			this.values.add(wrapEvent(new ValueEvent(event.getSourceElement(), event.getTripletValue(), ValueEventChangeType.ADD), event.isProjectCountryChanged()));
		}

		for (final ValueEvent event : editedValues.values()) {
			this.values.add(wrapEvent(new ValueEvent(event.getSourceElement(), event.getTripletValue(), ValueEventChangeType.EDIT), event.isProjectCountryChanged()));
		}

		for (final List<ValueEvent> events : multivaluedValues.values()) {
			for (final ValueEvent event : events) {
				this.values.add(wrapEvent(new ValueEvent(event.getSourceElement(), event.getMultivaluedIdsValue(), event.getChangeType()), event.isProjectCountryChanged()));
			}
		}
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public List<ValueEventWrapper> getValues() {
		return values;
	}

	public void setValues(List<ValueEvent> values) {
		this.values.clear();
		this.values.addAll(wrapEvents(values));
	}

	/**
	 * Add already wrapped values.
	 * Used to recreate an existing UpdateProject command by the offline mode.
	 * @param values Wrapped values to add to this command.
	 */
	public void setValueEventWrappers(List<ValueEventWrapper> values) {
		this.values.clear();
		this.values.addAll(values);
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	/**
	 * Wraps a list of events.
	 * 
	 * @param events
	 *          The events list.
	 * @return The events wrapped list.
	 */
	private static List<ValueEventWrapper> wrapEvents(List<ValueEvent> events) {
		final ArrayList<ValueEventWrapper> wrappers = new ArrayList<ValueEventWrapper>();
		for (ValueEvent event : events) {
			wrappers.add(wrapEvent(event, event.isProjectCountryChanged()));
		}
		return wrappers;
	}

	/**
	 * Wraps a event.
	 * 
	 * @param event
	 *          The event.
	 * @return The event wrapped.
	 */
	private static ValueEventWrapper wrapEvent(ValueEvent event, boolean isProjectCountryChange) {
		final ValueEventWrapper wrapper = new ValueEventWrapper();
		wrapper.setSourceElement(event.getSourceElement());
		wrapper.setSingleValue(event.getSingleValue());
		wrapper.setMultivaluedIdsValue(event.getMultivaluedIdsValue());
		wrapper.setTripletValue(event.getTripletValue());
		wrapper.setChangeType(event.getChangeType());
		wrapper.setProjectCountryChanged(isProjectCountryChange);

		return wrapper;
	}

	private static class ListEntityDTOKey {

		private int flexibleElement;
		private int index;

		public ListEntityDTOKey(int flexibleElement, int index) {
			super();
			this.flexibleElement = flexibleElement;
			this.index = index;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + flexibleElement;
			result = prime * result + index;
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ListEntityDTOKey other = (ListEntityDTOKey) obj;
			if (flexibleElement != other.flexibleElement)
				return false;
			if (index != other.index)
				return false;
			return true;
		}

	}
}
