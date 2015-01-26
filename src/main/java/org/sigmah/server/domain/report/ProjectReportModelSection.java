package org.sigmah.server.domain.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Project Report Model Section domain entity.
 * </p>
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_TABLE)
public class ProjectReportModelSection extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2023415671100683862L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_NAME, length = EntityConstants.PROJECT_REPORT_MODEL_SECTION_NAME_MAX_LENGTH)
	@Size(max = EntityConstants.PROJECT_REPORT_MODEL_SECTION_NAME_MAX_LENGTH)
	private String name;

	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_SORT_ORDER)
	private Integer index;

	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_NB_OF_TEXTAREA)
	private Integer numberOfTextarea;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	// FIXME Should be foreign key of 'projectmodelid'.
	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_PROJECT_MODEL_ID)
	private Integer projectModelId;

	// FIXME Should be foreign key of 'projectreportmodel'.
	@Column(name = EntityConstants.PROJECT_REPORT_MODEL_SECTION_COLUMN_PARENT_SECTION_MODEL_ID)
	private Integer parentSectionModelId;

	@OneToMany(mappedBy = "parentSectionModelId", orphanRemoval = true)
	@OrderBy("index ASC, id ASC")
	private List<ProjectReportModelSection> subSections;

	@OneToMany(mappedBy = "sectionId")
	@OrderBy("index ASC, id ASC")
	private List<KeyQuestion> keyQuestions;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("name", name);
		builder.append("index", index);
		builder.append("numberOfTextarea", numberOfTextarea);
	}

	/**
	 * Reset the identifiers of the object.
	 * 
	 * @param projectModelId
	 *          the project-report parent identifier.
	 * @param parentSectionModelId
	 *          project-report-section parent identifier.
	 * @param modelesReset
	 *          the map of the reseted objects (original object, transformed object).
	 * @param modelesImport
	 *          the list of object that have been transformed or are being transformed.
	 */
	public void resetImport(Integer projectModelId, Integer parentSectionModelId, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
		this.id = null;
		this.projectModelId = projectModelId;
		this.parentSectionModelId = parentSectionModelId;

		if (this.subSections != null) {
			for (ProjectReportModelSection projetModelSection : this.subSections) {
				if (!modelesReset.containsKey(projetModelSection)) {
					ProjectReportModelSection key = projetModelSection;
					projetModelSection.resetImport(null, this.id, modelesReset, modelesImport);
					modelesReset.put(key, projetModelSection);
				} else {
					projetModelSection = (ProjectReportModelSection) modelesReset.get(projetModelSection);
				}
			}
		}
		if (this.keyQuestions != null) {
			for (KeyQuestion keyQuestion : keyQuestions) {
				keyQuestion.resetImport(this.id, modelesReset, modelesImport);
			}
		}
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

	public Integer getParentSectionModelId() {
		return parentSectionModelId;
	}

	public void setParentSectionModelId(Integer parentSectionModelId) {
		this.parentSectionModelId = parentSectionModelId;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}

	public void setProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getNumberOfTextarea() {
		return numberOfTextarea;
	}

	public void setNumberOfTextarea(Integer numberOfTextarea) {
		this.numberOfTextarea = numberOfTextarea;
	}

	public List<ProjectReportModelSection> getSubSections() {
		return subSections;
	}

	public void setSubSections(List<ProjectReportModelSection> subSections) {
		this.subSections = subSections;
	}

	public List<KeyQuestion> getKeyQuestions() {
		return keyQuestions;
	}

	public void setKeyQuestions(List<KeyQuestion> keyQuestions) {
		this.keyQuestions = keyQuestions;
	}
}
