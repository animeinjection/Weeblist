package com.animeinjection.weeblist.api.services;

import androidx.annotation.NonNull;
import com.animeinjection.weeblist.api.AnilistRequest;
import com.animeinjection.weeblist.api.AnilistResponse;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListGroup;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.AnimeListService.AnimeListRequest;
import com.animeinjection.weeblist.api.services.AnimeListService.AnimeListResponse;
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
public class AnimeListService extends GraphQLService<AnimeListRequest, AnimeListResponse> {
  private final IdentityStore identityStore;

  @Inject
  public AnimeListService(OkHttpClient okHttpClient, AuthDataStore authDataStore, IdentityStore identityStore) {
    super(okHttpClient, authDataStore, new AnimeListResponse.Factory());
    this.identityStore = identityStore;
  }

  public AnimeListRequest newRequest() {
    return new AnimeListRequest(identityStore.getIdentity());
  }

  public static class AnimeListRequest extends AnilistRequest {
    private static final String REQUEST_BODY_FORMAT =
        "{\\n" +
            "  MediaListCollection(userId:%s, type:ANIME, sort:SCORE_DESC) {\\n" +
            "    lists {\\n" +
            "      entries {\\n" +
            "        media {\\n" +
            "          title {\\n" +
            "            romaji\\n" +
            "            english\\n" +
            "            native\\n" +
            "            userPreferred\\n" +
            "          }\\n" +
            "          averageScore\\n" +
            "          meanScore\\n" +
            "          genres\\n" +
            "        }\\n" +
            "        score\\n" +
            "        status\\n" +
            "      } \\n" +
            "      status " +
            "    }\\n" +
            "  }\\n" +
            "}";

    private final Identity identity;

    public AnimeListRequest(Identity identity) {
      this.identity = identity;
    }

    @NonNull
    @Override
    public String buildGraphQLQuery() {
      return String.format(Locale.US, REQUEST_BODY_FORMAT, identity.getUserId());
    }
  }

  public static class AnimeListResponse extends AnilistResponse {

    public List<MediaListEntry> getCurrentlyWatching() {
      ImmutableList.Builder<MediaListEntry> listBuilder = ImmutableList.builder();
      for (MediaListGroup group : parsedResponse.data.mediaListCollection.lists) {
        if (group.status == MediaListStatus.CURRENT) {
          listBuilder.addAll(Arrays.asList(group.entries));
        }
      }
      return listBuilder.build();
    }

    private static class Factory implements AnilistResponse.Factory<AnimeListResponse> {
      @Override
      public AnimeListResponse newResponseObject() {
        return new AnimeListResponse();
      }
    }
  }
}
