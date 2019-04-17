package com.animeinjection.weeblist.injection;

import android.content.Context;
import android.content.SharedPreferences;
import com.animeinjection.weeblist.application.WeeblistApplication;
import com.animeinjection.weeblist.injection.Qualifiers.ApplicationContext;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {NetModule.class})
public abstract class AppModule {
  @Provides
  static SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
    return context.getSharedPreferences("weeblist", Context.MODE_PRIVATE);
  }

  @Binds
  @ApplicationContext
  abstract Context provideApplicationContext(@ApplicationContext WeeblistApplication application);
}
