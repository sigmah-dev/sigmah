package org.sigmah.server.dao;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.domain.report.RichTextElement;
import org.sigmah.server.domain.value.Value;

/**
 * Project report DAO interface.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ProjectReportDAO extends DAO<ProjectReport, Integer> {

	void persist(ProjectReportVersion version);

	void merge(ProjectReport report);

	void merge(ProjectReportVersion version);

	void merge(RichTextElement element);

	void merge(Value value);

	ProjectReport findReportById(Integer id);

	ProjectReportVersion findReportVersionById(Integer id);

	ProjectReportModel findModelById(Integer id);

	RichTextElement findRichTextElementById(Integer id);

}
