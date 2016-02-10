package org.sigmah.shared.command.result;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.base.EntityDTO;

/**
 * The {@link DeleteResult} result used to return the entities that have not been deleted with their corresponding
 * error(s).
 * 
 * @param <T>
 *          The entityDTO type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteResult<T extends EntityDTO<?>> implements Result {

	/**
	 * Delete error cause used to return a specific cause for a deletion error.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static class DeleteErrorCause implements Result {

		private String causeLabel;
		private String modelName;
		private Boolean defaultFlexibleElement;

		protected DeleteErrorCause() {
			// Serialization.
		}

		/**
		 * Initializes a new delete error cause.
		 * 
		 * @param causeLabel
		 *          The cause label.
		 */
		public DeleteErrorCause(String causeLabel) {
			this(causeLabel, null, null);
		}

		/**
		 * Specific constructor for {@code FlexibleElement} error cause.
		 * 
		 * @param elementLabel
		 *          The flexible element label.
		 * @param modelName
		 *          The flexible element parent model name.
		 * @param defaultFlexibleElement
		 *          Is it a default flexible element?
		 */
		public DeleteErrorCause(String elementLabel, String modelName, Boolean defaultFlexibleElement) {
			this.causeLabel = elementLabel;
			this.modelName = modelName;
			this.defaultFlexibleElement = defaultFlexibleElement;
		}

		/**
		 * Returns the delete error cause label.
		 * 
		 * @return The delete error cause label.
		 */
		public String getCauseLabel() {
			return causeLabel;
		}

		// --
		// Flexible element cause specific getters.
		// --

		public boolean isFlexibleElementError() {
			return modelName != null && defaultFlexibleElement != null;
		}

		public String getModelName() {
			return modelName;
		}

		public boolean isDefaultFlexibleElement() {
			return ClientUtils.isTrue(defaultFlexibleElement);
		}
	}

	/**
	 * The successfully deleted entities.
	 */
	private List<T> deletedEntities = new ArrayList<T>();

	/**
	 * The errors map.
	 */
	// Important: insertion-ordered map.
	private Map<T, List<DeleteErrorCause>> errors = new LinkedHashMap<T, List<DeleteErrorCause>>();

	/**
	 * Adds the given successfully {@code deletedEntity}.
	 * 
	 * @param deletedEntity
	 *          The successfully deleted entity.
	 */
	public void addDeleted(final T deletedEntity) {
		if (deletedEntity != null) {
			deletedEntities.add(deletedEntity);
		}
	}

	/**
	 * Adds the delete action error to the result.
	 * 
	 * @param entity
	 *          The {@link EntityDTO} that cannot be deleted.
	 * @param errorArguments
	 *          The error arguments.
	 */
	public void addError(final T entity, final DeleteErrorCause errorArguments) {

		if (errors.containsKey(entity)) {
			// Existing privacy group.
			errors.get(entity).add(errorArguments);

		} else {
			// New privacy group.
			final List<DeleteErrorCause> errorsArguments = new ArrayList<DeleteErrorCause>();
			errorsArguments.add(errorArguments);
			errors.put(entity, errorsArguments);
		}
	}

	/**
	 * Returns if the delete result has successfully deleted entities.
	 * 
	 * @return {@code true} if the delete result has successfully deleted entities, {@code false} otherwise.
	 */
	public boolean hasDeletedEntities() {
		return ClientUtils.isNotEmpty(deletedEntities);
	}

	/**
	 * Returns if the delete result has errors (entities not deleted due to one or multiple causes).
	 * 
	 * @return {@code true} if the delete result has errors (entities not deleted due to one or multiple causes),
	 *         {@code false} otherwise.
	 */
	public boolean hasErrors() {
		return ClientUtils.isNotEmpty(errors);
	}

	/**
	 * Returns the successfully deleted entities.
	 * 
	 * @return The successfully deleted entities.
	 */
	public List<T> getDeletedEntities() {
		return deletedEntities;
	}

	/**
	 * Returns the detected errors as a map of the entities not deleted with their error cause(s).
	 * 
	 * @return The detected errors as a map of the entities not deleted with their error cause(s).
	 */
	public Map<T, List<DeleteErrorCause>> getErrors() {
		return errors;
	}

}
