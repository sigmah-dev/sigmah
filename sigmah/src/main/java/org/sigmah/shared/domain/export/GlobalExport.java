package org.sigmah.shared.domain.export;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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

import org.sigmah.shared.domain.Organization;
import java.util.List;

/*
 * Logs auto-exports
 * 
 * @author sherzod
 */
@Entity
@Table(name = "global_export")
public class GlobalExport implements Serializable {

	private static final long serialVersionUID = -7953828858435786172L;

	private Long id;
	private Date date;
	private Organization organization;
	private List<GlobalExportContent> contents = new ArrayList<GlobalExportContent>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "generated_date", nullable = false)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@OneToMany(mappedBy = "globalExport", cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	public List<GlobalExportContent> getContents() {
		return contents;
	}

	public void setContents(List<GlobalExportContent> contents) {
		this.contents = contents;
	}

}
