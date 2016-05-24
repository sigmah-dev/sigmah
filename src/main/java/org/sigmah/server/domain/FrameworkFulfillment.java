package org.sigmah.server.domain;

import java.util.List;

import javax.persistence.*;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

@Entity
@Table(name = EntityConstants.FRAMEWORK_FULFILLMENT_TABLE)
public class FrameworkFulfillment extends AbstractEntityId<Integer> {

	private static final long serialVersionUID = 1505225144046751205L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.FRAMEWORK_FULFILLMENT_COLUMN_ID)
	private Integer id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.FRAMEWORK_COLUMN_ID, nullable = false)
	private Framework framework;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = EntityConstants.PROJECT_MODEL_COLUMN_ID, nullable = false)
	private ProjectModel projectModel;

	@OneToMany(mappedBy = "frameworkFulfillment")
	private List<FrameworkElementImplementation> frameworkElementImplementations;

	@Column(name = EntityConstants.FRAMEWORK_FULFILLMENT_COLUMN_REJECT_REASON, nullable = true)
	private String rejectReason;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public Framework getFramework() {
		return framework;
	}

	public void setFramework(Framework framework) {
		this.framework = framework;
	}

	public ProjectModel getProjectModel() {
		return projectModel;
	}

	public void setProjectModel(ProjectModel projectModel) {
		this.projectModel = projectModel;
	}

	public List<FrameworkElementImplementation> getFrameworkElementImplementations() {
		return frameworkElementImplementations;
	}

	public void setFrameworkElementImplementations(List<FrameworkElementImplementation> frameworkElementImplementations) {
		this.frameworkElementImplementations = frameworkElementImplementations;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
}
