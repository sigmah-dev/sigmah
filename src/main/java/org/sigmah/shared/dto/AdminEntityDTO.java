package org.sigmah.shared.dto;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * One-to-one DTO for the {@link org.sigmah.server.domain.AdminEntity AdminEntity} domain object.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class AdminEntityDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4996539919456037792L;

	public static final String ENTITY_NAME = "AdminEntity";
	
	public static final String NAME = "name";
	public static final String LEVEL_ID = "levelId";
	public static final String PARENT_ID = "parentId";
	public static final String BOUNDS = "bounds";
	
	private BoundingBoxDTO bounds;

	public AdminEntityDTO() {
	}

	/**
	 * @param levelId
	 *          the id of the AdminLevel to which this AdminEntity belongs
	 * @param id
	 *          the id of this AdminEntity
	 * @param name
	 *          the name of this AdminEntity
	 */
	public AdminEntityDTO(int levelId, int id, String name) {
		setId(id);
		setName(name);
		setLevelId(levelId);
	}

	/**
	 * @param levelId
	 *          the id of the AdminLevel to which this AdminEntity belongs
	 * @param id
	 *          the id of this AdminEntity
	 * @param parentId
	 *          the id of this AdminEntity's parent
	 * @param name
	 *          this AdminEntity's name
	 */
	public AdminEntityDTO(int levelId, int id, int parentId, String name) {
		setId(id);
		setParentId(parentId);
		setName(name);
		setLevelId(levelId);
	}

	/**
	 * @param levelId
	 *          the id of the AdminLevel to which this AdminEntity belongs
	 * @param id
	 *          the id of this AdminEntity
	 * @param name
	 *          the name of this AdminEntity
	 * @param bounds
	 *          the geographing BoundingBox of this AdminEntity
	 */
	public AdminEntityDTO(int levelId, int id, String name, BoundingBoxDTO bounds) {
		setId(id);
		setName(name);
		setLevelId(levelId);
		setBounds(bounds);
	}

	/**
	 * @param levelId
	 *          the id of the AdminLevel to which this AdminEntity belongs
	 * @param id
	 *          the id of this AdminEntity
	 * @param parentId
	 *          the id of this AdminEntity's parent
	 * @param name
	 *          the name of this AdminEntity
	 * @param bounds
	 *          the geographing BoundingBox of this AdminEntity
	 */
	public AdminEntityDTO(int levelId, int id, int parentId, String name, BoundingBoxDTO bounds) {
		setId(id);
		setLevelId(levelId);
		setParentId(parentId);
		setName(name);
		setBounds(bounds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(LEVEL_ID, getLevelId());
		builder.append(PARENT_ID, getParentId());
		builder.append(BOUNDS, getBounds());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * Sets this AdminEntity's id
	 */
	public void setId(Integer id) {
		set("id", id);
	}

	/**
	 * @return this AdminEntity's id
	 */
	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	/**
	 * @return this AdminEntity's name
	 */
	public String getName() {
		return get(NAME);
	}

	/**
	 * Sets this AdminEntity's name
	 * @param name Then name to set.
	 */
	public void setName(String name) {
		set(NAME, name);
	}

	/**
	 * @return the id of this AdminEntity's corresponding {@link org.sigmah.server.domain.AdminLevel}.
	 */
	public int getLevelId() {
		return (Integer) get(LEVEL_ID);
	}

	/**
	 * Sets the id of the AdminLevel to which this AdminEntity belongs
	 * @param levelId The admin level to set.
	 */
	public void setLevelId(int levelId) {
		set(LEVEL_ID, levelId);
	}

	/**
	 * Sets the id of this AdminEntity's parent.
	 * @param value The parent id to set.
	 */
	public void setParentId(Integer value) {
		set(PARENT_ID, value);
	}

	/**
	 * @return the id of this AdminEntity's corresponding parent AdminEntity
	 */
	public Integer getParentId() {
		return get(PARENT_ID);
	}

	/**
	 * @return true if this AdminEntity has non-null bounds
	 */
	public boolean hasBounds() {
		return getBounds() != null;
	}

	/**
	 * @return the geographic BoundingBoxDTO of this AdminEntity
	 */
	public BoundingBoxDTO getBounds() {
		return bounds;
	}

	/**
	 * Sets the BoundingBoxDTO of this AdminEntity.
	 * @param bounds The bounding box to set.
	 */
	public void setBounds(BoundingBoxDTO bounds) {
		this.bounds = bounds;
	}

	/**
	 * Gets the property name for a given AdminLevel when AdminEntities are stored in pivoted form.
	 *
	 * @param levelId
	 *          the id of the AdminLevel
	 * @return the property name
	 */
	public static String getPropertyName(int levelId) {
		return AdminLevelDTO.getPropertyName(levelId);
	}

	/**
	 * @return the property name used for this AdminEntity's AdminLevel when stored in pivoted form
	 */
	public String getPropertyName() {
		return AdminLevelDTO.getPropertyName(this.getLevelId());
	}

}
