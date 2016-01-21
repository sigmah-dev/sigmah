package org.sigmah.server.servlet.exporter;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.handler.GetProjectReportHandler;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.base.Exporter;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;
import org.sigmah.shared.dto.report.RichTextElementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.field.RtfTableOfContents;

/**
 * Export project reports as RTF files.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 */
public class ProjectReportExporter extends Exporter {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(ProjectReportExporter.class);

	private ProjectReport report;

	public ProjectReportExporter(final Injector injector, final HttpServletRequest req, ServletExecutionContext context) throws Exception {
		super(injector, req, context);
	}

	@Override
	public String getFileName() {
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			loadReport();
		} catch (Exception e) {
			LOG.debug("The project report cannot be found.", e);
		}

		final String name;
		if (report != null)
			name = report.getName();
		else
			name = "report";

		return name + '_' + format.format(new Date()) + getExtention();
	}

	/**
	 * Loads the project report from the database.
	 * 
	 * @throws Exception
	 *           If the ID parameter is missing or if the report cannot be found.
	 */
	private void loadReport() throws Exception {

		if (report == null) {

			final String idAsString = requireParameter(RequestParameter.ID);
			final Integer id;
			try {
				id = Integer.parseInt(idAsString);
			} catch (NumberFormatException e) {
				LOG.error("[export] The id '" + idAsString + "' is invalid.", e);
				throw new Exception("The id '" + idAsString + "' is invalid.", e);
			}

			report = injector.getInstance(EntityManager.class).find(ProjectReport.class, id);
		}
	}

	@Override
	public void export(OutputStream output) throws Exception {

		loadReport();

		// Label displayed instead of the Table of Contents during the export.
		final String tocLabel = localize("projectReportTableOfContents");

		if (report != null) {
			final ProjectReportVersion version = report.getCurrentVersion();

			final ProjectReportDTO reportDTO = GetProjectReportHandler.toDTO(report, version);

			// Generating the RTF
			try {
				final Document document = new Document();
				final RtfWriter2 writer = RtfWriter2.getInstance(document, output);

				writer.setAutogenerateTOCEntries(true);

				document.open();

				// Title
				final Paragraph titleParagraph = new Paragraph(report.getName());
				titleParagraph.getFont().setSize(24);
				titleParagraph.getFont().setStyle(Font.BOLD);
				document.add(titleParagraph);

				document.add(new Paragraph()); // Empty paragraph

				// Table of contents
				final Paragraph tocParagraph = new Paragraph();
				final RtfTableOfContents toc = new RtfTableOfContents(tocLabel);
				tocParagraph.add(toc);
				document.add(tocParagraph);

				// Sections
				final List<ProjectReportSectionDTO> sections = reportDTO.getSections();
				final StringBuilder prefix = new StringBuilder();

				for (int index = 0; index < sections.size(); index++) {
					final ProjectReportSectionDTO section = sections.get(index);

					prefix.append(index + 1).append('.');
					addSection(section, prefix, index + 1, document);

					prefix.setLength(0);
				}

				document.close();

			} catch (DocumentException ex) {
				LOG.error("An error occured while generating the RTF.", ex);

			} catch (IOException e) {
				LOG.debug("An error occured while converting HTML to RTF.");
			}

		} else {
			final String idAsString = requireParameter(RequestParameter.ID);

			LOG.error("[export] No project report is identified by '" + idAsString + "'.");
			throw new Exception("[export] No project report is identified by '" + idAsString + "'.");
		}
	}

	/**
	 * Adds the given section to the RTF document.
	 * 
	 * @param section
	 *          Section to add.
	 * @param prefix
	 *          Current index (for example: 3.1.1).
	 * @param index
	 *          Local index.
	 * @param parent
	 *          Parent element.
	 * @throws DocumentException
	 */
	private void addSection(ProjectReportSectionDTO section, StringBuilder prefix, int index, Object parent) throws DocumentException, IOException {

		// Adding the title to the document
		final TextElementArray thisSection;
		if (parent instanceof Document) {
			// Style
			final Paragraph paragraph = new Paragraph(section.getName());
			paragraph.getFont().setSize(16);
			paragraph.getFont().setStyle(Font.BOLD);

			// New chapter
			final Chapter chapter = new Chapter(paragraph, index);
			thisSection = chapter;

		} else if (parent instanceof Chapter) {
			// Style
			final Paragraph paragraph = new Paragraph(section.getName());
			paragraph.getFont().setSize(14);
			paragraph.getFont().setStyle(Font.BOLD);

			// New section
			final Section chapterSection = ((Chapter) parent).addSection(paragraph);
			thisSection = chapterSection;

		} else if (parent instanceof TextElementArray) {
			// Style
			final Paragraph paragraph = new Paragraph(prefix.toString() + ' ' + section.getName());
			paragraph.getFont().setSize(12);
			paragraph.getFont().setStyle(Font.BOLD);

			// New paragraph
			((TextElementArray) parent).add(paragraph);
			thisSection = (TextElementArray) parent;

		} else
			thisSection = null;

		// Adding the content of this section
		int subIndex = 1;
		final int prefixLength = prefix.length();

		final StyleSheet stylesheet = new StyleSheet();
		stylesheet.loadTagStyle(HtmlTags.PARAGRAPH, "margin", "0");
		stylesheet.loadTagStyle(HtmlTags.PARAGRAPH, "padding", "0");
		stylesheet.loadTagStyle(HtmlTags.DIV, "margin", "0");
		stylesheet.loadTagStyle(HtmlTags.DIV, "padding", "0");

		for (final ProjectReportContent child : section.getChildren()) {

			if (child instanceof ProjectReportSectionDTO) {
				prefix.append(index).append('.');

				addSection((ProjectReportSectionDTO) child, prefix, subIndex, thisSection);
				subIndex++;

				prefix.setLength(prefixLength);

			} else if (child instanceof RichTextElementDTO) {

				final String value = ((RichTextElementDTO) child).getText();
				if (value != null && !"".equals(value)) {

					// HTML parsing.
					@SuppressWarnings("unchecked")
					final List<Element> elements = HTMLWorker.parseToList(new StringReader(value), stylesheet);

					for (final Element element : elements)
						thisSection.add(element);
				}
			}
		}

		// Adding the chapter to the document
		if (thisSection instanceof Chapter && parent instanceof Document)
			((Document) parent).add(thisSection);
	}

}
