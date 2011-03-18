package org.sigmah.client.page.admin;

import java.util.Collections;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.HasTab;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateParser;
import org.sigmah.client.page.TabPage;
import org.sigmah.client.page.admin.model.common.AdminOneModelPresenter;
import org.sigmah.client.ui.Tab;

public class AdminPageState implements PageState, TabPage, HasTab {

	private Integer currentSection;
	private Integer model;
	private String subModel;
	private Tab tab;
	
	public AdminPageState() {
		
	} 
	
	public AdminPageState(int section) {
		this.currentSection = section;
	}
	
	public AdminPageState(int section, int model, String subModel) {
		this.currentSection = section;
		this.model = model;
		this.subModel = subModel;
	} 
	


	public static class Parser implements PageStateParser {        
        @Override
        public PageState parse(String token) {
        	final String[] tokens = token.split("/");
        	final AdminPageState state = new AdminPageState(Integer.parseInt(tokens[0]));
        	if(tokens.length > 1) {
                state.setCurrentSection(Integer.parseInt(tokens[1]));
                if(tokens.length > 2) {
                    state.setModel(new Integer(tokens[2]));
                } else {
                    state.setModel(null);
                }
            }
        	return state;
        }
    }
	
	@Override
	public String serializeAsHistoryToken() {
		StringBuilder tokenBuilder = new StringBuilder();

        if(currentSection != null)
            tokenBuilder.append(currentSection.toString());

        if(model != null)
            tokenBuilder.append('/').append(model);
        
        if(subModel != null)
            tokenBuilder.append('/').append(subModel);

        if(tokenBuilder.length() == 0)
            return null;
        else
            return tokenBuilder.toString();
	}
	
	public AdminPageState deriveTo(int section) {
        final AdminPageState derivation = new AdminPageState(section);
        return derivation;
    }
	
	public AdminPageState deriveTo(int section, int model, String subModel) {
        final AdminPageState derivation = new AdminPageState(section);
        derivation.setModel(model);
        derivation.setSubModel(subModel);
        return derivation;
    }

	@Override
	public PageId getPageId() {
		return AdminPresenter.PAGE_ID;
	}

	@Override
	public List<PageId> getEnclosingFrames() {
		return Collections.singletonList(AdminPresenter.PAGE_ID);
	}
	
	public void setCurrentSection(Integer currentSection) {
        this.currentSection = currentSection;
    }
	
	public int getCurrentSection() {
        if(currentSection == null)
            return 0;
        else
            return currentSection;
    }

	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminboard();
	}

	@Override
	public Tab getTab() {
		return tab;
	}

	@Override
	public void setTab(Tab tab) {
		this.tab = tab;		
	}

	public void setModel(Integer model) {
		this.model = model;
	}

	public Integer getModel() {
		return model;
	}

	public void setSubModel(String subModel) {
		this.subModel = subModel;
	}

	public String getSubModel() {
		return subModel;
	}

}
