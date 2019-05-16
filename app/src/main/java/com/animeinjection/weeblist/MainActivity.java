package com.animeinjection.weeblist;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.animeinjection.weeblist.MainActivity.MainActivityComponent;
import com.animeinjection.weeblist.animelist.AnimeListFragment;
import com.animeinjection.weeblist.animelist.EditListEntryPopupFragment;
import com.animeinjection.weeblist.api.ServiceListener;
import com.animeinjection.weeblist.api.services.IdentityService;
import com.animeinjection.weeblist.api.services.IdentityService.IdentityRequest;
import com.animeinjection.weeblist.auth.AuthDataStore;
import com.animeinjection.weeblist.identity.Identity;
import com.animeinjection.weeblist.identity.IdentityStore;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.injection.HasComponent;
import dagger.Subcomponent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HasComponent<MainActivityComponent> {
  private static final String LOG_TAG = "MainActivity";

  @Inject AuthDataStore authDataStore;
  @Inject IdentityStore identityStore;
  @Inject IdentityService identityService;

  private MainActivityComponent component;
  private View loadingSpinner;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    component().inject(this);

    Uri intentData = getIntent().getData();
    if (intentData != null && intentData.getFragment() != null) {
      String path = intentData.getPath();
      Log.d(LOG_TAG, path);
      Log.d(LOG_TAG, intentData.getFragment());
      String[] queryPairs = intentData.getFragment().split("&");
      Map<String, String> queryData = new HashMap<>();
      for (String pair : queryPairs) {
        String[] kv = pair.split("=");
        queryData.put(kv[0], kv[1]);
        Log.d(LOG_TAG, String.format("%s: %s", kv[0], queryData.get(kv[0])));
      }
      authDataStore.setAuthorizationData(queryData);
    } else {
      Log.d(LOG_TAG, "no intent data!");
    }

    setContentView(R.layout.main_activity);
    loadingSpinner = findViewById(R.id.loading_spinner);

    if (authDataStore.hasValidAuth()) {
      if (identityStore.getIdentity() == null) {
        loadingSpinner.setVisibility(View.VISIBLE);
        IdentityRequest request = identityService.newRequest();
        identityService.sendRequest(request, ServiceListener.from(response -> {
          identityStore.logIn(Identity.create(response.getUserName(), response.getUserId()));
          transitionToAnimeList();
        }, e -> {
          Log.e(LOG_TAG, "identity request failed!", e);
          Toast.makeText(MainActivity.this, "Failed to get user data", Toast.LENGTH_LONG).show();
        }));
      } else {
        transitionToAnimeList();
      }

    } else {
      transitionToAuthorization();
    }
  }

  private void transitionToAnimeList() {
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fragment, new AnimeListFragment())
        .commit();
    loadingSpinner.setVisibility(View.GONE);
  }

  private void transitionToAuthorization() {
    getSupportFragmentManager()
        .beginTransaction()
        .add(R.id.fragment, new OAuthFragment())
        .commit();
  }

  public MainActivityComponent component() {
    if (component == null) {
      component = ComponentFetcher.fromContext(getApplicationContext(), MainActivityComponent.Factory.class)
          .mainActivityComponent();
    }
    return component;
  }

  @Subcomponent
  public interface MainActivityComponent extends
      OAuthFragment.Injector,
      AnimeListFragment.Injector,
      EditListEntryPopupFragment.Injector {
    interface Factory {
      MainActivityComponent mainActivityComponent();
    }

    void inject(MainActivity mainActivity);
  }
}
