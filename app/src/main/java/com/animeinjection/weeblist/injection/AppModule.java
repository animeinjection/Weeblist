package com.animeinjection.weeblist.injection;

import android.content.Context;
import android.content.SharedPreferences;
import com.animeinjection.weeblist.application.WeeblistApplication;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;
import com.animeinjection.weeblist.injection.Qualifiers.Ui;
import com.google.common.eventbus.EventBus;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

@Module(includes = {NetModule.class})
public abstract class AppModule {
  @Provides
  static SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
    return context.getSharedPreferences("weeblist", Context.MODE_PRIVATE);
  }

  @Binds
  @ApplicationContext
  abstract Context provideApplicationContext(@ApplicationContext WeeblistApplication application);

  @Provides
  @Singleton
  static EventBus provideEventBus() {
    return new EventBus();
  }
}
