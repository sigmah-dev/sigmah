package org.sigmah.shared.command.result;

import java.io.Serializable;
import java.util.List;

/**
 * Contains application info.
 * 
 * @author tmi
 * 
 */
public class ApplicationInfo implements CommandResult {

    private static final long serialVersionUID = 2572076605822207434L;

    /**
     * A manager.
     * 
     * @author tmi
     * 
     */
    public static class ApplicationManager implements Serializable {

        private static final long serialVersionUID = -4982443373706399166L;

        private String name;
        private String url;

        public ApplicationManager() {
            // serialization
        }

        public ApplicationManager(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    /**
     * Partners roles.
     * 
     * @author tmi
     * 
     */
    public static enum ApplicationPartnerRole {
        DEVELOPPER, DESIGN, GRAPHISM;
    }

    /**
     * A partner.
     * 
     * @author tmi
     * 
     */
    public static class ApplicationPartner implements Serializable {

        private static final long serialVersionUID = -7267120614413805355L;

        private String name;
        private ApplicationPartnerRole role;
        private String url;

        public ApplicationPartner() {
            // serialization
        }

        public ApplicationPartner(String name, ApplicationPartnerRole role, String url) {
            this.name = name;
            this.role = role;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public ApplicationPartnerRole getRole() {
            return role;
        }

        public void setRole(ApplicationPartnerRole role) {
            this.role = role;
        }
    }

    /**
     * A developper.
     * 
     * @author tmi
     * 
     */
    public static class ApplicationDevelopper implements Serializable {

        private static final long serialVersionUID = -3612557266664530686L;

        private String name;
        private String email;

        public ApplicationDevelopper() {
            // serialization
        }

        public ApplicationDevelopper(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * A contributor.
     * 
     * @author tmi
     * 
     */
    public static class ApplicationContributor implements Serializable {

        private static final long serialVersionUID = -5224657670056593687L;

        private String name;
        private String email;

        public ApplicationContributor() {
            // serialization
        }

        public ApplicationContributor(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    private String versionName;
    private String versionNumber;
    private String versionReleaseDate;
    private List<ApplicationManager> managers;
    private List<ApplicationPartner> partners;
    private List<ApplicationDevelopper> developpers;
    private List<ApplicationContributor> contributors;

    public ApplicationInfo() {
        // serialization
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionReleaseDate() {
        return versionReleaseDate;
    }

    public void setVersionReleaseDate(String versionReleaseDate) {
        this.versionReleaseDate = versionReleaseDate;
    }

    public List<ApplicationManager> getManagers() {
        return managers;
    }

    public void setManagers(List<ApplicationManager> managers) {
        this.managers = managers;
    }

    public List<ApplicationPartner> getPartners() {
        return partners;
    }

    public void setPartners(List<ApplicationPartner> partners) {
        this.partners = partners;
    }

    public List<ApplicationDevelopper> getDeveloppers() {
        return developpers;
    }

    public void setDeveloppers(List<ApplicationDevelopper> developpers) {
        this.developpers = developpers;
    }

    public List<ApplicationContributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<ApplicationContributor> contributors) {
        this.contributors = contributors;
    }
}
