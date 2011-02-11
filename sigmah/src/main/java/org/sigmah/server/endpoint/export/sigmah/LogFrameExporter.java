package org.sigmah.server.endpoint.export.sigmah;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.client.page.project.logframe.ProjectLogFrameLabels;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.logframe.ExpectedResult;
import org.sigmah.shared.domain.logframe.LogFrame;
import org.sigmah.shared.domain.logframe.LogFrameActivity;
import org.sigmah.shared.domain.logframe.LogFrameGroup;
import org.sigmah.shared.domain.logframe.LogFrameModel;
import org.sigmah.shared.domain.logframe.Prerequisite;
import org.sigmah.shared.domain.logframe.SpecificObjective;
import org.sigmah.shared.dto.ExportUtils;

/**
 * Exports logical frameworks.
 * 
 * @author tmi
 */
public class LogFrameExporter extends Exporter {

    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(LogFrameExporter.class);

    public LogFrameExporter(EntityManager em, Map<String, Object> parametersMap) {
        super(em, parametersMap);
    }

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.MSEXCEL;
    }

    @Override
    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return "Export_" + format.format(new Date()) + ".xls";
    }

    @Override
    public void export(OutputStream output) throws ExportException {

        // The project id.
        final String idString = requireParameter(ExportUtils.PARAM_EXPORT_PROJECT_ID);
        final Integer id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            log.error("[export] The id '" + idString + "' is invalid.", e);
            throw new ExportException("The id '" + idString + "' is invalid.", e);
        }

        // Labels.
        final String labelsString = requireParameter(ExportUtils.PARAM_EXPORT_LABELS_LIST);
        final Map<String, String> labels = ProjectLogFrameLabels.split(labelsString);

        // Retrieves the project log frame.
        final Project project = em.find(Project.class, id);

        if (project == null) {
            log.error("[export] The project #" + id + " doesn't exist.");
            throw new ExportException("The project #" + id + " doesn't exist.");
        }

        final LogFrame logFrame = project.getLogFrame();

        try {

            // Creates workbook.
            final WritableWorkbook workbook = Workbook.createWorkbook(output);
            final WritableSheet sheet = workbook.createSheet(labels.get(ProjectLogFrameLabels.KEY_LOG_FRAME), 0);

            if (logFrame != null) {
                writeLogFrame(sheet, logFrame, labels);
            }

            // Writes workbook.
            workbook.write();
            workbook.close();

        } catch (IOException e) {
            log.error("[export] Error during the workbook writing.", e);
            throw new ExportException("Error during the workbook writing.");
        } catch (WriteException e) {
            log.error("[export] Error during the workbook writing.", e);
            throw new ExportException("Error during the workbook writing.");
        }
    }

    /**
     * Write the given log frame into the given sheet.
     * 
     * @param sheet
     *            The workbook sheet.
     * @param logFrame
     *            The log frame.
     * @param labels
     *            The labels.
     * @throws WriteException
     *             If an error occurs.
     */
    private void writeLogFrame(WritableSheet sheet, LogFrame logFrame, Map<String, String> labels)
            throws WriteException {

        final LogFrameModel logFrameModel = logFrame.getLogFrameModel();

        // -----------
        // Formats.
        // -----------

        // Fonts.
        final WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, false,
                UnderlineStyle.NO_UNDERLINE, Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);
        final WritableFont normalFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD, false,
                UnderlineStyle.NO_UNDERLINE, Colour.BLACK, ScriptStyle.NORMAL_SCRIPT);

        // Formats.
        final WritableCellFormat headerFormat = new WritableCellFormat(boldFont);
        headerFormat.setWrap(false);
        headerFormat.setBackground(Colour.TAN);
        headerFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.BLACK);

        final WritableCellFormat headerFormat2 = new WritableCellFormat(boldFont);
        headerFormat2.setWrap(false);
        headerFormat2.setBackground(Colour.TAN);
        headerFormat2.setBorder(Border.RIGHT, BorderLineStyle.THIN, Colour.BLACK);

        final WritableCellFormat groupFormat = new WritableCellFormat(boldFont);
        groupFormat.setWrap(false);
        groupFormat.setBackground(Colour.SKY_BLUE);

        final WritableCellFormat contentFormat = new WritableCellFormat(normalFont);
        contentFormat.setWrap(false);

        Label label = null;

        // -----------
        // Headers.
        // -----------

        // Title
        label = new Label(0, 0, labels.get(ProjectLogFrameLabels.KEY_TITLE));
        label.setCellFormat(new WritableCellFormat(boldFont));
        sheet.addCell(label);

        // Objective
        label = new Label(0, 1, labels.get(ProjectLogFrameLabels.KEY_MAIN_OBJECTIVE));
        label.setCellFormat(new WritableCellFormat(boldFont));
        sheet.addCell(label);

        sheet.addCell(new Blank(0, 2));
        sheet.addCell(new Blank(1, 2));
        sheet.addCell(new Blank(2, 2));

        // Logic
        label = new Label(3, 2, labels.get(ProjectLogFrameLabels.KEY_INTERVENTION_LOGIC));
        sheet.addCell(label);

        // Indicator
        label = new Label(4, 2, labels.get(ProjectLogFrameLabels.KEY_INDICATORS));
        sheet.addCell(label);

        // Risks
        label = new Label(5, 2, labels.get(ProjectLogFrameLabels.KEY_RISKS));
        sheet.addCell(label);

        // Assumptions
        label = new Label(6, 2, labels.get(ProjectLogFrameLabels.KEY_ASSUMPTIONS));
        sheet.addCell(label);

        sheet.mergeCells(1, 0, 6, 0);
        sheet.mergeCells(1, 1, 6, 1);
        sheet.mergeCells(1, 2, 2, 2);

        for (int i = 0; i <= 6; i++) {
            sheet.getWritableCell(i, 2).setCellFormat(headerFormat);
        }

        // -----------
        // Title and objective.
        // -----------

        label = new Label(1, 0, logFrame.getTitle());
        label.setCellFormat(contentFormat);
        sheet.addCell(label);

        label = new Label(1, 1, logFrame.getMainObjective());
        label.setCellFormat(contentFormat);
        sheet.addCell(label);

        // -----------
        // Groups.
        // -----------

        final ArrayList<LogFrameGroup> soGroups = new ArrayList<LogFrameGroup>();
        final HashMap<LogFrameGroup, ArrayList<SpecificObjective>> soMap = new HashMap<LogFrameGroup, ArrayList<SpecificObjective>>();
        final ArrayList<LogFrameGroup> erGroups = new ArrayList<LogFrameGroup>();
        final HashMap<LogFrameGroup, ArrayList<ExpectedResult>> erMap = new HashMap<LogFrameGroup, ArrayList<ExpectedResult>>();
        final ArrayList<LogFrameGroup> prGroups = new ArrayList<LogFrameGroup>();
        final HashMap<LogFrameGroup, ArrayList<Prerequisite>> prMap = new HashMap<LogFrameGroup, ArrayList<Prerequisite>>();
        final ArrayList<LogFrameGroup> acGroups = new ArrayList<LogFrameGroup>();
        final HashMap<LogFrameGroup, ArrayList<LogFrameActivity>> acMap = new HashMap<LogFrameGroup, ArrayList<LogFrameActivity>>();

        // Displays all the groups (even the empty ones).
        for (final LogFrameGroup group : logFrame.getGroups()) {
            switch (group.getType()) {
            case SPECIFIC_OBJECTIVE:
                soGroups.add(group);
                soMap.put(group, new ArrayList<SpecificObjective>());
                break;
            case EXPECTED_RESULT:
                erGroups.add(group);
                erMap.put(group, new ArrayList<ExpectedResult>());
                break;
            case PREREQUISITE:
                prGroups.add(group);
                prMap.put(group, new ArrayList<Prerequisite>());
                break;
            case ACTIVITY:
                acGroups.add(group);
                acMap.put(group, new ArrayList<LogFrameActivity>());
                break;
            }
        }

        // -----------
        // Specific objectives.
        // -----------

        // Sort by group.
        for (final SpecificObjective so : logFrame.getSpecificObjectives()) {
            final LogFrameGroup soGroup = so.getGroup();
            final ArrayList<SpecificObjective> list = soMap.get(soGroup);
            if (list != null) {
                list.add(so);
            } else {
                // TODO
            }
        }

        int start = 3;
        int row = start;

        // For each group.
        for (final LogFrameGroup group : soGroups) {

            // Displays the group.
            if (logFrameModel.getEnableSpecificObjectivesGroups()) {

                label = new Label(1, row, labels.get(ProjectLogFrameLabels.KEY_GROUP) + " ("
                        + labels.get(ProjectLogFrameLabels.KEY_SO_CODE) + ") - " + group.getLabel());
                label.setCellFormat(groupFormat);
                sheet.addCell(label);

                sheet.mergeCells(1, row, 6, row);

                row++;
            }

            // Sort the items.
            final ArrayList<SpecificObjective> list = soMap.get(group);
            Collections.sort(list, new Comparator<SpecificObjective>() {
                @Override
                public int compare(SpecificObjective o1, SpecificObjective o2) {
                    return o1.getPosition().compareTo(o2.getPosition());
                }
            });

            // Displays items.
            for (final SpecificObjective so : list) {

                label = new Label(1, row, "" + so.getCode());
                label.setCellFormat(contentFormat);
                sheet.addCell(label);

                sheet.mergeCells(1, row, 2, row);

                label = new Label(3, row, so.getInterventionLogic());
                label.setCellFormat(contentFormat);
                sheet.addCell(label);

                label = new Label(5, row, so.getRisks());
                label.setCellFormat(contentFormat);
                sheet.addCell(label);

                label = new Label(6, row, so.getAssumptions());
                label.setCellFormat(contentFormat);
                sheet.addCell(label);

                row++;
            }
        }

        sheet.mergeCells(0, start, 0, row - 1);

        label = new Label(0, start, labels.get(ProjectLogFrameLabels.KEY_SO_LABEL) + " ("
                + labels.get(ProjectLogFrameLabels.KEY_SO_CODE) + ")");
        label.setCellFormat(headerFormat2);
        sheet.addCell(label);
    }
}
