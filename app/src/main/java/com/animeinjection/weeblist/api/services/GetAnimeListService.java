package com.animeinjection.weeblist.api.services;

import androidx.annotation.NonNull;
import com.animeinjection.weeblist.api.AnilistRequest;
import com.animeinjection.weeblist.api.AnilistResponse;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListGroup;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.GetAnimeListService.GetAnimeListRequest;
import com.animeinjection.weeblist.api.services.GetAnimeListService.GetAnimeListResponse;
import com.animeinjection.weeblist.auth.AuthDataStore;
import com.animeinjection.weeblist.identity.Identity;
import com.animeinjection.weeblist.identity.IdentityStore;
import com.google.common.collect.ImmutableList;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Singleton
public class GetAnimeListService extends GraphQLService<GetAnimeListRequest, GetAnimeListResponse> {
  private final IdentityStore identityStore;

  @Inject
  public GetAnimeListService(OkHttpClient okHttpClient, AuthDataStore authDataStore, IdentityStore identityStore) {
    super(okHttpClient, authDataStore, new GetAnimeListResponse.Factory());
    this.identityStore = identityStore;
  }

  public GetAnimeListRequest newRequest() {
    return new GetAnimeListRequest(identityStore.getIdentity());
  }

  public static class GetAnimeListRequest extends AnilistRequest {
    private static final String REQUEST_BODY_FORMAT =
        "query { " +
            "MediaListCollection(userId:%s, type:ANIME, sort:SCORE_DESC) { " +
                "lists { " +
                    "entries { " +
                        "id " +
                        "media { " +
                            "title { " +
                                "romaji " +
                                "english " +
                                "native " +
                                "userPreferred " +
                            "} " +
                            "averageScore " +
                            "meanScore " +
                            "genres " +
                            "episodes " +
                            "bannerImage" +
                        "} " +
                        "score " +
                        "status " +
                        "progress " +
                    "} " +
                    "status " +
                "} " +
            "}}";

    private final Identity identity;

    public GetAnimeListRequest(Identity identity) {
      this.identity = identity;
    }

    @NonNull
    @Override
    public String buildGraphQLQuery() {
      return String.format(Locale.US, REQUEST_BODY_FORMAT, identity.getUserId());
    }

    @Override
    protected void validate() {
      // No fields to validate
    }
  }

  public static class GetAnimeListResponse extends AnilistResponse {

    public List<MediaListEntry> getCurrentlyWatching() {
      ImmutableList.Builder<MediaListEntry> listBuilder = ImmutableList.builder();
      for (MediaListGroup group : parsedResponse.data.mediaListCollection.lists) {
        if (group.status == MediaListStatus.CURRENT) {
          listBuilder.addAll(Arrays.asList(group.entries));
        }
      }
      return listBuilder.build();
    }

    public List<MediaListEntry> getAllEntries() {
      ImmutableList.Builder<MediaListEntry> listBuilder = ImmutableList.builder();
      for (MediaListGroup group : parsedResponse.data.mediaListCollection.lists) {
        listBuilder.addAll(Arrays.asList(group.entries));
      }
      return listBuilder.build();
    }

    private static class Factory implements AnilistResponse.Factory<GetAnimeListResponse> {
      @Override
      public GetAnimeListResponse newResponseObject() {
        return new GetAnimeListResponse();
      }
    }
  }
}
