package org.sigmah.server.domain.report;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Project Report Version domain entity.
 * </p>
 * <p>
 * Version of a project report.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_REPORT_VERSION_TABLE)
public class ProjectReportVersion extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2704394858349357250L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_ID)
	private Integer id;

	/**
	 * Version number of this copy. If null, it means this version is still a draft.
	 */
	@Column(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_VERSION)
	private Integer version;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_EDIT_DATE)
	private Date editDate;

	@Column(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_PHASE_NAME)
	@Size(max = EntityConstants.PROJECT_REPORT_VERSION_PHASE_NAME_MAX_LENGTH)
	private String phaseName;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(cascade = {
												CascadeType.MERGE,
												CascadeType.PERSIST,
												CascadeType.REFRESH
	})
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_REPORT_ID)
	private ProjectReport report;

	/**
	 * Author of this copy.
	 */
	@ManyToOne
	@JoinColumn(name = EntityConstants.PROJECT_REPORT_VERSION_COLUMN_USER_ID)
	private User editor;

	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
	@OrderBy(value = "sectionId, index ASC")
	private List<RichTextElement> texts;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("version", version);
		builder.append("editDate", editDate);
		builder.append("phaseName", phaseName);
	}

	public String getEditorShortName() {
		return User.getUserShortName(editor);
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

	public ProjectReport getReport() {
		return report;
	}

	public void setReport(ProjectReport report) {
		this.report = report;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getEditDate() {
		return editDate;
	}

	public void setEditDate(Date editDate) {
		this.editDate = editDate;
	}

	public User getEditor() {
		return editor;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public List<RichTextElement> getTexts() {
		return texts;
	}

	public void setTexts(List<RichTextElement> texts) {
		this.texts = texts;
	}
}
