/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah;

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
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.gwtrpc.handler.GetProjectReportHandler;
import org.sigmah.shared.domain.report.ProjectReport;
import org.sigmah.shared.domain.report.ProjectReportVersion;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;
import org.sigmah.shared.dto.report.RichTextElementDTO;

/**
 * Export project reports as RTF files.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectReportExporter extends Exporter {
    
    private final static Log LOG = LogFactory.getLog(ProjectReportExporter.class);

    private ProjectReport report;
    
    
    public ProjectReportExporter(EntityManager em, Map<String, Object> parametersMap) {
        super(em, parametersMap);
    }

    @Override
    public String getFileName() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            loadReport();
        } catch (ExportException e) {
            LOG.debug("The project report cannot be found.", e);
        }

        final String name;
        if(report != null)
            name = report.getName();
        else
            name = "report";
        
        return name + '_' + format.format(new Date())+".rtf";
    }

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.MSWORD;
    }

    /**
     * Loads the project report from the database.
     * @throws ExportException If the ID parameter is missing or
     * if the report cannot be found.
     */
    private void loadReport() throws ExportException {

        if(report == null) {
            
            final String idAsString = requireParameter(ExportUtils.PARAM_EXPORT_PROJECT_ID);
            final Integer id;
            try {
                id = Integer.parseInt(idAsString);
            } catch (NumberFormatException e) {
                LOG.error("[export] The id '" + idAsString + "' is invalid.", e);
                throw new ExportException("The id '" + idAsString + "' is invalid.", e);
            }

            report = em.find(ProjectReport.class, id);
        }
    }

    @Override
    public void export(OutputStream output) throws ExportException {
        
        loadReport();

        // Label displayed instead of the Table of Contents during the export.
        final String tocLabel = requireParameter(ExportUtils.PARAM_EXPORT_LABELS_LIST);

        if(report != null) {
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
                
            } catch(IOException e) {
                LOG.debug("An error occured while converting HTML to RTF.");
            }
            
        } else {
            final String idAsString = requireParameter(ExportUtils.PARAM_EXPORT_PROJECT_ID);
            
            LOG.error("[export] No project report is identified by '" + idAsString + "'.");
            throw new ExportException("[export] No project report is identified by '" + idAsString + "'.");
        }
    }

    /**
     * Adds the given section to the RTF document.
     * @param section Section to add.
     * @param prefix Current index (for example: 3.1.1).
     * @param index Local index.
     * @param parent Parent element.
     * @throws DocumentException
     */
    private void addSection(ProjectReportSectionDTO section, StringBuilder prefix,
            int index, Object parent) throws DocumentException, IOException {
        
        // Adding the title to the document
        final TextElementArray thisSection;
        if(parent instanceof Document) {
            // Style
            final Paragraph paragraph = new Paragraph(section.getName());
            paragraph.getFont().setSize(16);
            paragraph.getFont().setStyle(Font.BOLD);

            // New chapter
            final Chapter chapter = new Chapter(paragraph, index);
            thisSection = chapter;
            
        } else if(parent instanceof Chapter) {
            // Style
            final Paragraph paragraph = new Paragraph(section.getName());
            paragraph.getFont().setSize(14);
            paragraph.getFont().setStyle(Font.BOLD);

            // New section
            final Section chapterSection = ((Chapter)parent).addSection(paragraph);
            thisSection = chapterSection;
            
        } else if(parent instanceof TextElementArray) {
            // Style
            final Paragraph paragraph = new Paragraph(prefix.toString() + ' ' + section.getName());
            paragraph.getFont().setSize(12);
            paragraph.getFont().setStyle(Font.BOLD);

            // New paragraph
            ((TextElementArray)parent).add(paragraph);
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
                    final List<Element> elements = HTMLWorker.parseToList(new StringReader(value), stylesheet);

                    for (final Element element : elements)
                        thisSection.add(element);
                }
            }
        }

        // Adding the chapter to the document
        if(thisSection instanceof Chapter && parent instanceof Document)
            ((Document)parent).add((Chapter)thisSection);
    }
    
}
