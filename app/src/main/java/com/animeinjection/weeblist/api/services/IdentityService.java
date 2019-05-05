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

  public static class IdentityRequest extends AnilistRequest {
    private static final String REQUEST =
        "{\\n" +
            "  Viewer {\\n" +
            "    id\\n" +
            "    name\\n" +
            "    avatar {\\n" +
            "      large\\n" +
            "      medium\\n" +
            "    }\\n" +
            "  }\\n" +
            "}";

    @NonNull
    @Override
    public String buildGraphQLQuery() {
      return REQUEST;
    }

    @Override
    protected void validate() {
      // No fields to validate
    }
  }

  public static class IdentityResponse extends AnilistResponse {

    public String getUserName() {
      return parsedResponse.data.viewer.name;
    }

    public String getUserId() {
      return String.valueOf(parsedResponse.data.viewer.id);
    }

    private static class Factory implements AnilistResponse.Factory<IdentityResponse> {
      @Override
      public IdentityResponse newResponseObject() {
        return new IdentityResponse();
      }
    }
  }
}
