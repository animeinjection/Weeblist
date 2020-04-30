package com.animeinjection.weeblist.animelist;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.animeinjection.weeblist.MainActivity;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.api.ServiceListener;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService;
import com.animeinjection.weeblist.api.services.UpdateMediaListEntryService.UpdateMediaListEntryRequest;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.util.ImageUtils;
import com.animeinjection.weeblist.util.AutoCompleteTextViewUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.eventbus.EventBus;

import javax.inject.Inject;

public class EditListEntryPopupFragment extends Fragment {
  private static final String LOG_TAG = "EditListEntryPopupFragment";
  private static final String FRAGMENT_TAG = "edit_list_entry_popup";
  private static final String ARG_MEDIA_LIST_ENTRY_ID = "arg_media_list_entry_id";

  public static void startEditListEntry(MainActivity activity, MediaListEntry entry) {
    EditListEntryPopupFragment fragment = new EditListEntryPopupFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_MEDIA_LIST_ENTRY_ID, entry.id);
    fragment.setArguments(args);
    activity.getSupportFragmentManager()
        .beginTransaction()
        .add(android.R.id.content, fragment)
        .addToBackStack(FRAGMENT_TAG)
        .commit();
  }

  @Inject ImageUtils imageUtils;
  @Inject UpdateMediaListEntryService updateMediaListEntryService;
  @Inject AnimeListController animeListController;
  @Inject EventBus eventBus;

  public interface Injector {
    void inject(EditListEntryPopupFragment fragment);
  }

  private MediaListEntry mediaListEntry;
  private AutoCompleteTextView status;
  private ArrayAdapter<MediaListStatus> statusAdapter;
  private MediaListStatus selectedStatus;
  private TextView progress;
  private TextInputLayout progressLayout;
  private TextView score;
  private TextInputLayout scoreLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
    Bundle args = getArguments();
    mediaListEntry = animeListController.getMediaListEntry(args.getInt(ARG_MEDIA_LIST_ENTRY_ID));
    if (mediaListEntry == null) {
      dismiss();
      return;
    }
    eventBus.register(this);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.edit_list_entry_popup_fragment, container, false);
    root.setOnClickListener(v -> dismiss());

    TextView title = root.findViewById(R.id.title);
    title.setText(mediaListEntry.media.title.userPreferred);

    ImageView banner = root.findViewById(R.id.banner_image);
    imageUtils.loadImage(mediaListEntry.media.bannerImage, banner);

    status = root.findViewById(R.id.status);
    statusAdapter = AutoCompleteTextViewUtils.setupAdapterForEnum(
        getContext(), status, MediaListStatus.class, mediaListEntry.status);

    // No way to get the selected item from the view when the dropdown isn't showing because what the fuck are they even
    // thinking
    status.setOnItemClickListener((parent, view, position, id) -> {
      Log.d(LOG_TAG, "onItemClick");
      selectedStatus = statusAdapter.getItem(position);
    });

    progress = root.findViewById(R.id.progress);
    progressLayout = root.findViewById(R.id.progress_layout);
    progress.setText(String.valueOf(mediaListEntry.progress));

    score = root.findViewById(R.id.score);
    scoreLayout = root.findViewById(R.id.score_layout);
    if (mediaListEntry.score > 0) {
      score.setText(String.valueOf((int) mediaListEntry.score));
    }

    View cancel = root.findViewById(R.id.cancel_button);
    cancel.setOnClickListener(v -> dismiss());

    View save = root.findViewById(R.id.save_button);
    save.setOnClickListener(v -> saveChanges());

    return root;
  }

  private void saveChanges() {
    if (!checkFields()) {
      return;
    }

    UpdateMediaListEntryRequest request = updateMediaListEntryService.newRequest();
    request.setMediaListEntryId(mediaListEntry.id);
    request.setProgress(Integer.valueOf(progress.getText().toString()));
    if (!TextUtils.isEmpty(score.getText())) {
      request.setScore(Integer.valueOf(score.getText().toString()));
    }
    Log.d(LOG_TAG, "selected status is " + selectedStatus);
    if (selectedStatus != null) {
      request.setStatus(selectedStatus);
    }

    updateMediaListEntryService.sendRequest(request, ServiceListener.from(
        response -> {
          mediaListEntry.progress = response.getProgress();
          mediaListEntry.status = response.getStatus();
          mediaListEntry.score = response.getScore();
          animeListController.updateMediaListEntry(mediaListEntry);
          dismiss();
        },
        error -> {
          if (getContext() != null) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
          }
        }
    ));
  }

  private boolean checkFields() {
    boolean wasProgressSuccessful = true;
    try {
      int progressInt = Integer.valueOf(progress.getText().toString());
      if (progressInt < 0 || progressInt > mediaListEntry.media.episodes) {
        wasProgressSuccessful = false;
      }
    } catch (NumberFormatException e) {
      wasProgressSuccessful = false;
    }
    if (!wasProgressSuccessful) {
      progressLayout.setError("Progress should be a number between 0 and " + mediaListEntry.media.episodes);
    } else {
      progressLayout.setError(null);
    }

    boolean wasRatingSuccessful = true;
    try {
      if (!TextUtils.isEmpty(score.getText())) {
        int ratingInt = Integer.valueOf(score.getText().toString());
        if (ratingInt <= 0 || ratingInt > 10) {
          wasRatingSuccessful = false;
        }
      }
    } catch (NumberFormatException e) {
      wasRatingSuccessful = false;
    }
    if (!wasRatingSuccessful) {
      scoreLayout.setError("Score should be a number between 1 and 10");
    } else {
      scoreLayout.setError(null);
    }

    return wasProgressSuccessful && wasRatingSuccessful;
  }

  private void dismiss() {
    if (getActivity() == null) {
      // Already detached, nothing to do
      return;
    }
    getActivity().getSupportFragmentManager().popBackStack(FRAGMENT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
  }
}
