package com.animeinjection.weeblist.application;

import android.app.Application;
import android.content.Context;
import com.animeinjection.weeblist.injection.HasComponent;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;

import javax.inject.Inject;

public class WeeblistApplication extends Application implements HasComponent<WeeblistComponent> {

  @Inject
  @ApplicationContext
  Context applicationContext;
  WeeblistComponent component;

  @Override
  public void onCreate() {
    super.onCreate();
    component().inject(this);
  }

  @Override
  public WeeblistComponent component() {
    if (component == null) {
      component = DaggerWeeblistComponent.builder().application(this).build();
    }
    return component;
  }
}
