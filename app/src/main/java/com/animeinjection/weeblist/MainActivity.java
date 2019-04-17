package com.animeinjection.weeblist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.animeinjection.weeblist.MainActivity.MainActivityComponent;
import com.animeinjection.weeblist.authorization.AuthDataStore;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.injection.HasComponent;
import dagger.Subcomponent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HasComponent<MainActivityComponent> {
  private static final String LOG_TAG = "MainActivity";

  @Inject AuthDataStore authDataStore;

  private MainActivityComponent component;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    component().inject(this);

    Uri intentData = getIntent().getData();
    if (intentData != null) {
      String path = intentData.getPath();
      Log.d(LOG_TAG, path);
      Log.d(LOG_TAG, intentData.getFragment());
      Map<String, String> queryData = new HashMap<>();
      for (String key : intentData.getQueryParameterNames()) {
        queryData.put(key, intentData.getQueryParameter(key));
      }
      authDataStore.setAuthorizationData(queryData);
    } else {
      Log.d(LOG_TAG, "no intent data!");
    }

    setContentView(R.layout.main_activity);

    if (authDataStore.hasValidAuth()) {
      // TODO(alyssa): start the home fragment
    } else {
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.main_activity_frame, new OAuthFragment())
          .commit();
    }
  }

  public MainActivityComponent component() {
    if (component == null) {
      component = ComponentFetcher.fromContext(getApplicationContext(), MainActivityComponent.Factory.class)
          .mainActivityComponent();
    }
    return component;
  }

  @Subcomponent
  public interface MainActivityComponent extends OAuthFragment.Injector {
    interface Factory {
      MainActivityComponent mainActivityComponent();
    }

    void inject(MainActivity mainActivity);
  }
}
