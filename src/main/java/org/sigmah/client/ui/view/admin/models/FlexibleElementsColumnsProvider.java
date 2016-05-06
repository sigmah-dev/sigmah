package org.sigmah.client.ui.view.admin.models;

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
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.util.ColumnProviders;
import org.sigmah.shared.dto.element.DefaultContactFlexibleElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.DefaultContactFlexibleElementType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ElementTypeEnum;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Provides flexible elements admin grid columns configuration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
abstract class FlexibleElementsColumnsProvider {

	/**
	 * Returns <code>true</code> if the parent model is editable.
	 * 
	 * @return <code>true</code> if the parent model is editable, <code>false</code> otherwise.
	 */
	protected abstract boolean isEditable();
	
	/**
	 * Returns the {@link GridEventHandler} implementation.
	 * 
	 * @return The {@link GridEventHandler} implementation.
	 */
	protected abstract GridEventHandler<FlexibleElementDTO> getGridEventHandler();

	/**
	 * Gets the columns model for the flexible elements admin grid.
	 * 
	 * @return The columns model for the flexible elements admin grid.
	 */
	public ColumnModel getColumnModel(boolean hasBanner, boolean hasCard) {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Label column.
		// --

		ColumnConfig column = new ColumnConfig(FlexibleElementDTO.LABEL, I18N.CONSTANTS.adminFlexibleName(), 250);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				final String label = getLabel(model);

				if (model.isDisabled()) {
					return ColumnProviders.renderDisabled(label);
				}

				if (!isEditable()) {
					return ColumnProviders.renderText(label);
				}

				return ColumnProviders.renderLink(label, new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						model.set(FlexibleElementsAdminPresenter.ON_GROUP_CLICK_EVENT_KEY, null);
						getGridEventHandler().onRowClickEvent(model);
					}

				});
			}
		});
		configs.add(column);

		// --
		// Type column.
		// --

		column = new ColumnConfig("type", I18N.CONSTANTS.adminFlexibleType(), 125);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				return ColumnProviders.renderText(ElementTypeEnum.getName(model.getElementType()));
			}
		});
		configs.add(column);

		// --
		// Validates column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.VALIDATES, I18N.CONSTANTS.adminFlexibleCompulsory(), 50);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				return ColumnProviders.renderBoolean(model.getValidates(), I18N.CONSTANTS.adminFlexibleCompulsory());
			}
		});
		configs.add(column);

		// --
		// Privacy group column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.PRIVACY_GROUP, I18N.CONSTANTS.adminProfilesPrivacyGroups(), 125);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				final PrivacyGroupDTO privacy = model.getPrivacyGroup();
				return ColumnProviders.renderText(privacy != null ? privacy.getTitle() : null);
			}
		});
		configs.add(column);

		// --
		// Amendable column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.AMENDABLE, I18N.CONSTANTS.partOfProjectCore(), 50);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				return ColumnProviders.renderBoolean(model.getAmendable(), I18N.CONSTANTS.partOfProjectCore());
			}
		});
		configs.add(column);

		// --
		// Exportable column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.EXPORTABLE, I18N.CONSTANTS.adminFlexibleExportable(), 50);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				return ColumnProviders.renderBoolean(model.getExportable(), I18N.CONSTANTS.adminFlexibleExportable());
			}
		});
		configs.add(column);


		if (hasBanner || hasCard) {
			String bannerTitle = hasBanner ? I18N.CONSTANTS.Admin_BANNER() : I18N.CONSTANTS.Admin_CARD();
			String bannerPositionTitle = hasBanner ? I18N.CONSTANTS.adminFlexibleBannerPosition() : I18N.CONSTANTS.adminFlexibleCardPosition();
			// --
			// Banner column.
			// --

			column = new ColumnConfig(FlexibleElementDTO.BANNER, bannerTitle, 50);
			column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

				@Override
				public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
														 final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

					return ColumnProviders.renderBoolean(model.getBannerConstraint() != null, I18N.CONSTANTS.Admin_BANNER());
				}
			});
			configs.add(column);

			// --
			// Banner position column.
			// --

			column = new ColumnConfig("bannerPos", bannerPositionTitle, 50);
			column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

				@Override
				public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
														 final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

					final LayoutConstraintDTO bannerConstraint = model.getBannerConstraint();
					return ColumnProviders.renderText(bannerConstraint != null ? bannerConstraint.getSortOrder() : null);
				}
			});
			configs.add(column);
		}

		// --
		// Container column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.CONTAINER, I18N.CONSTANTS.adminFlexibleContainer(), 120);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				final BaseModelData container = model.getContainerModel();
				return ColumnProviders.renderText(container.get("name"));
			}

		});
		configs.add(column);

		// --
		// Group column.
		// --

		column = new ColumnConfig(FlexibleElementDTO.GROUP, I18N.CONSTANTS.adminFlexibleGroup(), 200);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				final LayoutGroupDTO group = model.getGroup();

				if (!isEditable()) {
					return ColumnProviders.renderText(group.getTitle());
				}

				return ColumnProviders.renderLink(group.getTitle(), new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						model.set(FlexibleElementsAdminPresenter.ON_GROUP_CLICK_EVENT_KEY, Boolean.TRUE);
						getGridEventHandler().onRowClickEvent(model);
					}

				});
			}
		});
		configs.add(column);

		// --
		// Order column.
		// --

		column = new ColumnConfig("order", I18N.CONSTANTS.adminFlexibleOrderInGroup(), 50);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<FlexibleElementDTO> store, final Grid<FlexibleElementDTO> grid) {

				return ColumnProviders.renderText(model.getConstraint().getSortOrder());
			}
		});
		configs.add(column);

		return new ColumnModel(configs);
	}

	private String getLabel(FlexibleElementDTO model) {
		if (model.getElementType() == null) {
			return model.getLabel();
		}

		switch (model.getElementType()) {
			case DEFAULT:
				return DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) model).getType());
			case DEFAULT_CONTACT:
				return DefaultContactFlexibleElementType.getName(((DefaultContactFlexibleElementDTO) model).getType());
			default:
				return model.getLabel();
		}
	}
}
