package com.animeinjection.weeblist.authorization;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import androidx.annotation.Nullable;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class AuthDataStore {
  private static final String PREFERENCE_FILE_NAME = "auth";

  private static final String ACCESS_TOKEN_KEY = "access_token";
  private static final String TOKEN_TYPE_KEY = "token_type";
  private static final String EXPIRES_AT_TIME_KEY = "expires_at_time";
  private static final String EXPIRES_IN_TIME_KEY = "expires_in";

  @Inject @ApplicationContext Context applicationContext;

  @Inject AuthDataStore() {}

  private SharedPreferences getSharedPrefs() {
    return applicationContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void setAuthorizationData(Map<String, String> authData) {
    String accessToken = authData.get(ACCESS_TOKEN_KEY);
    String tokenType = authData.get(TOKEN_TYPE_KEY);
    String expiresInString = authData.get(EXPIRES_IN_TIME_KEY);
    long expiresIn = Long.parseLong(expiresInString);
    long expiresAt = System.currentTimeMillis() / DateUtils.SECOND_IN_MILLIS + expiresIn;

    getSharedPrefs()
        .edit()
        .putString(ACCESS_TOKEN_KEY, accessToken)
        .putString(TOKEN_TYPE_KEY, tokenType)
        .putLong(EXPIRES_AT_TIME_KEY, expiresAt)
        .apply();
  }

  public boolean hasValidAuth() {
    return !isAuthExpired() && getAccessToken() != null;
  }

  public boolean isAuthExpired() {
    return System.currentTimeMillis() / DateUtils.SECOND_IN_MILLIS
        > getSharedPrefs().getLong(EXPIRES_AT_TIME_KEY, 0);
  }

  public @Nullable String getAccessToken() {
    return getSharedPrefs().getString(ACCESS_TOKEN_KEY, null);
  }

  public String getTokenType() {
    return getSharedPrefs().getString(TOKEN_TYPE_KEY, "Bearer");
  }
}
