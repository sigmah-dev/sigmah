package org.sigmah.shared.dto;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

/**
 * OrgUnitDetailsDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class OrgUnitDetailsDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4611350969297280470L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "OrgUnitDetails";
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public String getName() {
		return I18N.CONSTANTS.Admin_ORGUNIT_DETAILS();
	}

	public void setName() {
		set("name", I18N.CONSTANTS.Admin_ORGUNIT_DETAILS());
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
