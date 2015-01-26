package org.sigmah.server.servlet.exporter.template;

import org.odftoolkit.simple.SpreadsheetDocument;
import org.sigmah.server.domain.Project;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.ProjectSynthesisData;
import org.sigmah.shared.Language;

/**
 * @author sherzod (v1.3)
 */
public class ProjectSynthesisCalcTemplate extends BaseSynthesisCalcTemplate {

	public ProjectSynthesisCalcTemplate(final ProjectSynthesisData data, final SpreadsheetDocument doc, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language) throws Throwable {
		super(data, doc, Project.class, context, i18nTranslator, language);
	}

}
