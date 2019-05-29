package com.animeinjection.weeblist.animelist;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.animeinjection.weeblist.api.ServiceListener;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.GetAnimeListService;
import com.animeinjection.weeblist.api.services.GetAnimeListService.GetAnimeListRequest;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;
import com.google.common.collect.Comparators;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Singleton
public class AnimeListController {
  private static final String LOG_TAG = "AnimeListController";

  @Inject GetAnimeListService getAnimeListService;
  @Inject EventBus eventBus;
  @Inject @ApplicationContext Context context;

  private final Map<Integer, MediaListEntry> mediaList = new HashMap<>();

  @Inject
  AnimeListController() {}

  public void updateAnimeList() {
    Log.d(LOG_TAG, "updating anime list");
    GetAnimeListRequest request = getAnimeListService.newRequest();
    getAnimeListService.sendRequest(request, ServiceListener.from(
        response -> {
          Log.d(LOG_TAG, "got success response");
          mediaList.clear();
          for (MediaListEntry entry : response.getAllEntries()) {
            mediaList.put(entry.id, entry);
          }
          eventBus.post(new AnimeListUpdatedEvent());
        },
        error -> {
          Log.d(LOG_TAG, "got error response");
          Toast.makeText(context, "Failed to update anime list", Toast.LENGTH_LONG).show();
          eventBus.post(new AnimeListUpdateErrorEvent());
        }
    ));
  }

  @Nullable
  public MediaListEntry getMediaListEntry(int id) {
    return mediaList.get(id);
  }

  public void updateMediaListEntry(MediaListEntry entry) {
    mediaList.put(entry.id, entry);
    eventBus.post(new AnimeListUpdatedEvent());
  }

  public List<MediaListEntry> getAnimeListForPredicateAndSort(Predicate<MediaListEntry> predicate, Comparator<MediaListEntry> comparator) {
    return mediaList.values().stream().filter(predicate).sorted(comparator).collect(Collectors.toList());
  }

  public List<MediaListEntry> getAnimeListForPredicate(Predicate<MediaListEntry> predicate) {
    return getAnimeListForPredicateAndSort(predicate, (a, b) -> 0);
  }

  public List<MediaListEntry> getAnimeListWithStatus(MediaListStatus status) {
    return getAnimeListForPredicate(entry -> entry.status == status);
  }

  public List<MediaListEntry> getAnimeListWithStatusAndSort(MediaListStatus status, Comparator<MediaListEntry> sort) {
    return getAnimeListForPredicateAndSort(entry -> entry.status == status, sort);
  }

  public static final Comparator<MediaListEntry> LEXICOGRAPHICAL_BY_TITLE =
      (a, b) -> a.media.title.userPreferred.compareTo(b.media.title.userPreferred);

  public static class AnimeListUpdatedEvent {}

  public static class AnimeListUpdateErrorEvent {}
}
