package com.animeinjection.weeblist.animelist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.animeinjection.weeblist.R;
import com.animeinjection.weeblist.api.objects.MediaListEntry;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.util.ImageUtils;
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

    return root;
  }

  private void dismiss() {
    if (getActivity() == null) {
      // Already detached, nothing to do
      return;
    }
    getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
  }
}
