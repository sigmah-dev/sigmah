package org.sigmah.server.domain.value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.Deleteable;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.server.domain.util.EntityFilters;

/**
 * <p>
 * File domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.FILE_META_TABLE)
@FilterDefs({ @FilterDef(name = EntityFilters.HIDE_DELETED)
})
@Filters({ @Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.FILE_META_HIDE_DELETED_CONDITION)
})
public class File extends AbstractEntityId<Integer> implements Deleteable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -271699094058979365L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FILE_META_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.FILE_META_COLUMN_NAME, nullable = false, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	@NotNull
	private String name;

	@Column(name = EntityConstants.FILE_META_COLUMN_DATE_DELETED)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date dateDeleted;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "parentFile", cascade = CascadeType.ALL)
	@Filter(name = EntityFilters.HIDE_DELETED, condition = EntityFilters.FILE_META_HIDE_DELETED_CONDITION)
	private List<FileVersion> versions = new ArrayList<FileVersion>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("dateDeleted", dateDeleted);
	}

	/**
	 * Adds a new version of the current file.
	 */
	public void addVersion(FileVersion version) {
		version.setParentFile(this);
		versions.add(version);
	}

	@Override
	public void delete() {
		setDateDeleted(new Date());
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}

	/**
	 * Returns the last version (with the higher version number).
	 * 
	 * @return the last version.
	 */
	@Transient
	public FileVersion getLastVersion() {

		if (versions == null || versions.isEmpty()) {
			return null;
		}

		// Searches the max version number which identifies the last version.
		int index = 0;
		int maxVersionNumber = versions.get(index).getVersionNumber();
		for (int i = 1; i < versions.size(); i++) {
			if (versions.get(i).getVersionNumber() > maxVersionNumber) {
				index = i;
			}
		}

		return versions.get(index);
	}

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setVersions(List<FileVersion> versions) {
		this.versions = versions;
	}

	public List<FileVersion> getVersions() {
		return versions;
	}

	public Date getDateDeleted() {
		return this.dateDeleted;
	}

	public void setDateDeleted(Date date) {
		this.dateDeleted = date;
	}
}
