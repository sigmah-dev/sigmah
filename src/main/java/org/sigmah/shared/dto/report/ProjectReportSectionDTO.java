package org.sigmah.shared.dto.report;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;

/**
 * Section of a project report.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectReportSectionDTO implements ProjectReportContent {

	private Integer id;
	private String name;
	private List<ProjectReportContent> children;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("id", getId());
		builder.append("name", getName());

		return builder.toString();
	}

	public List<ProjectReportContent> getChildren() {
		return children;
	}

	public void setChildren(List<ProjectReportContent> children) {
		this.children = children;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
