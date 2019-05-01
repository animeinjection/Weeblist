package com.animeinjection.weeblist.api.services;

import android.os.Handler;
import com.animeinjection.weeblist.api.AnilistRequest;
import com.animeinjection.weeblist.api.AnilistResponse;
import com.animeinjection.weeblist.api.ServiceListener;
import com.animeinjection.weeblist.auth.AuthDataStore;
import com.animeinjection.weeblist.util.ThreadUtil;
import okhttp3.*;

import java.io.IOException;
import java.util.Locale;

public abstract class GraphQLService<RQ extends AnilistRequest, RS extends AnilistResponse> {
  private static final String SERVICE_ENDPOINT = "https://graphql.anilist.co";

  private final OkHttpClient okHttpClient;
  private final AuthDataStore authDataStore;
  private final AnilistResponse.Factory<RS> responseFactory;
  private final Handler uiThreadHandler = ThreadUtil.getUiThreadHandler();

  protected GraphQLService(OkHttpClient okHttpClient, AuthDataStore authDataStore, AnilistResponse.Factory<RS> responseFactory) {
    this.okHttpClient = okHttpClient;
    this.authDataStore = authDataStore;
    this.responseFactory = responseFactory;
  }

  public void sendRequest(RQ request, ServiceListener<RS> listener) {
    String requestBody = request.buildRequestBody();
    Request.Builder requestBuilder = new Request.Builder();
    requestBuilder.header("Authorization", getAuthorizationHeader())
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .url(SERVICE_ENDPOINT)
        .post(RequestBody.create(MediaType.get("application/json"), requestBody));
    okHttpClient.newCall(requestBuilder.build()).enqueue(wrapListener(listener));
  }

  private String getAuthorizationHeader() {
    return String.format(Locale.US, "%s %s", authDataStore.getTokenType(), authDataStore.getAccessToken());
  }

  private Callback wrapListener(ServiceListener<RS> listener) {
    return new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        uiThreadHandler.post(() -> listener.onError(e));
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        RS responseObject = responseFactory.newResponseObject();
        if (response.body() != null) {
          responseObject.setResponseJson(response.body().string());
        }
        uiThreadHandler.post(() -> listener.onResponse(responseObject));
      }
    };
  }
}
