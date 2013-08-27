package org.sigmah.shared.domain.importation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.sigmah.shared.domain.Deleteable;

/**
 * Describes an importation schema
 * 
 * @author gjb
 * 
 */
@Entity
@Table(name = "importation_scheme")
@Filter(name = "hideDeleted", condition = "DateDeleted is null")
public class ImportationScheme implements Serializable, Deleteable {

	private static final long serialVersionUID = -9215969897646019755L;

	private Long id;
	private String name;
	private ImportationSchemeFileFormat fileFormat;
	private ImportationSchemeImportType importType;
	private Integer firstRow;
	private String sheetName;
	private List<Variable> variables = new ArrayList<Variable>();
	private Date dateDeleted;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sch_id", unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "sch_name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "sch_file_format", nullable = false)
	@Enumerated(EnumType.STRING)
	public ImportationSchemeFileFormat getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(ImportationSchemeFileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	@Column(name = "sch_import_type", nullable = false)
	@Enumerated(EnumType.STRING)
	public ImportationSchemeImportType getImportType() {
		return importType;
	}

	public void setImportType(ImportationSchemeImportType importType) {
		this.importType = importType;
	}

	@Column(name = "sch_first_row")
	public Integer getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}

	@Column(name = "sch_sheet_name")
	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	@OneToMany(mappedBy = "importationScheme", cascade = CascadeType.ALL)
	@Filter(name = "hideDeleted", condition = "DateDeleted is null")
	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	@Column
	@Temporal(value = TemporalType.TIMESTAMP)
	public Date getDateDeleted() {
		return dateDeleted;
	}

	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

	@Override
	public void delete() {
		Date now = new Date();
		setDateDeleted(now);
	}

	@Override
	@Transient
	public boolean isDeleted() {
		return getDateDeleted() != null;
	}
}
