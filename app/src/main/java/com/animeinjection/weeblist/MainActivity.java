package com.animeinjection.weeblist;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.animeinjection.weeblist.MainActivity.MainActivityComponent;
import com.animeinjection.weeblist.injection.ComponentFetcher;
import com.animeinjection.weeblist.injection.HasComponent;
import dagger.Subcomponent;

public class MainActivity extends AppCompatActivity implements HasComponent<MainActivityComponent> {

  private MainActivityComponent component;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    component().inject(this);
    setContentView(R.layout.main_activity);
    getSupportFragmentManager().beginTransaction().add(R.id.main_activity_frame, new OAuthFragment()).commit();
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
