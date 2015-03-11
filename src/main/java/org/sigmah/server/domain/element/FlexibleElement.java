package org.sigmah.server.domain.element;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.Historable;

/**
 * <p>
 * Flexible element domain entity.
 * </p>
 * <p>
 * Uses a joined inheritance to map the different types of flexible elements.<br/>
 * Each type of element is stored in a kind of sub-table.<br/>
 * Each element, regardless of the type, is created with an unique identifier. This identifier is unique for all the
 * flexible element types. An element is retrieved by join instructions on sub-tables and a constraint on the
 * identifier.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.FLEXIBLE_ELEMENT_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class FlexibleElement extends AbstractEntityId<Integer> implements Historable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8754613123116586106L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_LABEL, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String label;

	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_VALIDATES, nullable = false)
	@NotNull
	private Boolean validates = false;

	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_AMENDABLE, nullable = false)
	@NotNull
	private Boolean amendable = false;

	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_EXPORTABLE, nullable = false)
	@NotNull
	// exported to project synthesis sheet
	private Boolean exportable = true;

	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_GLOBALLY_EXPORTABLE, nullable = false)
	@NotNull
	// exported to a global projects list
	private Boolean globallyExportable = false;
	
	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_DISABLED_DATE, nullable = true)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date disabledDate;
	
	@Column(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_CREATION_DATE, nullable = true)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date creationDate = new Date();

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PRIVACY_GROUP_COLUMN_ID, nullable = true)
	private PrivacyGroup privacyGroup;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isHistorable() {
		// Doesn't manage history by default.
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String asHistoryToken(String value) {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String asHistoryToken(EntityId<?> value) {
		return value != null ? value.toString() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("label", label);
		builder.append("validates", validates);
		builder.append("amendable", amendable);
		builder.append("exportable", exportable);
		builder.append("globallyExportable", globallyExportable);
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport(boolean keepPrivacyGroups) {
		this.id = null;
		
		if(!keepPrivacyGroups) {
			// remove the privacy group from imported flexible elements.
			this.privacyGroup = null;
		}
	}

	/*
	 * When importing a project model from old releases, its flexible elements do not have export flags. Thus, they should
	 * be initialized, before element is persisted
	 */
	public void initializeExportFlags() {
		if (exportable == null)
			exportable = true;
		if (globallyExportable == null)
			globallyExportable = false;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isValidates() {
		return validates;
	}

	public void setValidates(boolean validates) {
		this.validates = validates;
	}

	public PrivacyGroup getPrivacyGroup() {
		return privacyGroup;
	}

	public void setPrivacyGroup(PrivacyGroup privacyGroup) {
		this.privacyGroup = privacyGroup;
	}

	public boolean isAmendable() {
		return amendable;
	}

	public void setAmendable(boolean amendable) {
		this.amendable = amendable;
	}

	public boolean isExportable() {
		return exportable;
	}

	public void setExportable(boolean exportable) {
		this.exportable = exportable;
	}

	public boolean isGloballyExportable() {
		return globallyExportable;
	}

	public void setGloballyExportable(boolean globallyExportable) {
		this.globallyExportable = globallyExportable;
	}

	public Date getDisabledDate() {
		return disabledDate;
	}

	public void setDisabledDate(Date disabledDate) {
		this.disabledDate = disabledDate;
	}

	/**
     * Returns the date on which this element was created.
     * @return The date on which this element was created.
     */
    public Date getCreationDate() {
		return creationDate;
	}

    public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
}
