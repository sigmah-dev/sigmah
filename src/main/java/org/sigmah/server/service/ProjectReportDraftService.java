package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.server.dao.ProjectReportDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.domain.report.RichTextElement;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.report.ProjectReportDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handle the creation procedure of the project <b>draft</b> version of a project report.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectReportDraftService extends AbstractEntityService<ProjectReportVersion, Integer, ProjectReportDTO> {

	private final ProjectReportDAO projectReportDAO;

	@Inject
	public ProjectReportDraftService(final ProjectReportDAO projectReportDAO) {
		this.projectReportDAO = projectReportDAO;
	}

	/**
	 * <p>
	 * Creates a new <b>draft</b> report version.
	 * </p>
	 * <p>
	 * Requires the following properties :
	 * <ul>
	 * <li><code>phaseName</code> - Name of the current phase.</li>
	 * <li><code>reportId</code> - ID of the project report.</li>
	 * </ul>
	 * </p>
	 */
	@Override
	public ProjectReportVersion create(final PropertyMap properties, final UserExecutionContext context) {

		final ProjectReportVersion version = new ProjectReportVersion();
		version.setEditor(context.getUser());
		version.setEditDate(new Date());
		version.setPhaseName((String) properties.get(ProjectReportDTO.PHASE_NAME));

		// Linking the draft to the report
		final ProjectReport report = projectReportDAO.findReportById((Integer) properties.get(ProjectReportDTO.REPORT_ID));
		version.setReport(report);

		// Copying the current values
		final ArrayList<RichTextElement> texts = new ArrayList<RichTextElement>();

		final List<RichTextElement> currentTexts = report.getCurrentVersion().getTexts();
		for (final RichTextElement text : currentTexts) {
			final RichTextElement element = text.duplicate();
			element.setVersion(version);

			texts.add(element);
		}

		version.setTexts(texts);

		// Saving
		projectReportDAO.persist(version);

		return version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportVersion update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

}
