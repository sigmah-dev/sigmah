/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command.result;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectDTOLight;

/**
 * List of projects visible to the user.
 */
public class ProjectListResult implements CommandResult {

    private static final long serialVersionUID = 749007653017696900L;

    /**
     * A comparator which sort the projects with their codes.
     */
    public static final Comparator<ProjectDTOLight> CODE_COMPARATOR = new Comparator<ProjectDTOLight>() {

        @Override
        public int compare(ProjectDTOLight p1, ProjectDTOLight p2) {

            if (p1 == null) {
                if (p2 == null) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (p2 == null) {
                return 1;
            }

            return p1.getName() != null ? p1.getName().compareToIgnoreCase(p2.getName()) : -1;
        }
    };

    /**
     * List of 'light' mapped projects.
     */
    private List<ProjectDTOLight> listProjectsLightDTO;

    /**
     * List of mapped projects.
     */
    private List<ProjectDTO> listProjectsDTO;

    /**
     * List of projects ids.
     */
    private List<Integer> listProjectsIds;

    public ProjectListResult() {
        // serialization.
    }

    /**
     * Gets the list of 'light' mapped projects. This list is set only if the
     * command return type was set to
     * {@link GetProjects.ProjectResultType#PROJECT_LIGHT}. This list is never
     * <code>null</code>.
     * 
     * @return The list of 'light' mapped projects.
     */
    @SuppressWarnings("unchecked")
    public List<ProjectDTOLight> getListProjectsLightDTO() {
        return listProjectsLightDTO != null ? listProjectsLightDTO : (List<ProjectDTOLight>) Collections.EMPTY_LIST;
    }

    public void setListProjectsLightDTO(List<ProjectDTOLight> listProjectsLightDTO) {
        this.listProjectsLightDTO = listProjectsLightDTO;
    }

    /**
     * Gets the list of mapped projects. This list is set only if the command
     * return type was set to {@link GetProjects.ProjectResultType#PROJECT}.
     * This list is never <code>null</code>.
     * 
     * @return The list of mapped projects.
     */
    @SuppressWarnings("unchecked")
    public List<ProjectDTO> getListProjectsDTO() {
        return listProjectsDTO != null ? listProjectsDTO : (List<ProjectDTO>) Collections.EMPTY_LIST;
    }

    public void setListProjectsDTO(List<ProjectDTO> listProjectsDTO) {
        this.listProjectsDTO = listProjectsDTO;
    }

    /**
     * Gets the list of projects ids. This list is set only if the command
     * return type was set to {@link GetProjects.ProjectResultType#ID}. This
     * list is never <code>null</code>.
     * 
     * @return The list of projects ids.
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getListProjectsIds() {
        return listProjectsIds != null ? listProjectsIds : (List<Integer>) Collections.EMPTY_LIST;
    }

    public void setListProjectsIds(List<Integer> listProjectsIds) {
        this.listProjectsIds = listProjectsIds;
    }

    /**
     * Gets the projects list ordered with the given comparator.
     * 
     * @param comparator
     *            The comparator.
     * @return The ordered list of projects (never <code>null</code>).
     */
    public List<ProjectDTOLight> getOrderedList(Comparator<ProjectDTOLight> comparator) {
        final List<ProjectDTOLight> list = getListProjectsLightDTO();
        Collections.sort(list, comparator);
        return list;
    }
}
