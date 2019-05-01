package com.animeinjection.weeblist.api.services;

import androidx.annotation.NonNull;
import com.animeinjection.weeblist.api.AnilistRequest;
import com.animeinjection.weeblist.api.AnilistResponse;
import com.animeinjection.weeblist.api.services.IdentityService.IdentityRequest;
import com.animeinjection.weeblist.api.services.IdentityService.IdentityResponse;
import com.animeinjection.weeblist.auth.AuthDataStore;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class IdentityService extends GraphQLService<IdentityRequest, IdentityResponse> {

  @Inject
  public IdentityService(OkHttpClient okHttpClient, AuthDataStore authDataStore) {
    super(okHttpClient, authDataStore, new IdentityResponse.Factory());
  }

  public IdentityRequest newRequest() {
    return new IdentityRequest();
  }

  public static class IdentityRequest implements AnilistRequest {
    private static final String REQUEST =
        "{\n" +
            "  Viewer {\n" +
            "    id\n" +
            "    name\n" +
            "    avatar\n" +
            "  }\n" +
            "}";

    @NonNull
    @Override
    public String buildGraphQLRequestBody() {
      return REQUEST;
    }
  }

  public static class IdentityResponse extends AnilistResponse {

    public String getUserName() {
      return parsedResponse.data.user.name;
    }

    public String getUserId() {
      return String.valueOf(parsedResponse.data.user.id);
    }

    private static class Factory implements AnilistResponse.Factory<IdentityResponse> {
      @Override
      public IdentityResponse newResponseObject() {
        return new IdentityResponse();
      }
    }
  }
}
