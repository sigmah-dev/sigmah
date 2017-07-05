package org.sigmah.server.search;

import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.sigmah.server.dao.ContactDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Contact;

import com.allen_sauer.gwt.log.client.Log;

public class SolrDocFilter {
	
	private ProjectDAO projectDAO;
	private OrgUnitDAO orgUnitDAO;
	private ContactDAO contactDAO;
	Set<Integer> memberOfProjectIds;
	Set<Integer> orgUnitIds;
	List<Contact> contacts;
	
	
	public SolrDocFilter(Integer userID){
		System.out.println("User Id = " + userID.toString());
		orgUnitIds = orgUnitDAO.getOrgUnitTreeIdsByUserId(userID);
		System.out.println("OrgUnit IDs = " + orgUnitIds.toString());
		memberOfProjectIds = projectDAO.findProjectIdsByTeamMemberIdAndOrgUnitIds(userID, orgUnitIds);
		System.out.println("member of Project IDs = " + memberOfProjectIds.toString());
		//contacts = contactDAO.findByIds(memberOfProjectIds);
	}

	public boolean doFilter(SolrDocument resultDoc){
		if(resultDoc.getFieldValue("doc_type").equals("PROJECT")){
			System.out.println("HEYYYO!");
			Integer projectID = (Integer)resultDoc.getFieldValue("databaseid");
			System.out.println("Found Project ID: " + projectID );
			if(memberOfProjectIds.contains(projectID))return true;
			return false;
		}
		return true;
	}
}
