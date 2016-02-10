package org.sigmah.shared.dto;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

/**
 * OrgUnit banner DTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrgUnitBannerDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 6674394979745783738L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "OrgUnitBanner";
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public String getName() {
		return I18N.CONSTANTS.Admin_BANNER();
	}

	// Layout
	public LayoutDTO getLayout() {
		return get("layout");
	}

	public void setLayout(LayoutDTO layout) {
		set("layout", layout);
	}

	// Model
	public OrgUnitModelDTO getOrgUnitModel() {
		return get("oum");
	}

	public void setOrgUnitModel(OrgUnitModelDTO oum) {
		set("oum", oum);
	}
}
