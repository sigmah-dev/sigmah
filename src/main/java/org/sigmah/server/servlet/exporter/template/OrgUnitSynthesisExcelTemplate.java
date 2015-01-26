package org.sigmah.server.servlet.exporter.template;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.data.OrgUnitSynthesisData;
import org.sigmah.shared.Language;

/**
 * @author sherzod (v1.3)
 */
public class OrgUnitSynthesisExcelTemplate extends BaseSynthesisExcelTemplate {

	public OrgUnitSynthesisExcelTemplate(final OrgUnitSynthesisData data, final HSSFWorkbook wb, final ServletExecutionContext context, final I18nServer i18nTranslator, final Language language) throws Throwable {
		super(data, wb, OrgUnit.class, context, i18nTranslator, language);
	}

}
