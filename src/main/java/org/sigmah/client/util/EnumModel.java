package org.sigmah.client.util;

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


import org.sigmah.shared.Language;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.TextAreaType;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * {@link ModelData} implementation for enum values.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <E> Enum type
 */
public final class EnumModel<E extends Enum<E>> extends BaseModelData {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6650862290025436945L;

	/**
	 * Enum {@link ModelData} value field key.
	 */
	public static final String VALUE_FIELD = "value";

	/**
	 * Enum {@link ModelData} display field key.
	 */
	public static final String DISPLAY_FIELD = "display";

	/**
	 * The inner enum value.
	 */
	private final E enumValue;

	/**
	 * Initializes a new {@code EnumModel} for the given {@code language}.
	 * 
	 * @param language
	 *          The language.
	 */
	@SuppressWarnings("unchecked")
	public EnumModel(final Language language) {
		this((E) language, Language.i18n(language));
	}

	/**
	 * Initializes a new {@code EnumModel} for the given {@code privacyGroupPermission}.
	 * 
	 * @param privacyGroupPermission
	 *          The privacyGroupPermission.
	 */
	@SuppressWarnings("unchecked")
	public EnumModel(final PrivacyGroupPermissionEnum privacyGroupPermission) {
		this((E) privacyGroupPermission, PrivacyGroupPermissionEnum.getName(privacyGroupPermission));
	}

	/**
	 * Initializes a new {@code EnumModel} for the given {@code projectModelStatus}.
	 * 
	 * @param projectModelStatus
	 *          The project model status.
	 */
	@SuppressWarnings("unchecked")
	public EnumModel(final ProjectModelStatus projectModelStatus) {
		this((E) projectModelStatus, ProjectModelStatus.getName(projectModelStatus));
	}

	@SuppressWarnings("unchecked")
	public EnumModel(ContactModelType contactModelType) {
		this((E) contactModelType, ContactModelType.getName(contactModelType));
	}

	/**
	 * Initializes a new {@code EnumModel} for the given {@code elementType}.
	 * 
	 * @param elementType
	 *          The flexible element type.
	 */
	@SuppressWarnings("unchecked")
	public EnumModel(final ElementTypeEnum elementType) {
		this((E) elementType, ElementTypeEnum.getName(elementType));
	}

	/**
	 * Initializes a new {@code EnumModel} for the given {@code textAreaType}.
	 * 
	 * @param textAreaType
	 *          The text area type.
	 */
	@SuppressWarnings("unchecked")
	public EnumModel(final TextAreaType textAreaType) {
		this((E) textAreaType, TextAreaType.getName(textAreaType));
	}

	/**
	 * Initializes a new {@code EnumModel} for the given {@code enumValue}.
	 * 
	 * @param enumValue
	 *          The enum instance.
	 * @param displayValue
	 *          The display value.
	 */
	public EnumModel(final E enumValue, final String displayValue) {
		this.enumValue = enumValue;
		if (enumValue != null) {
			set(VALUE_FIELD, enumValue.name());
			set(DISPLAY_FIELD, displayValue);
		}
	}

	/**
	 * Returns the enum instance associated to the current model.
	 * 
	 * @return The enum instance associated to the current model.
	 */
	public E getEnum() {
		return enumValue;
	}

	/**
	 * Utility method providing a null-safe access to the given {@code enumModel} inner enum value.
	 * 
	 * @param <E> Enum type.
	 * @param enumModel
	 *          The enum model instance, may be {@code null}.
	 * @return The given {@code enumModel} inner enum value, or {@code null}.
	 */
	public static <E extends Enum<E>> E getEnum(final EnumModel<E> enumModel) {
		return enumModel != null ? enumModel.getEnum() : null;
	}

}
