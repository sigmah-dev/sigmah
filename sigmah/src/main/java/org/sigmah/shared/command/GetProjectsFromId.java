/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import java.util.List;

import org.sigmah.shared.command.result.ProjectListResult;

/**
 * Retrieves the list of projects with their ids.
 */
public class GetProjectsFromId implements Command<ProjectListResult> {

    private static final long serialVersionUID = -5110073809257246784L;

    /**
     * List of the projects ids.
     */
    private List<Integer> ids;

    public GetProjectsFromId() {
        this(null);
    }

    public GetProjectsFromId(List<Integer> ids) {
        this.ids = ids;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ids == null) ? 0 : ids.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetProjectsFromId other = (GetProjectsFromId) obj;
        if (ids == null) {
            if (other.ids != null)
                return false;
        } else if (!ids.equals(other.ids))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[GetProjectsFromId] command: ");
        sb.append("ids [");
        sb.append(ids);
        sb.append("]");
        return sb.toString();
    }
}
