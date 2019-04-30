package com.animeinjection.weeblist.identity;

import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.animeinjection.weeblist.auth.AuthDataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IdentityStore {
  private static final String USER_NAME = "IdentityStore_userName";
  private static final String USER_ID = "IdentityStore_userId";

  @Inject AuthDataStore authDataStore;
  @Inject SharedPreferences sharedPreferences;

  private Identity identity;

  @Inject
  public IdentityStore() {}

  public @Nullable Identity getIdentity() {
    if (identity == null) {
      identity = getStoredIdentity();
    }
    return identity;
  }

  public void logIn(Identity identity) {
    this.identity = identity;
    storeIdentity(identity);
  }

  public void logOut() {
    this.identity = null;
    clearStoredIdentity();
    authDataStore.logOut();
  }

  private void storeIdentity(Identity identity) {
    sharedPreferences.edit()
        .putString(USER_NAME, identity.getUserName())
        .putString(USER_ID, identity.getUserId())
        .apply();
  }

  private Identity getStoredIdentity() {
    String userName = sharedPreferences.getString(USER_NAME, null);
    String userId = sharedPreferences.getString(USER_ID, null);
    if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userId)) {
      return Identity.create(userName, userId);
    } else {
      return null;
    }
  }

  private void clearStoredIdentity() {
    sharedPreferences.edit()
        .remove(USER_NAME)
        .remove(USER_ID)
        .apply();
  }
}
