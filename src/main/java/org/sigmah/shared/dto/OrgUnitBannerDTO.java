package org.sigmah.shared.dto;

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
