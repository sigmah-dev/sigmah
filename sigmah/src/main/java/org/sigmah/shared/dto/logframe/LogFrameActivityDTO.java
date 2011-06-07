package org.sigmah.shared.dto.logframe;

import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.project.logframe.grid.Row.Positionable;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity logframe.Activity.
 * 
 * @author tmi
 * 
 */
public class LogFrameActivityDTO extends LogFrameElementDTO  {

    private static final long serialVersionUID = 6134012388369233491L;

    @Override
    public String getEntityName() {
        return "logframe.Activity";
    }
    
    //Activity advancement 
    public Integer getAdvancement() {
        return get("advancement");
    }

    public void setAdvancement(Integer advancement) {
        set("advancement", advancement);
    }

    // Activity title.
    public String getTitle() {
        return get("title");
    }

    public void setTitle(String title) {
        set("title", title);
    }

    // Activity start date.
    public Date getStartDate() {
        return get("startDate");
    }

    public void setStartDate(Date startDate) {
        set("startDate", startDate);
    }

    // Activity end date.
    public Date getEndDate() {
        return get("endDate");
    }

    public void setEndDate(Date endDate) {
        set("endDate", endDate);
    }

    // Activity parent result.
    public ExpectedResultDTO getParentExpectedResult() {
        return get("parentExpectedResult");
    }

    public void setParentExpectedResult(ExpectedResultDTO parentExpectedResultDTO) {
        set("parentExpectedResult", parentExpectedResultDTO);
    }

    // Display label.
    /**
     * Sets the attribute <code>label</code> to display this element in a
     * selection window.
     */
    public void setLabel(String label) {
        set("label", label);
    }

    @Override
    public String getLabel() {
        return get("label");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LogFrameActivityDTO [");
        sb.append("entity name = ");
        sb.append(getEntityName());
        sb.append(" ; id = ");
        sb.append(getId());
        sb.append(" ; group id = ");
        if (getGroup() != null) {
            sb.append(getGroup().getId() != -1 ? getGroup().getId() : getGroup()
                    .getClientSideId());
        }
        sb.append(" ; dlabel = ");
        sb.append(getLabel());
        sb.append(" ; code = ");
        sb.append(getCode());
//        sb.append(" ; content = ");
//        sb.append(getContent());
        sb.append("]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return getClientSideId();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof LogFrameActivityDTO)) {
            return false;
        }

        final LogFrameActivityDTO other = (LogFrameActivityDTO) obj;
        return getClientSideId() == other.getClientSideId();
    }

	@Override
	public String getFormattedCode() {

	    final StringBuilder sb = new StringBuilder();

	    final ExpectedResultDTO parent;
	    if ((parent = getParentExpectedResult()) != null) {
	        sb.append(getParentExpectedResult().getFormattedCode());
	    }

	    sb.append(getCode());
	    sb.append(".");

	    return sb.toString();
	}

	@Override
	public String getDescription() {
		return getTitle();
	}
}
