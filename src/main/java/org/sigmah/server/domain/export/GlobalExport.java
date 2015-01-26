package org.sigmah.server.domain.export;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Global Export domain entity.
 * </p>
 * <p>
 * Logs auto-exports.
 * </p>
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.GLOBAL_EXPORT_TABLE)
public class GlobalExport extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7953828858435786172L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.GLOBAL_EXPORT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.GLOBAL_EXPORT_COLUMN_DATE, nullable = false)
	@NotNull
	private Date date;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.GLOBAL_EXPORT_COLUMN_ORGANIZATION, nullable = false)
	@NotNull
	private Organization organization;

	@OneToMany(mappedBy = "globalExport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<GlobalExportContent> contents = new ArrayList<GlobalExportContent>();

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("date", date);
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<GlobalExportContent> getContents() {
		return contents;
	}

	public void setContents(List<GlobalExportContent> contents) {
		this.contents = contents;
	}

}
