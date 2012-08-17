package org.sigmah.shared.domain.export;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.shared.domain.Organization;

/*
 * Global export settings per organization
 * 
 * @author sherzod
 */
@Entity
@Table(name = "global_export_settings")
public class GlobalExportSettings implements Serializable {

	private static final long serialVersionUID = -2722884637221828205L;

	private Long id;
	private GlobalExportFormat exportFormat ; //xls, ods
	private Date lastExportDate;
	private Integer autoExportFrequency; // days
	private Integer autoDeleteFrequency; //months                
	private String locale;  
	private Organization organization;
	
	
	
	@Column(name = "export_format", nullable = true)
	@Enumerated(EnumType.STRING)
	public GlobalExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(
			GlobalExportFormat exportFormat) {
		this.exportFormat = exportFormat;
	} 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
 
	 
	@Column(name = "last_export_date")
	public Date getLastExportDate() {
		return lastExportDate;
	}

	public void setLastExportDate(Date lastExportDate) {
		this.lastExportDate = lastExportDate;
	}

	@Column(name = "auto_export_frequency")
	public Integer getAutoExportFrequency() {
		return autoExportFrequency;
	}

	public void setAutoExportFrequency(Integer autoExportFrequency) {
		this.autoExportFrequency = autoExportFrequency;
	}

	@Column(name = "auto_delete_frequency")
	public Integer getAutoDeleteFrequency() {
		return autoDeleteFrequency;
	}

	public void setAutoDeleteFrequency(Integer autoDeleteFrequency) {
		this.autoDeleteFrequency = autoDeleteFrequency;
	}

	@Column(name = "locale_string",length=4, nullable = false)
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@ManyToOne
	@JoinColumn(name = "organization_id", nullable = false)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
