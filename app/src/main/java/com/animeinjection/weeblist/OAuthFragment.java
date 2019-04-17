package com.animeinjection.weeblist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.animeinjection.weeblist.injection.ComponentFetcher;

public class OAuthFragment extends Fragment {

  public interface Injector {
    void inject(OAuthFragment fragment);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ComponentFetcher.fromContext(getActivity(), Injector.class).inject(this);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.oauth_fragment, container, false);
  }
}
