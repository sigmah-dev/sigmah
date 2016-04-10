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

import java.util.EnumMap;
import java.util.Map.Entry;

import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.presenter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.Image;

/**
 * <p>
 * {@link ProjectModelType} corresponding radios form field (with proper icons).
 * </p>
 * <p>
 * To listen to field events, use {@link #addListener(EventType, Listener)} method.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProjectModelTypeField extends MultiField<ProjectModelType> {

	private final EnumMap<ProjectModelType, Radio> radios;
	private final RadioGroup radioGroup;

	/**
	 * Initializes the field with the given arguments and default {@link Orientation#HORIZONTAL}.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 * @param mandatory
	 *          Is the field mandatory?
	 * @param projectModelTypes
	 *          The specific {@link ProjectModelType} to show. If {@code null} or empty, all types are shown.
	 */
	public ProjectModelTypeField(final String fieldLabel, final boolean mandatory, final ProjectModelType... projectModelTypes) {
		this(fieldLabel, mandatory, null, projectModelTypes);
	}

	/**
	 * Initializes the field with the given arguments.
	 * 
	 * @param fieldLabel
	 *          The field label.
	 * @param mandatory
	 *          Is the field mandatory?
	 * @param orientation
	 *          The orientation. If {@code null}, default {@link Orientation#HORIZONTAL} is set.
	 * @param projectModelTypes
	 *          The specific {@link ProjectModelType} to show. If {@code null} or empty, all types are shown.
	 */
	public ProjectModelTypeField(final String fieldLabel, final boolean mandatory, final Orientation orientation, ProjectModelType... projectModelTypes) {

		this.radios = new EnumMap<ProjectModelType, Radio>(ProjectModelType.class);
		this.radioGroup = Forms.radioGroup(null, "project-model-types", orientation != null ? orientation : Orientation.HORIZONTAL);

		radioGroup.setSelectionRequired(mandatory);
		radioGroup.setFireChangeEventOnSetValue(true);

		if (ClientUtils.isEmpty(projectModelTypes)) {
			projectModelTypes = ProjectModelType.values();
		}

		for (final ProjectModelType projectModelType : projectModelTypes) {

			final Radio radio = Forms.radio(null, null, null, Boolean.FALSE, "project-model-type-radio");
			radio.setFireChangeEventOnSetValue(true);

			final Image icon = FundingIconProvider.getProjectTypeIcon(projectModelType, IconSize.SMALL).createImage();
			icon.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			icon.getElement().getStyle().setMarginTop(-2, Unit.PX);
			icon.getElement().getStyle().setPaddingRight(6, Unit.PX);

			radio.setBoxLabel(icon.toString() + ProjectModelType.getName(projectModelType) + "(" + ProjectModelType.counter + ")");

			radioGroup.add(radio);
			radios.put(projectModelType, radio);
		}

		add(radioGroup);
		setFieldLabel(fieldLabel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final EventType eventType, final Listener<? extends BaseEvent> listener) {
		radioGroup.addListener(eventType, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModelType getValue() {

		final Radio selectedRadio = radioGroup.getValue();

		if (selectedRadio == null) {
			return null;
		}

		for (final Entry<ProjectModelType, Radio> entry : radios.entrySet()) {
			if (selectedRadio.equals(entry.getValue())) {
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(final ProjectModelType value) {

		if (value == null) {
			radioGroup.setValue(null);
			return;
		}

		radioGroup.setValue(radios.get(value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return radioGroup.isValid();
	}

}
