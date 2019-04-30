package com.animeinjection.weeblist.api.services;

import com.animeinjection.weeblist.auth.AuthDataStore;
import okhttp3.OkHttpClient;

public class IdentityService extends GraphQLService {
  protected IdentityService(OkHttpClient okHttpClient, AuthDataStore authDataStore) {
    super(okHttpClient, authDataStore);
  }
}
