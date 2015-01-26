package org.sigmah.server.domain.export;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Global Export Content domain entity.
 * </p>
 * <p>
 * Contents of global export stored as CSV string and linked to log.
 * </p>
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.GLOBAL_EXPORT_CONTENT_TABLE)
public class GlobalExportContent extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3243791678694151703L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_PROJECT_MODEL_NAME, nullable = false, length = EntityConstants.GLOBAL_EXPORT_CONTENT_PROJECT_MODEL_NAME_MAX_LENGTH)
	@NotNull
	@Size(max = EntityConstants.GLOBAL_EXPORT_CONTENT_PROJECT_MODEL_NAME_MAX_LENGTH)
	private String projectModelName;

	@Column(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_CSV_CONTENT, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String csvContent;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne
	@JoinColumn(name = EntityConstants.GLOBAL_EXPORT_CONTENT_COLUMN_GLOBAL_EXPORT_ID, nullable = false)
	@NotNull
	private GlobalExport globalExport;

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
		builder.append("projectModelName", projectModelName);
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

	public String getProjectModelName() {
		return projectModelName;
	}

	public void setProjectModelName(String projectModelName) {
		this.projectModelName = projectModelName;
	}

	public String getCsvContent() {
		return csvContent;
	}

	public void setCsvContent(String csvContent) {
		this.csvContent = csvContent;
	}

	public GlobalExport getGlobalExport() {
		return globalExport;
	}

	public void setGlobalExport(GlobalExport globalExport) {
		this.globalExport = globalExport;
	}

}
