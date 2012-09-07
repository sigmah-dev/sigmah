/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.domain.export;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/*
 * Contents of global export stored as CSV string
 * and linked to log
 * 
 * @author sherzod
 */
@Entity
@Table(name = "global_export_content")
public class GlobalExportContent implements Serializable {

	private static final long serialVersionUID = -3243791678694151703L;

	private Long id;
	private String projectModelName;
	private String csvContent;
	private GlobalExport globalExport;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "project_model_name", nullable = false, length = 8192)
	public String getProjectModelName() {
		return projectModelName;
	}

	public void setProjectModelName(String projectModelName) {
		this.projectModelName = projectModelName;
	}

	@Column(name = "csv_content", nullable = true, columnDefinition = "TEXT")
	public String getCsvContent() {
		return csvContent;
	}

	public void setCsvContent(String csvContent) {
		this.csvContent = csvContent;
	}

	@ManyToOne
	@JoinColumn(name = "global_export_id", nullable = false)
	public GlobalExport getGlobalExport() {
		return globalExport;
	}

	public void setGlobalExport(GlobalExport globalExport) {
		this.globalExport = globalExport;
	}

}
