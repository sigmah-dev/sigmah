package org.sigmah.shared.dto;

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * Projection DTO for the {@link org.sigmah.server.domain.Site} domain object, including its
 * {@link org.sigmah.server.domain.Location Location}, and {@link org.sigmah.server.domain.ReportingPeriod
 * ReportingPeriod} totals.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class SiteDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8005793321070591202L;

	public static final String ENTITY_NAME = "Site";
	
	public static final String ACTIVITY_ID = "activityId";
	public static final String DATABASE_ID = "databaseId";
	public static final String DATE_1 = "date1";
	public static final String DATE_2 = "date2";
	public static final String LOCATION_NAME = "locationName";
	public static final String LOCATION_AXE = "locationAxe";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String PARTNER = "partner";
	public static final String COMMENTS = "comments";
	
	public SiteDTO() {
	}

	/**
	 * Constucts an empty SiteDTO with the given id
	 *
	 * @param id
	 *          the siteId
	 */
	public SiteDTO(int id) {
		setId(id);
	}

	/**
	 * Constructs a shallow copy of the given SiteDTO instance
	 *
	 * @param site
	 *          the object to copy
	 */
	public SiteDTO(SiteDTO site) {
		super(site.getProperties());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(ACTIVITY_ID, getActivityId());
		builder.append(DATABASE_ID, getDatabaseId());
		builder.append(DATE_1, getDate1());
		builder.append(DATE_2, getDate2());
		builder.append(LOCATION_NAME, getLocationName());
		builder.append(LOCATION_AXE, getLocationAxe());
		builder.append(X, getX());
		builder.append(Y, getY());
	}

	/**
	 * Sets this site's id
	 *
	 * @param id
	 */
	@Override
	public void setId(Integer id) {
		set(ID, id);
	}

	/**
	 * @return this site's id
	 */
	@Override
	public Integer getId() {
		return (Integer) get(ID);
	}

	/**
	 * @return the id of the Activity to which this Site belongs
	 */
	public Integer getActivityId() {
		return get(ACTIVITY_ID);
	}

	/**
	 * Sets the id of Activity to which this Site belongs
	 *
	 * @param id
	 */
	public void setActivityId(Integer id) {
		set(ACTIVITY_ID, id);
	}

	public Integer getDatabaseId() {
		return get(DATABASE_ID);
	}

	public void setDatabaseId(Integer id) {
		set(DATABASE_ID, id);
	}

	/**
	 * @return the beginning of work at this Site
	 */
	public Date getDate1() {
		return get(DATE_1);
	}

	/**
	 * Sets the beginning of work at this Site
	 *
	 * @param date1
	 */
	public void setDate1(Date date1) {
		set(DATE_1, date1);
	}

	/**
	 * @return the end of work at this Site
	 */
	public Date getDate2() {
		return get(DATE_2);
	}

	/**
	 * Sets the end of work at this Site
	 *
	 * @param date2
	 */
	public void setDate2(Date date2) {
		set(DATE_2, date2);
	}

	/**
	 * @return the name of the Partner who owns this Site
	 */
	public String getPartnerName() {
		PartnerDTO partner = getPartner();
		if (partner == null) {
			return null;
		}

		return partner.getName();
	}

	/**
	 * @return the instance of the Partner who owns this Site
	 */
	public PartnerDTO getPartner() {
		return get(PARTNER);
	}

	/**
	 * Sets the partner who owns this Site
	 * 
	 * @param partner
	 */
	public void setPartner(PartnerDTO partner) {
		set(PARTNER, partner);
	}

	/**
	 * Sets the name of Location of this Site. See {@link org.sigmah.server.domain.Location#getName()}
	 * 
	 * @param name
	 *          the name of the location.
	 */
	public void setLocationName(String name) {
		set(LOCATION_NAME, name);
	}

	/**
	 * @return the name of the Location of the Site
	 */
	public String getLocationName() {
		return get(LOCATION_NAME);
	}

	/**
	 * @return the "axe routier" on which the Location of the Site lies
	 */
	public String getLocationAxe() {
		return get(LOCATION_AXE);
	}

	/**
	 * Sets the axe routier on which the Location of the Site lies
	 * 
	 * @param name
	 */
	public void setLocationAxe(String name) {
		set(LOCATION_AXE, name);
	}

	public void setAdminEntity(int levelId, AdminEntityDTO value) {
		set(AdminLevelDTO.getPropertyName(levelId), value);
	}

	public AdminEntityDTO getAdminEntity(int levelId) {
		return get(AdminLevelDTO.getPropertyName(levelId));
	}

	public Object getAdminEntityName(int levelId) {
		AdminEntityDTO entity = getAdminEntity(levelId);
		if (entity == null) {
			return null;
		}

		return entity.getName();
	}

	/**
	 * Sets the X (longitudinal) coordinate of this Site
	 * 
	 * @param x
	 *          the longitude, in degrees
	 */
	public void setX(Double x) {
		set(X, x);
	}

	/**
	 * @return the X (longitudinal) coordinate of this Site, or null if a coordinate has not been set.
	 */
	public Double getX() {
		return get(X);
	}

	/**
	 * @return the Y (latitudinal) coordinate of this Site in degrees, or null if a coordinate has not been set.
	 */
	public Double getY() {
		return get(Y);
	}

	/**
	 * Sets the Y (latitudinal) coordinate of this Site in degrees
	 *
	 * @param y
	 *          latitude in degrees
	 */
	public void setY(Double y) {
		set(Y, y);
	}

	/**
	 * @return true if this Site has non-null x and y coordinates.
	 */
	public boolean hasCoords() {
		return get(X) != null && get(Y) != null;
	}

	/**
	 * Sets the value for the given Attribute of this Site
	 * 
	 * @param attributeId
	 *          the Id of the attribute
	 * @param value
	 */
	public void setAttributeValue(int attributeId, Boolean value) {
		set(AttributeDTO.getPropertyName(attributeId), value);
	}

	/**
	 * Sets the (total) value of the given Indicator of this Site
	 *
	 * @param indicatorId
	 *          the Id of the indicator
	 * @param value
	 *          the total value for all ReportingPeriods
	 */
	public void setIndicatorValue(int indicatorId, Double value) {
		set(IndicatorDTO.getPropertyName(indicatorId), value);
	}

	/**
	 * @param indicatorId
	 * @return the total value of the given indicator for this Site, across all ReportingPeriods
	 */
	public Double getIndicatorValue(int indicatorId) {
		return get(IndicatorDTO.getPropertyName(indicatorId));
	}

	/**
	 * @param indicator
	 * @return the total value of the given indicator for this Site, across all ReportingPeriods
	 */
	public Double getIndicatorValue(IndicatorDTO indicator) {
		return getIndicatorValue(indicator.getId());
	}

	/**
	 * Sets the plain text comments for this Site
	 *
	 * @param comments
	 *          comments in plain-text format
	 */
	public void setComments(String comments) {
		set(COMMENTS, comments);
	}

	/**
	 * @return the plain-text comments for this Site
	 */
	public String getComments() {
		return get(COMMENTS);
	}

	/**
	 * @param attributeId
	 * @return the value of the given attribute for this Site
	 */
	public Boolean getAttributeValue(int attributeId) {
		return get(AttributeDTO.getPropertyName(attributeId));
	}

	/**
	 * @return true if this Site has a non-null ID
	 */
	public boolean hasId() {
		return get(ID) != null;
	}

}
