package com.animeinjection.weeblist;

import androidx.appcompat.app.AppCompatActivity;
import com.animeinjection.weeblist.MainActivity.MainActivityComponent;
import com.animeinjection.weeblist.injection.HasComponent;
import dagger.Subcomponent;

public class MainActivity extends AppCompatActivity implements HasComponent<MainActivityComponent> {

  public MainActivityComponent component() {
    return null;
  }

  @Subcomponent
  public interface MainActivityComponent extends OAuthFragment.Injector {
    interface Factory {
      MainActivityComponent mainActivityComponent();
    }

    void inject(MainActivity mainActivity);
  }
}
