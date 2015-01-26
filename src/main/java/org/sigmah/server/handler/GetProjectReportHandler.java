package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.KeyQuestion;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportModelSection;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.domain.report.RichTextElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.KeyQuestionDTO;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;
import org.sigmah.shared.dto.report.RichTextElementDTO;

/**
 * Handler for {@link GetProjectReport} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectReportHandler extends AbstractCommandHandler<GetProjectReport, ProjectReportDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportDTO execute(final GetProjectReport cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<ProjectReport> query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.id = :reportId", ProjectReport.class);
		query.setParameter("reportId", cmd.getReportId());

		try {

			final ProjectReport report = query.getSingleResult();
			ProjectReportVersion version = report.getCurrentVersion();

			// Looking for a draft
			final TypedQuery<ProjectReportVersion> queryVersion =
					em().createQuery("SELECT v FROM ProjectReportVersion v WHERE v.report.id = :reportId AND v.editor = :user AND v.version IS NULL",
						ProjectReportVersion.class);
			queryVersion.setParameter("reportId", cmd.getReportId());
			queryVersion.setParameter("user", context.getUser());

			try {

				version = queryVersion.getSingleResult();

			} catch (final NoResultException e) {
				// No draft for the current user.
			}

			return toDTO(report, version);

		} catch (NoResultException e) {
			// Bad report id.
		}

		return null;
	}

	/**
	 * Convert the given report into a ProjectReportDTO.
	 * 
	 * @param report
	 *          A project report.
	 * @param version
	 *          A version of this report.
	 * @return A ProjectReportDTO.
	 */
	public static ProjectReportDTO toDTO(final ProjectReport report, final ProjectReportVersion version) {

		final ProjectReportDTO reportDTO = new ProjectReportDTO();

		reportDTO.setId(report.getId());
		reportDTO.setVersionId(version.getId());
		reportDTO.setName(report.getName());
		reportDTO.setPhaseName(version.getPhaseName());
		reportDTO.setDraft(version.getVersion() == null);
		reportDTO.setLastEditDate(version.getEditDate());
		reportDTO.setEditorName(version.getEditorShortName());

		if (report.getProject() != null) {
			reportDTO.setProjectId(report.getProject().getId());
		}
		if (report.getOrgUnit() != null) {
			reportDTO.setOrgUnitId(report.getOrgUnit().getId());
		}

		final ProjectReportModel model = report.getModel();

		final List<ProjectReportModelSection> sectionModels = model.getSections();
		final Map<Integer, List<RichTextElement>> richTextElements = organizeElementsBySection(version.getTexts());
		final List<ProjectReportSectionDTO> sectionDTOs = new ArrayList<ProjectReportSectionDTO>();

		for (ProjectReportModelSection sectionModel : sectionModels) {
			sectionDTOs.add(iterateOnSection(sectionModel, richTextElements));
		}

		reportDTO.setSections(sectionDTOs);

		return reportDTO;
	}

	/**
	 * Order the rich text elements by section.
	 * 
	 * @param elements
	 *          Rich text elements.
	 * @return A map containing lists of rich text elements.
	 */
	private static Map<Integer, List<RichTextElement>> organizeElementsBySection(final List<RichTextElement> elements) {

		final Map<Integer, List<RichTextElement>> map = new HashMap<Integer, List<RichTextElement>>();

		for (final RichTextElement element : elements) {
			List<RichTextElement> list = map.get(element.getSectionId());

			if (list == null) {
				list = new ArrayList<RichTextElement>();
				map.put(element.getSectionId(), list);
			}

			list.add(element);
		}

		return map;
	}

	private static ProjectReportSectionDTO iterateOnSection(final ProjectReportModelSection sectionModel,
			final Map<Integer, List<RichTextElement>> richTextElements) {

		final ProjectReportSectionDTO sectionDTO = new ProjectReportSectionDTO();
		sectionDTO.setId(sectionModel.getId());
		sectionDTO.setName(sectionModel.getName());

		List<RichTextElement> elementList = richTextElements.get(sectionModel.getId());
		if (elementList == null)
			elementList = Collections.emptyList();

		final Iterator<RichTextElement> elementIterator = elementList.iterator();
		final Iterator<KeyQuestion> keyQuestionIterator = sectionModel.getKeyQuestions().iterator();
		final Iterator<ProjectReportModelSection> subSectionIterator = sectionModel.getSubSections().iterator();

		// Children of this section
		final ArrayList<ProjectReportContent> children = new ArrayList<ProjectReportContent>();

		// Next rich text element
		RichTextElement nextElement;
		if (elementIterator.hasNext()) {
			nextElement = elementIterator.next();
		} else {
			nextElement = null;
		}

		// Key questions
		int keys = 0;
		while (keyQuestionIterator.hasNext()) {
			final KeyQuestion keyQuestion = keyQuestionIterator.next();
			final KeyQuestionDTO keyQuestionDTO = new KeyQuestionDTO();

			keyQuestionDTO.setId(keyQuestion.getId());
			keyQuestionDTO.setLabel(keyQuestion.getLabel());

			final RichTextElementDTO elementDTO = new RichTextElementDTO();
			elementDTO.setId(nextElement.getId());
			elementDTO.setText(nextElement.getText());

			keyQuestionDTO.setRichTextElementDTO(elementDTO);

			if (elementIterator.hasNext()) {
				nextElement = elementIterator.next();
			} else {
				nextElement = null;
			}

			children.add(keyQuestionDTO);
			keys++;
		}

		// Sub sections
		while (subSectionIterator.hasNext()) {
			final ProjectReportModelSection subSectionModel = subSectionIterator.next();

			while (nextElement != null && nextElement.getIndex() - keys < subSectionModel.getIndex()) {
				final RichTextElementDTO elementDTO = new RichTextElementDTO();
				elementDTO.setId(nextElement.getId());
				elementDTO.setText(nextElement.getText());
				children.add(elementDTO);

				if (elementIterator.hasNext()) {
					nextElement = elementIterator.next();
				} else {
					nextElement = null;
				}
			}

			final ProjectReportSectionDTO subSectionDTO = iterateOnSection(subSectionModel, richTextElements);
			children.add(subSectionDTO);
		}

		// Remaining elements
		while (nextElement != null) {
			final RichTextElementDTO elementDTO = new RichTextElementDTO();
			elementDTO.setId(nextElement.getId());
			elementDTO.setText(nextElement.getText());
			children.add(elementDTO);

			if (elementIterator.hasNext()) {
				nextElement = elementIterator.next();
			} else {
				nextElement = null;
			}
		}

		sectionDTO.setChildren(children);

		return sectionDTO;
	}
}
