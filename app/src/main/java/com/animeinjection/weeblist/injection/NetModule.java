package com.animeinjection.weeblist.injection;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

import javax.inject.Singleton;

@Module
public class NetModule {

  @Provides
  @Singleton
  static OkHttpClient provideOkHttpClient() {
    return new OkHttpClient();
  }
}
