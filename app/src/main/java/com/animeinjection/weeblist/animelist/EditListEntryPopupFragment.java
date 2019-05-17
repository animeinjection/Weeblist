package com.animeinjection.weeblist.animelist;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.api.objects.MediaListStatus;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.util.ImageUtils;
import com.animeinjection.weeblist.util.AutoCompleteTextViewUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import javax.inject.Inject;

public class EditListEntryPopupFragment extends Fragment {
  public static final String LOG_TAG = "EditListEntryPopupFragment";
  public static final String ARG_MEDIA_LIST_ENTRY = "arg_media_list_entry";

  public static void startEditListEntry(AppCompatActivity activity, MediaListEntry entry) {
    EditListEntryPopupFragment fragment = new EditListEntryPopupFragment();
    Bundle args = new Bundle();
    Gson gson = new Gson();
    String serialized_entry = gson.toJson(entry);
    args.putString(ARG_MEDIA_LIST_ENTRY, serialized_entry);
    fragment.setArguments(args);
    activity.getSupportFragmentManager()
        .beginTransaction()
        .add(android.R.id.content, fragment)
        .commit();
  }

  @Inject ImageUtils imageUtils;

  public interface Injector {
    void inject(EditListEntryPopupFragment fragment);
  }

  private MediaListEntry mediaListEntry;
  private AutoCompleteTextView status;
  private TextView progress;
  private TextInputLayout progressLayout;
  private TextView score;
  private TextInputLayout scoreLayout;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getContext(), Injector.class).inject(this);
    Bundle args = getArguments();
    String serializedListEntry = args == null ? null : args.getString(ARG_MEDIA_LIST_ENTRY, null);
    if (serializedListEntry == null) {
      dismiss();
      return;
    }
    Gson gson = new Gson();
    mediaListEntry = gson.fromJson(serializedListEntry, MediaListEntry.class);
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
    AutoCompleteTextViewUtils.setupAdapterForEnum(getContext(), status, MediaListStatus.class, mediaListEntry.status);

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
    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
  }
}
