package com.animeinjection.weeblist.animelist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.animeinjection.weeblist.MainActivity;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.animelist.AnimeListController.AnimeListUpdateErrorEvent;
import com.animeinjection.weeblist.animelist.AnimeListController.AnimeListUpdatedEvent;
import com.animeinjection.weeblist.api.ServiceListener;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.GetAnimeListService;
import com.animeinjection.weeblist.api.services.GetAnimeListService.GetAnimeListRequest;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService.UpdateMediaListEntryRequest;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class AnimeListFragment extends Fragment {
  private static final String LOG_TAG = "AnimeListFragment";

  @Inject AnimeListController animeListController;
  @Inject UpdateMediaListEntryService updateMediaListEntryService;
  @Inject EventBus eventBus;

  private RecyclerView recyclerView;
  private AnimeListAdapter adapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
    eventBus.register(this);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.anime_list_fragment, container, false);
    recyclerView = root.findViewById(R.id.recycler_view);
    adapter = new AnimeListAdapter(getContext(), updateMediaListEntryService);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    animeListController.updateAnimeList();
    return root;
  }

  @Subscribe
  public void handleAnimeListUpdatedEvent(AnimeListUpdatedEvent event) {
    adapter.clearListEntries();
    adapter.addListEntries(animeListController.getAnimeListWithStatus(MediaListStatus.CURRENT));
    adapter.notifyDataSetChanged();
  }

  @Subscribe
  public void handleAnimeListUpdateError(AnimeListUpdateErrorEvent event) {
    transitionToError();
  }

  private void transitionToError() {
  }

  public interface Injector {
    void inject(AnimeListFragment fragment);
  }

  private static class AnimeItemHolder extends RecyclerView.ViewHolder {
    private final UpdateMediaListEntryService updateMediaListEntryService;
    private final TextView titleView;
    private final TextView progressView;
    private final View incrementProgressButton;

    private MediaListEntry mediaListEntry;

    public AnimeItemHolder(View v, UpdateMediaListEntryService updateMediaListEntryService) {
      super(v);
      this.updateMediaListEntryService = updateMediaListEntryService;
      titleView = v.findViewById(R.id.title);
      progressView = v.findViewById(R.id.progress);
      incrementProgressButton = v.findViewById(R.id.plus_button);
    }

    public void bindData(MediaListEntry listEntry) {
      mediaListEntry = listEntry;
      titleView.setText(listEntry.media.title.userPreferred);
      progressView.setText(
          itemView.getContext().getString(
              R.string.progress_format, listEntry.progress, listEntry.media.episodes));
      incrementProgressButton.setOnClickListener(this::onIncrementProgress);
      itemView.setOnClickListener(this::onItemClick);
    }

    private void onIncrementProgress(View progressButton) {
      progressButton.setEnabled(false);
      UpdateMediaListEntryRequest request = updateMediaListEntryService.newRequest();
      request.setMediaListEntryId(mediaListEntry.id);
      request.setProgress(mediaListEntry.progress + 1);
      updateMediaListEntryService.sendRequest(request, ServiceListener.from(response -> {
            if (itemView.getContext() == null) {
              return;
            }
            mediaListEntry.progress = response.getProgress();
            progressView.setText(itemView.getContext().getString(
                R.string.progress_format, mediaListEntry.progress, mediaListEntry.media.episodes));
            progressButton.setEnabled(true);
          },
          e -> {
            Log.e(LOG_TAG, "Failed to update progress", e);
            if (itemView.getContext() == null) {
              return;
            }
            Toast.makeText(itemView.getContext(), "Something went wrong.", Toast.LENGTH_LONG).show();
            progressButton.setEnabled(true);
          }));
    }

    private void onItemClick(View v) {
      EditListEntryPopupFragment.startEditListEntry((MainActivity) v.getContext(), mediaListEntry);
    }
  }

  private static class AnimeListAdapter extends RecyclerView.Adapter<AnimeItemHolder> {

    private final Context context;
    private final UpdateMediaListEntryService updateMediaListEntryService;
    private final List<MediaListEntry> entries = new ArrayList<>();

    public AnimeListAdapter(Context context, UpdateMediaListEntryService updateMediaListEntryService) {
      this.context = context;
      this.updateMediaListEntryService = updateMediaListEntryService;
    }

    @NonNull
    @Override
    public AnimeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(context).inflate(R.layout.anime_item, parent, false);
      return new AnimeItemHolder(v, updateMediaListEntryService);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeItemHolder holder, int position) {
      holder.bindData(entries.get(position));
    }

    public void clearListEntries() {
      entries.clear();
    }

    public void addListEntry(MediaListEntry listEntry) {
      entries.add(listEntry);
    }

    public void addListEntries(List<MediaListEntry> entries) {
      this.entries.addAll(entries);
//      Log.d(LOG_TAG, entries.toString());
    }

    @Override
    public int getItemCount() {
      return entries.size();
    }
  }
}
