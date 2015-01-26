/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.domain.element;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.shared.domain.report.ProjectReportModel;

/**
 * Report element entity.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Entity
@Table(name = "report_element")
public class ReportElement extends FlexibleElement {

    private static final long serialVersionUID = 1L;

    /**
     * Link to the ProjectReportModel that will be used by the report contained
     * by this element.
     */

    private ProjectReportModel model;

    @ManyToOne(optional = true)
    @JoinColumn(name = "model_id", nullable = true)
    public ProjectReportModel getModel() {
        return model;
    }

    public void setModel(ProjectReportModel model) {
        this.model = model;
    }

}
