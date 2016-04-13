package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.js.ProfileJS;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class ProfileAsyncDAO extends AbstractUserDatabaseAsyncDAO<ProfileDTO, ProfileJS> {

  @Override
  public Store getRequiredStore() {
    return Store.PROFILE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProfileJS toJavaScriptObject(ProfileDTO t) {
    return ProfileJS.toJavaScript(t);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ProfileDTO toJavaObject(ProfileJS js) {
    return js.toDTO();
  }
}
