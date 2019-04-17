package com.animeinjection.weeblist.api;

import com.animeinjection.weeblist.authorization.AuthDataStore;
import okhttp3.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;

@Singleton
public class GraphQLService {

  @Inject OkHttpClient okHttpClient;
  @Inject AuthDataStore authDataStore;

  @Inject
  public GraphQLService() {}

  public void sendRequest(AnilistRequest request) {
    Request.Builder requestBuilder = request.buildRequest();
    requestBuilder.header("Authorization", getAuthorizationHeader());
    okHttpClient.newCall(requestBuilder.build()).enqueue(OkCallback.from(request::onSuccess, request::onFailure));
  }

  public String getAuthorizationHeader() {
    return String.format(Locale.US, "%s %s", authDataStore.getTokenType(), authDataStore.getAccessToken());
  }
}
}
