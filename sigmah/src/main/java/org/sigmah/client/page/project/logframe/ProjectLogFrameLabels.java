package org.sigmah.client.page.project.logframe;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.ExportUtils;

/**
 * Manages the list of all the labels used during the export of the logical
 * framework.
 * 
 * @author tmi
 * 
 */
public final class ProjectLogFrameLabels {

    private ProjectLogFrameLabels() {
    }

    // The labels keys.

    public static final String KEY_LOG_FRAME = "k_log_frame";
    public static final String KEY_TITLE = "k_title";
    public static final String KEY_MAIN_OBJECTIVE = "k_main_objective";
    public static final String KEY_INTERVENTION_LOGIC = "k_intervention_logic";
    public static final String KEY_INDICATORS = "k_indicators";
    public static final String KEY_RISKS = "k_risks";
    public static final String KEY_ASSUMPTIONS = "k_assumptions";
    public static final String KEY_GROUP = "k_group";
    public static final String KEY_SO_CODE = "k_so_code";
    public static final String KEY_SO_LABEL = "k_so_label";

    /**
     * Gets all the labels as a concatenated string.
     * 
     * @return The concatenated labels.
     */
    public static final String merge() {

        final StringBuilder labels = new StringBuilder();

        labels.append(KEY_LOG_FRAME);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrame());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_TITLE);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameActionTitle());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_MAIN_OBJECTIVE);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameMainObjective());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_INTERVENTION_LOGIC);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameInterventionLogic());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_INDICATORS);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameIndicators());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_RISKS);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameRisks());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_ASSUMPTIONS);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameAssumptions());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_GROUP);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameGroup());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_SO_CODE);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameSpecificObjectivesCode());

        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(KEY_SO_LABEL);
        labels.append(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
        labels.append(I18N.CONSTANTS.logFrameSpecificObjectives());

        return labels.toString();
    }

    /**
     * Splits a concatenated string of labels.
     * 
     * @param labels
     *            The concatenated string.
     * @return The labels as a map.
     */
    public static final Map<String, String> split(String labels) {

        final HashMap<String, String> map = new HashMap<String, String>();

        if (labels != null) {
            final String[] splitted = labels.split(ExportUtils.PARAM_EXPORT_LABELS_LIST_SEPARATOR);
            if (splitted != null) {
                for (int i = 0; i < splitted.length; i = i + 2) {
                    map.put(splitted[i], splitted[i + 1]);
                }
            }
        }

        return map;
    }
}
